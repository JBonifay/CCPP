import { inject, Injectable } from '@angular/core';
import { finalize, Observable, switchMap, tap } from 'rxjs';
import { ApiService } from '../api/api.service';
import { AuthStrategy } from './auth.strategy';
import { User } from './user';

interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

@Injectable({ providedIn: 'root' })
export class RealAuthStrategy extends AuthStrategy {
  private readonly api = inject(ApiService);

  login(email: string, password: string): Observable<User> {
    this.clearStorage();
    return this.api.post<AuthTokens>('/auth/login', { email, password }).pipe(
      switchMap(tokens =>
        this.api.get<User>('/auth/me').pipe(
          tap(user => {
            this.persistTokens(tokens);
            this.persistUser(user);
          })
        )
      )
    );
  }

  logout(): Observable<void> {
    return this.api.post<void>('/auth/logout', {}).pipe(
      finalize(() => this.clearStorage())
    );
  }

  override restore(): User | null {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');

    if (!token || !storedUser) {
      return null;
    }

    try {
      return JSON.parse(storedUser) as User;
    } catch {
      this.clearStorage();
      return null;
    }
  }

  private persistTokens(tokens: AuthTokens): void {
    localStorage.setItem('token', tokens.accessToken);
    localStorage.setItem('refreshToken', tokens.refreshToken);
  }

  private persistUser(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  private clearStorage(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
  }
}
