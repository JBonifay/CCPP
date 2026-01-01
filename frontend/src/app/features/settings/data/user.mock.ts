import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {UserListItem, UserRepository} from './user.repository';
import {UserRole} from '../../../core';

@Injectable({providedIn: 'root'})
export class UserMock implements UserRepository {

  private users: UserListItem[] = [
    {id: '1', email: 'admin@example.com', name: 'Admin User', role: 'ADMIN'},
    {id: '2', email: 'john@example.com', name: 'John Doe', role: 'USER'},
    {id: '3', email: 'jane@example.com', name: 'Jane Smith', role: 'USER'},
    {id: '4', email: 'bob@example.com', name: 'Bob Wilson', role: 'USER'},
  ];

  getAll(): Observable<UserListItem[]> {
    return of(this.users);
  }

  updateRole(userId: string, role: UserRole): Observable<void> {
    const user = this.users.find(u => u.id === userId);
    if (user) {
      user.role = role;
    }
    return of(undefined);
  }
}
