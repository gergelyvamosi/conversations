import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from './auth.service';
import { DataService } from './communication.service';
import { ConversationListComponent } from './conversation-list.component';
import { Conference, Conversation, User } from './app.model';

@Component({
  selector: 'conversation-filter-component',
  template: `
    <div style="height: 200px; width: 90%; overflow-y: auto; border: 1px solid #ccc; padding: 10px; display: flex; flex-direction: column;">
      <h2>Filter 1on1s</h2>
      <div>
        <input type="text" [(ngModel)]="filter.title" placeholder="Title fulltext" />
        <input type="date" [(ngModel)]="filter.startDate" placeholder="Start Date" />
        <input type="date" [(ngModel)]="filter.endDate" placeholder="End Date" />
        <select [(ngModel)]="filter.archived">
          <option value="">All</option>
          <option value="Y">Archived</option>
          <option value="N">Not Archived</option>
        </select><br />
        <button (click)="applyFilter()">Apply Filter</button>
      </div>
      <h3>1on1 heads</h3>
      <div *ngFor="let conversation of filteredConversations()" style="margin-bottom: 10px; cursor: pointer;" (click)="onSelectConversation(conversation)">
          <p>User A: {{conversation.userA.name}}</p>
          <p>User B: {{conversation.userB.name}}</p>
          <p>Text: {{conversation.text}}</p>
      </div>
    </div>
  `,
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class ConversationFilterComponent implements OnInit {
  conversations = signal<Conversation[]>([]);
  filteredConversations = signal<Conversation[]>([]);
  selectedConversation: Conversation | null = null;
  private loginSubscription: Subscription;


  filter: { title: string; startDate: string; endDate: string; archived: string } = {
    title: '',
    startDate: '',
    endDate: '',
    archived: '',
  };

  constructor(private http: HttpClient, private authService: AuthService, private dataService: DataService) {
    // Subscribe to login events
    this.loginSubscription = this.authService.login$.subscribe(() => {
      this.loadConversations(); // Refresh the user list
    });
  }

  ngOnInit(): void {
    this.loadConversations();
  }

  ngOnDestroy(): void {
    // Unsubscribe to prevent memory leaks
    this.loginSubscription.unsubscribe();
  }

  loadConversations() {
    this.authService.get<Conversation[]>(this.authService.HTTP_URL + '/conversations/filter?title=' + this.filter.title + '&startDate=' + this.filter.startDate + '&endDate=' + this.filter.endDate + '&archived=' + this.filter.archived).subscribe((data) => {
      this.conversations.set(data);
      this.filteredConversations.set(data);
    });
  }


  applyFilter() {
    /*let filtered = this.conversations();
    if (this.filter.title) {
      filtered = filtered.filter((c) => c.conference.title.includes(this.filter.title));
    }
    if (this.filter.startDate) {
      const startDate = new Date(this.filter.startDate);
      filtered = filtered.filter((c) => new Date(c.created) >= startDate);
    }
    if (this.filter.endDate) {
      const endDate = new Date(this.filter.endDate);
      filtered = filtered.filter((c) => new Date(c.created) <= endDate);
    }
    if (this.filter.archived) {
      filtered = filtered.filter((c) => c.archived === this.filter.archived);
    }
    this.filteredConversations.set(filtered);*/
    this.loadConversations();
  }

    onSelectConversation(conversation: Conversation) {
        this.dataService.setSelectedConversation(conversation);
        this.selectedConversation = conversation;
    }
}