import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {UserListItem, UserRepository} from './user.repository';
import {UserRole} from '../../../core';

@Injectable({providedIn: 'root'})
export class UserApi implements UserRepository {

  private http = inject(HttpClient);

  getAll(): Observable<UserListItem[]> {
    return this.http.get<UserListItem[]>('/users');
  }

  updateRole(userId: string, role: UserRole): Observable<void> {
    return this.http.patch<void>(`/users/${userId}/role`, {role});
  }
}
