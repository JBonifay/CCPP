import {Observable} from 'rxjs';
import {UserRole} from '../../../core';

export interface UserListItem {
  id: string;
  email: string;
  name: string;
  role: UserRole;
}

export abstract class UserRepository {
  abstract getAll(): Observable<UserListItem[]>;
  abstract updateRole(userId: string, role: UserRole): Observable<void>;
}
