import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap, Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from './auth.service';
import { DataService } from './communication.service';
import { Conference, Conversation, User } from './app.model';

@Component({
  selector: 'user-management-component',
  template: `
    <div style="height: 200px; width: 90%; overflow-y: auto; border: 1px solid #ccc; padding: 10px; display: flex; flex-direction: column;">
      <h2>User Management</h2>
      <div>
        <input type="text" [(ngModel)]="newUserName" placeholder="Username" />
        <button (click)="addUser()">Add User</button>
      </div>
      <h3>Users</h3>
      <div *ngFor="let user of users()" style="margin-bottom: 5px;">
        <span>{{ user.name }} (ID: {{ user.id }})</span>
      </div>
      <div *ngIf="getSelectedUser !== null">
        <h3>Add/Remove Users from 1on1 group: {{ getSelectedUser()?.title }}</h3>
        <select [(ngModel)]="selectedUserToAddId">
          <option *ngFor="let user of users()" [value]="user.id">
            {{ user.name }}
          </option>
        </select>
        <button (click)="addUserToConference()">Add User to 1on1 group</button>
        <select [(ngModel)]="selectedUserToRemoveId">
          <option
            *ngFor="let user of getSelectedUser()?.users"
            [value]="user.id"
          >
            {{ user.name }}
          </option>
        </select>
        <button (click)="removeUserFromConference()">
          Remove User from 1on1 group
        </button>
      </div>
    </div>
  `,
  standalone: true,
  imports: [FormsModule, CommonModule],
  providers: [ /* Add this line */ ],
})
export class UserManagementComponent implements OnInit {
  users = signal<User[]>([]);
  //users = signal<User[]>([{ id: 1, name: 'Alice'}, {id: 2, name: 'Bob'}, {id: 3, name: 'Charlie'}]);
  newUserName = '';
  //selectedConference: Conference | null = null;
  //selectedConference = signal<Conference | null>(null);
  selectedUserToAddId: number | null = null;
  selectedUserToRemoveId: number | null = null;
  private loginSubscription: Subscription;

  constructor(private http: HttpClient, private authService: AuthService, private dataService: DataService) {
    // Subscribe to login events
    this.loginSubscription = this.authService.login$.subscribe(() => {
      this.loadUsers(); // Refresh the user list
    });
  }

  ngOnInit(): void {
    this.loadUsers();
  }

  ngOnDestroy(): void {
    // Unsubscribe to prevent memory leaks
    this.loginSubscription.unsubscribe();
  }

  loadUsers() {
    this.authService.get<User[]>(this.authService.HTTP_URL + '/users').subscribe((data) => {
      this.users.set(data);
    });
  }

  addUser() {
    if (this.newUserName.trim() !== '') {
      this.authService
        .post<User>(this.authService.HTTP_URL + '/users/wrapper', JSON.stringify({ name: this.newUserName }))
        .subscribe(() => {
          this.loadUsers();
          this.newUserName = '';
        });
    }
  }

  addUserToConference() {
    if (
      this.dataService.selectedConference &&
      this.selectedUserToAddId !== null
    ) {
      const id = this.dataService.selectedConference();
      this.authService
        .post<Conference>(
          `${this.authService.HTTP_URL}/conferences/add/${this.dataService.selectedConference()!.id}/users/${this.selectedUserToAddId}`,
          {}
        )
        .subscribe((updatedConference) => {
          this.dataService.selectedConference.set(updatedConference);
          this.authService.notifyLogin();
        });
    }
  }

  removeUserFromConference() {
    if (
      this.dataService.selectedConference &&
      this.selectedUserToRemoveId !== null
    ) {
      this.authService
        .delete<Conference>(
          `${this.authService.HTTP_URL}/conferences/remove/${this.dataService.selectedConference()!.id}/users/${this.selectedUserToRemoveId}`
        )
        .subscribe((updatedConference) => {
          this.dataService.selectedConference.set(updatedConference);
          this.authService.notifyLogin();
        });
    }
  }

  getSelectedUser() {
    return this.dataService.selectedConference();
  }

}