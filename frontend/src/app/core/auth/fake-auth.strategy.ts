import { Injectable } from '@angular/core';
import type { AuthStrategy, AuthResult } from './auth.strategy';

@Injectable()
export class FakeAuthStrategy implements AuthStrategy {
  async login(email: string, password: string): Promise<AuthResult> {
    // Simulate network delay
    await new Promise((resolve) => setTimeout(resolve, 500));

    // Accept any credentials in dev mode
    console.log('[FakeAuth] Login with:', email);

    return {
      success: true,
      user: {
        id: 'fake-user-id',
        email,
        name: email.split('@')[0],
      },
      token: 'fake-jwt-token-for-development',
    };
  }

  async logout(): Promise<void> {
    console.log('[FakeAuth] Logout');
    await new Promise((resolve) => setTimeout(resolve, 100));
  }
}
