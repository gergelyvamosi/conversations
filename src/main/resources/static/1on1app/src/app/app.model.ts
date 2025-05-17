export interface User {
  id: number;
  name: string;
}

export interface Conference {
  id: number;
  title: string;
  users: User[];
  plannedDate: string; // ISO String
  description: string;
  location: string;
}

export interface Conversation {
  id: number;
  conference: Conference;
  createdTimestamp: string; // ISO String
  userA: User;
  userB: User;
  text: string;
  archived: string; // 'Y' or 'N'
}