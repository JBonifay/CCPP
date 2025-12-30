import {Observable} from 'rxjs';
import {User} from './user';

export const STORAGE_KEYS = {
  accessToken: 'token',
  refreshToken: 'refreshToken',
  user: 'user',
} as const;

export abstract class AuthStrategy {
  abstract login(email: string, password: string): Observable<User>;
  abstract logout(): Observable<void>;
  abstract restore(): User | null;
}
