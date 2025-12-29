import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../api/api.service';
import type { AuthStrategy, AuthResult } from './auth.strategy';
import type { User } from './auth.store';

interface LoginResponse {
  token: string;
  refreshToken: string;
}

@Injectable()
export class RealAuthStrategy implements AuthStrategy {
  private readonly api = inject(ApiService);

  async login(email: string, password: string): Promise<AuthResult> {
    try {
      const loginResponse = await firstValueFrom(
        this.api.post<LoginResponse>('/auth/login', { email, password })
      );

      // Store tokens first so the /auth/me call is authenticated
      localStorage.setItem('token', loginResponse.token);
      localStorage.setItem('refreshToken', loginResponse.refreshToken);

      // Fetch user details
      const user = await firstValueFrom(
        this.api.get<User>('/auth/me')
      );

      return {
        success: true,
        user,
        token: loginResponse.token,
      };
    } catch (error: unknown) {
      const message =
        error instanceof Error ? error.message : 'Authentication failed. Please try again.';

      return {
        success: false,
        error: message,
      };
    }
  }

  async logout(): Promise<void> {
    try {
      await firstValueFrom(this.api.post('/auth/logout', {}));
    } catch {
      // Ignore logout errors - we'll clear local state anyway
    }
  }

  async refreshToken(): Promise<string | null> {
    try {
      const response = await firstValueFrom(
        this.api.post<{ token: string }>('/auth/refresh', {})
      );
      return response.token;
    } catch {
      return null;
    }
  }
}
