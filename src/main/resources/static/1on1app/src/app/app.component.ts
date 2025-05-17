import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LoginComponent } from './login.component';
import { UserManagementComponent } from './user-management.component';
import { ConferenceManagementComponent } from './conference-management.component';
import { ConversationFilterComponent } from './conversation-filter.component';
import { ConversationListComponent } from './conversation-list.component';

import { Conference, Conversation, User } from './app.model';

@Component({
  selector: 'app-root',
  template: `
<body>
  <div style="display: flex; flex-wrap: wrap;">
    <div style="flex: 1; min-width: 400px; flex-basis: 33%;">
      <login-component></login-component>
      <conference-management-component (conferenceSelected)="onConferenceSelected($event)"></conference-management-component>
    </div>
    <div style="flex: 1; min-width: 400px; width: 100%; flex-basis: 67%;">
      <user-management-component></user-management-component>
      <conversation-filter-component></conversation-filter-component>
      <conversation-list-component></conversation-list-component>
    </div>
  </div>
</body>
  `,
  standalone: true,
  imports: [
    RouterOutlet,
    LoginComponent,
    UserManagementComponent,
    ConferenceManagementComponent,
    ConversationFilterComponent,
    ConversationListComponent
  ],
  providers: [
    UserManagementComponent,
    ConferenceManagementComponent,
    ConversationFilterComponent,
    ConversationListComponent
  ]
})

export class AppComponent {
  title = '1on1app';

  selectedConference: Conference | null = null;

  onConferenceSelected(conference: Conference) {
    this.selectedConference = conference;
  }
}
