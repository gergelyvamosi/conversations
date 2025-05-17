import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AuthService } from './auth.service';

@Component({
  selector: 'login-component',
  template: `
    <div style="width: 90%; height: 150px; top: 0; left: 0; border: 1px solid #ccc; padding: 10px;">
      <h2>Login</h2>
      <input type="text" [(ngModel)]="username" placeholder="Username" (keydown.enter)="login()" #nameInput/><label for="username">Admin user is root, enter a valid username.</label><br />
      <button (click)="login()">Login</button><button (click)="logout(nameInput)">Logout</button>
      <p>X-AUTHENTICATED-USER setted to: {{ authService.getAuthenticatedUser() }}</p>
    </div>
  `,
  standalone: true,
  imports: [FormsModule],
})
export class LoginComponent implements OnInit {
  username: string = '';

  constructor(public authService: AuthService) {}

  ngOnInit(): void {
    this.authService.setAuthenticatedUser(this.username);
  }

  login() {
    this.authService.setAuthenticatedUser(this.username);
    this.authService.notifyLogin(); // Notify other components
  }

  logout(nameInput: HTMLInputElement) {
    this.authService.post<String>(this.authService.HTTP_URL + '/logout', '').subscribe(() => {
      // do nothing theoretically :)
    });
    this.username = '';
    this.authService.setAuthenticatedUser(this.username);
    //this.authService.notifyLogin();
    nameInput.value = '';
    window.location.reload();
  }
}