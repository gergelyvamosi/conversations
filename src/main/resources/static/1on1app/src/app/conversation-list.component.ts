import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from './auth.service';
import { DataService } from './communication.service';
import { Conference, Conversation, User } from './app.model';

@Component({
  selector: 'conversation-list-component',
  template: `
    <div style="overflow-y: auto; height: 400px; width: 90%; border: 1px solid #ccc; padding: 10px; display: flex; flex-direction: column;">
      <h2>1on1s</h2>
      <div *ngIf="getSelectedConference !== null">
      <div>
        <select [(ngModel)]="selectedUserToAddConversationIdB" (change)="fillCreateForm()">
          <option *ngFor="let user of getUsersChatWith()" [value]="user.id">
            {{ user.name }}
          </option>
        </select>
      </div>
      <div *ngIf="getSelectedConversation">
        <textarea [(ngModel)]="newConversationText"></textarea>
        <button (click)="addConversation()">Add</button>
        <button (click)="archiveConversations()">Archive all</button>
      </div>
      <div *ngFor="let conversation of conversations()" style="margin-bottom: 10px;">
        <p>User A: {{ conversation.userA.name }}</p>
        <p>User B: {{ conversation.userB.name }}</p>
        <p>Text: {{ conversation.text }}</p>
        <p>Created: {{ conversation.createdTimestamp }}</p>
        <button *ngIf="conversation.archived !== 'Y'" (click)="deleteConversation(conversation.id)">Delete</button>
        <input *ngIf="conversation.archived !== 'Y'" type="text" [(ngModel)]="conversationTexts[conversation.id]" (blur)="updateConversation(conversation.id)" placeholder="Edit 1on1 text"/>
      </div>
    </div>
    </div>
  `,
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class ConversationListComponent implements OnInit, OnDestroy {
  /*initialConversations: Conversation[] = [
    {
      id: 1,
      conference: { 
          id: 101, 
          title: 'Tech Conference 2024',
          users: [{id: 1, name: 'Alice'}, {id: 2, name: 'Bob'}],
          plannedDate: '2024-12-25',
          description: 'The best tech conference',
          location: 'New York'
      },
      created: '2024-01-15T12:00:00Z',
      userA: { id: 1, name: 'Alice' },
      userB: { id: 2, name: 'Bob' },
      text: 'Hello Bob, how are you?',
      archived: 'N',
    },
    {
      id: 2,
      conference: {
          id: 101,
          title: 'Tech Conference 2024',
          users: [{id: 1, name: 'Alice'}, {id: 2, name: 'Bob'}],
          plannedDate: '2024-12-25',
          description: 'The best tech conference',
          location: 'New York'
      },
      created: '2024-01-15T13:30:00Z',
      userA: { id: 2, name: 'Bob' },
      userB: { id: 1, name: 'Alice' },
      text: 'I am fine, Alice! How about you?',
      archived: 'N',
    },
     {
      id: 3,
      conference: {
          id: 102,
          title: 'Web Dev Summit',
          users: [{id: 3, name: 'Charlie'}],
          plannedDate: '2024-11-20',
          description: 'Learn about web development',
          location: 'San Francisco'
      },
      created: '2024-02-10T10:00:00Z',
      userA: { id: 3, name: 'Charlie' },
      userB: { id: 4, name: 'Dana' },
      text: 'Hi Dana, are you coming to the summit?',
      archived: 'Y',
    },
  ];*/
  conversations = signal<Conversation[]>([]);
  //conversations = signal<Conversation[]>(this.initialConversations);
  interval$: Subscription| undefined;
  destroy$ = new Subject<void>();
  usersForConversation = signal<User[]>([]);
  selectedUserToAddConversationIdB: number | null = null;
  newConversationText: string = "";
  conversationTexts: { [key: number]: string } = {};
  private loginSubscription: Subscription;


  constructor(private http: HttpClient, private authService: AuthService, private dataService: DataService) {
    // Subscribe to login events
    this.loginSubscription = this.authService.login$.subscribe(() => {
      this.loadConversations(); // Refresh the user list
    });
  }

  ngOnInit(): void {
    this.loadConversations();
    //this.interval$ = interval(1000).pipe(takeUntil(this.destroy$)).subscribe(() => {
    //  this.loadConversations();
    //});
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.interval$) { // Added this check
      this.interval$.unsubscribe();
    }
    // Unsubscribe to prevent memory leaks
    this.loginSubscription.unsubscribe();
  }

  loadConversations() {
    this.authService.get<Conversation[]>(this.authService.HTTP_URL + '/conversations/conference/' + this.dataService.selectedConference()!.id).subscribe((data) => {
      this.conversations.set(data);
      /*if(this.dataService.selectedConversation()){
        this.loadUsersForConversation();
      }*/
    });
  }

  loadUsersForConversation() {
    const allUsers = this.conversations().flatMap(c => [c.userA, c.userB]);
    // distinct users
    const distinctUsers = Array.from(new Map(allUsers.map(u => [u.id, u])).values());
    this.usersForConversation.set(distinctUsers);
  }

  deleteConversation(id: number) {
    this.authService.delete<void>(this.authService.HTTP_URL + `/conversations/${id}`).subscribe(() => {
      this.loadConversations();
    });
  }

  updateConversation(id: number) {
  if (this.conversationTexts[id]) {
    this.authService
      .put<Conversation>(`${this.authService.HTTP_URL}/conversations/wrapper/${id}`, JSON.stringify({
        text: this.conversationTexts[id],
      }))
      .subscribe(() => {
        this.loadConversations();
        delete this.conversationTexts[id];
      });
    }
  }

  addConversation() {
    if (/*this.dataService.selectedConversation() && */this.newConversationText.trim() !== '' && this.selectedUserToAddConversationIdB) {
      const newConversation: Omit<Conversation, 'id' | 'createdTimestamp'> = {
        conference: this.getSelectedConference()!,
        userA: this.getOwnUser()!,
        userB: this.getSelectedConference()!.users.find(u => u.id == this.selectedUserToAddConversationIdB)!,
        text: this.newConversationText,
        archived: 'N',
      };

      this.authService.post<Conversation>(this.authService.HTTP_URL + '/conversations/wrapper', JSON.stringify(newConversation)).subscribe(() => {
        //this.loadConversations();
        //this.newConversationText = '';
        //this.selectedUserToAddConversationIdB = null;
      });
      this.authService.notifyLogin();
    }
  }

  archiveConversations() {
    if (this.dataService.selectedConference()) {
      this.authService
        .put<void>(`${this.authService.HTTP_URL}/conversations/archive/${this.dataService.selectedConference()!.id}`, {})
        .subscribe(() => {
          this.loadConversations();
        });
    }
  }

  /*setSelectedConversation(conversation: Conversation) {
      this.dataService.setSelectedConversation(conversation);
      this.loadUsersForConversation();
  }*/

  getSelectedConversation() {
    return this.dataService.selectedConversation();
  }

  getSelectedConference() {
    return this.dataService.selectedConference();
  }

  getUsersChatWith() {
    return this.getSelectedConference()!.users.filter(user => user.name !== this.authService.getAuthenticatedUser());
  }

  getOwnUser() {
    return this.getSelectedConference()!.users.filter(user => user.name == this.authService.getAuthenticatedUser()).at(0);
  }

  fillCreateForm() {
    this.loadConversations();
    if (this.selectedUserToAddConversationIdB) {
    }
  }

}