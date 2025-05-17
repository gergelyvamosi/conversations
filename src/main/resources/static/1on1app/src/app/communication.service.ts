//  Important:  Services are the typical way to share signals between components.
import { Injectable, signal } from '@angular/core';

import { Conference, Conversation, User } from './app.model';

@Injectable({
  providedIn: 'root',
})
export class DataService {
  selectedConference = signal<Conference | null>(null);
  selectedConversation = signal<Conversation | null>(null);

  setSelectedConference(conference: Conference) {
    this.selectedConference.set(conference);
  }

  setSelectedConversation(conversation: Conversation) {
    this.selectedConversation.set(conversation);
  }

}