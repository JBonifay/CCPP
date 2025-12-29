import { InjectionToken } from '@angular/core';
import type { User } from './auth.store';

export interface AuthResult {
  success: boolean;
  user?: User;
  token?: string;
  error?: string;
}

export interface AuthStrategy {
  login(email: string, password: string): Promise<AuthResult>;
  logout(): Promise<void>;
  refreshToken?(): Promise<string | null>;
}

export const AUTH_STRATEGY = new InjectionToken<AuthStrategy>('AUTH_STRATEGY');
