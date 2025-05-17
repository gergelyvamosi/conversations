import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect, Output, EventEmitter } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from './auth.service';
import { DataService } from './communication.service';
import { UserManagementComponent } from './user-management.component';
import { Conference, Conversation, User } from './app.model';

@Component({
  selector: 'conference-management-component',
  template: `
    <div style="width: 90%; height: 670px; overflow-y: auto; border: 1px solid #ccc; padding: 10px; display: flex; flex-direction: column;">
      <h2>1on1 groups Management</h2>
      <div>
        <input type="text" [(ngModel)]="newConference.title" placeholder="Title" /><br />
        <input type="date" [(ngModel)]="newConference.plannedDate" placeholder="Planned Date" /><br />
        <input type="text" [(ngModel)]="newConference.location" placeholder="Location" /><br />
        <textarea [(ngModel)]="newConference.description" placeholder="Description"></textarea><br />
        <button (click)="addConference()">Add 1on1 group</button>
      </div>
      <div>
        <h3>Modify 1on1 groups</h3>
        <select [(ngModel)]="selectedConferenceToModifyId" (change)="loadConferenceToModify()">
            <option *ngFor="let conference of conferences()" [value]="conference.id">
            {{ conference.title }}
          </option>
        </select>
        <input type="text" [(ngModel)]="modifiedConference.title" placeholder="Title" /><br />
        <input type="date" [(ngModel)]="modifiedConference.plannedDate" placeholder="Planned Date" /><br />
        <input type="text" [(ngModel)]="modifiedConference.location" placeholder="Location" /><br />
        <textarea [(ngModel)]="modifiedConference.description" placeholder="Description"></textarea><br />
        <button (click)="modifyConference()">Modify 1on1 group</button>
        <button (click)="deleteConference()">Delete 1on1 group</button>
      </div>
      <h3>1on1 groups</h3>
      <div style="margin-bottom: 10px; cursor: pointer;" *ngFor="let conference of conferences()" [ngStyle]="{'background-color': conference.id === getSelectedConference()?.id ? 'lightyellow' : 'transparent'}" (click)="onSelectConference(conference)">
        <span>{{ conference.title }} ({{ conference.plannedDate }})</span>
          <div *ngFor="let user of conference?.users">
            <span>{{ user.name }} (ID: {{ user.id }})</span>
          </div>
      </div>
    </div>
  `,
  standalone: true,
  imports: [FormsModule, CommonModule],
})
export class ConferenceManagementComponent implements OnInit {
  conferences = signal<Conference[]>([]);
  /*conferences = signal<Conference[]>([
    { id: 1, title: 'Tech Conference 2024', users: [{id: 1, name: 'Alice'}, {id: 2, name: 'Bob'}], plannedDate: '2024-12-25', description: 'The best tech conference', location: 'New York'},
    { id: 2, title: 'Web Dev Summit', users: [{id: 3, name: 'Charlie'}], plannedDate: '2024-11-20', description: 'Learn about web development', location: 'San Francisco'},
  ]);*/
  newConference: Conference = { id: 0, title: '', users: [], plannedDate: '', description: '', location: '' };
  selectedConferenceToModifyId: number | null = null;
  modifiedConference: Conference = { id: 0, title: '', users: [], plannedDate: '', description: '', location: '' };
  private loginSubscription: Subscription;
  @Output() conferenceSelected = new EventEmitter<Conference>();

  constructor(private http: HttpClient, private authService: AuthService, private dataService: DataService) {
    // Subscribe to login events
    this.loginSubscription = this.authService.login$.subscribe(() => {
      this.loadConferences(); // Refresh the user list
    });
  }

  ngOnInit(): void {
    this.loadConferences();

  }

  ngOnDestroy(): void {
    // Unsubscribe to prevent memory leaks
    this.loginSubscription.unsubscribe();
  }

  loadConferences() {
    this.authService.get<Conference[]>(this.authService.HTTP_URL + '/conferences').subscribe((data) => {
      this.conferences.set(data);
    });
  }

  addConference() {
    if (this.newConference.title.trim() !== '') {
      this.authService
        .post<Conference>(this.authService.HTTP_URL + '/conferences/wrapper', JSON.stringify(this.newConference))
        .subscribe(() => {
          this.loadConferences();
          this.newConference = { id: 0, title: '', users: [], plannedDate: '', description: '', location: '' };
        });
    }
  }

    loadConferenceToModify() {
    if (this.selectedConferenceToModifyId) {
      const selectedConference = this.conferences().find(c => c.id == this.selectedConferenceToModifyId);
      if (selectedConference) {
        this.modifiedConference = { ...selectedConference };
      }
    }
  }

  modifyConference() {
    if (this.selectedConferenceToModifyId && this.modifiedConference.title.trim() !== '') {
      this.authService
        .put<Conference>(`${this.authService.HTTP_URL}/conferences/wrapper/${this.selectedConferenceToModifyId}`, JSON.stringify(this.modifiedConference))
        .subscribe(() => {
          this.loadConferences();
          this.modifiedConference = { id: 0, title: '', users: [], plannedDate: '', description: '', location: '' };
          this.selectedConferenceToModifyId = null;
        });
    }
  }

  deleteConference() {
    if (this.selectedConferenceToModifyId) {
      this.authService.delete<void>(`${this.authService.HTTP_URL}/conferences/${this.selectedConferenceToModifyId}`).subscribe(() => {
        this.loadConferences();
        this.selectedConferenceToModifyId = null;
        this.modifiedConference = { id: 0, title: '', users: [], plannedDate: '', description: '', location: '' };
      });
    }
  }

  onSelectConference(conference: Conference) {
    //this.selectedConference = conference;
    this.dataService.setSelectedConference(conference);
    this.conferenceSelected.emit(conference); // Emit the selected conference

    this.authService.notifyLogin();
  }

  getSelectedConference() {
    return this.dataService.selectedConference();
  }
}