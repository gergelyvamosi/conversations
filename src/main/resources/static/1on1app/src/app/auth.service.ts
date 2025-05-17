import { Component, OnInit, OnDestroy, Injectable, signal, computed, effect } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, interval, Subject, takeUntil, tap, switchMap, catchError, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authenticatedUser = signal<string>(""); // Default value
  private errorMessage = signal<string | null>(null);

  // Add a shared Subject for login events
  private loginSubject = new Subject<void>();
  login$ = this.loginSubject.asObservable();
  public HTTP_URL = "http://localhost:8080";

  constructor(private http: HttpClient) {}

  getAuthenticatedUser() {
    return this.authenticatedUser();
  }

  setAuthenticatedUser(username: string) {
    this.authenticatedUser.set(username);
  }

  getErrorMessage() {
    return this.errorMessage();
  }

  clearErrorMessage() {
    this.errorMessage.set(null);
  }

  // Notify components about login
  notifyLogin() {
    this.loginSubject.next();
  }

  // --- Helper Methods for HTTP ---
  private getHttpHeaders() {
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'X-Authenticated-User': this.authenticatedUser(),
    });
  }

  // workaround for >>content-type 'application/json;charset=utf-8' is not supported.<< bug with @JsonManagedReference
  private getHttpHeadersWrapper() {
    return new HttpHeaders({
      'Content-Type': 'text/plain',
      'X-Authenticated-User': this.authenticatedUser(),
    });
  }

  public get<T>(url: string): Observable<T> {
    this.clearErrorMessage();
    return this.http.get<T>(url, { headers: this.getHttpHeaders() }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  public post<T>(url: string, body: any): Observable<T> {
    this.clearErrorMessage();
    return this.http.post<T>(url, body, { headers: this.getHttpHeadersWrapper() }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  public put<T>(url: string, body: any): Observable<T> {
    this.clearErrorMessage();
    return this.http.put<T>(url, body, { headers: this.getHttpHeadersWrapper() }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  public delete<T>(url: string): Observable<T> {
    this.clearErrorMessage();
    return this.http.delete<T>(url, { headers: this.getHttpHeaders() }).pipe(
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let message = 'An unknown error occurred';
    if (error.error instanceof ErrorEvent) {
      // Client-side errors
      message = `Error: ${error.error.message}`;
    } else {
      // Server-side errors
      message = `Server returned code ${error.status}, body was: ${error.error.message || error.error}`;
    }
    this.errorMessage.set(message);
    return throwError(() => new Error(message));
  }
}