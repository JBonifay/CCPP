import { InjectionToken } from '@angular/core';

export interface AuthResult {
  success: boolean;
  user?: {
    id: string;
    email: string;
    name: string;
  };
  token?: string;
  error?: string;
}

export interface AuthStrategy {
  login(email: string, password: string): Promise<AuthResult>;
  logout(): Promise<void>;
  refreshToken?(): Promise<string | null>;
}

export const AUTH_STRATEGY = new InjectionToken<AuthStrategy>('AUTH_STRATEGY');
