import {Injectable} from '@angular/core';
import {delay, finalize, Observable, of, tap} from 'rxjs';
import {AuthStrategy, STORAGE_KEYS} from './auth.strategy';
import {User} from './user';

@Injectable({providedIn: 'root'})
export class FakeAuthStrategy extends AuthStrategy {

  login(email: string, password: string): Observable<User> {
    const user: User = {
      id: 'fake-user-id',
      email,
      name: email.split('@')[0],
      role: email.includes('admin') ? 'admin' : 'user',
    };

    return of(user).pipe(
      delay(500),
      tap(user => {
        this.persistTokens();
        this.persistUser(user);
      })
    );
  }

  logout(): Observable<void> {
    return of(void 0).pipe(
      delay(100),
      finalize(() => this.clearStorage())
    );
  }

  override restore(): User | null {
    const stored = localStorage.getItem(STORAGE_KEYS.user);
    if (!stored) {
      return null;
    }

    try {
      return JSON.parse(stored) as User;
    } catch {
      this.clearStorage();
      return null;
    }
  }

  private persistTokens(): void {
    localStorage.setItem(STORAGE_KEYS.accessToken, 'fake-jwt-token');
    localStorage.setItem(STORAGE_KEYS.refreshToken, 'fake-refresh-token');
  }

  private persistUser(user: User): void {
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }

  private clearStorage(): void {
    localStorage.removeItem(STORAGE_KEYS.accessToken);
    localStorage.removeItem(STORAGE_KEYS.refreshToken);
    localStorage.removeItem(STORAGE_KEYS.user);
  }
}
