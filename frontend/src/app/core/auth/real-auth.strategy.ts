import { inject, Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { ApiService } from '../api/api.service';
import type { AuthStrategy, AuthResult } from './auth.strategy';

interface LoginResponse {
  id: string;
  email: string;
  name: string;
  token: string;
}

@Injectable()
export class RealAuthStrategy implements AuthStrategy {
  private readonly api = inject(ApiService);

  async login(email: string, password: string): Promise<AuthResult> {
    try {
      const response = await firstValueFrom(
        this.api.post<LoginResponse>('/auth/login', { email, password })
      );

      return {
        success: true,
        user: {
          id: response.id,
          email: response.email,
          name: response.name,
        },
        token: response.token,
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
