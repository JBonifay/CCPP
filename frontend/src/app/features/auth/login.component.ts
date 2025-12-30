import { Component, effect, inject } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Button } from 'primeng/button';
import { InputText } from 'primeng/inputtext';
import { Password } from 'primeng/password';
import { Card } from 'primeng/card';
import { Message } from 'primeng/message';
import { AuthStore } from '../../core';

@Component({
  selector: 'app-login',
  imports: [FormsModule, Button, InputText, Password, Card, Message],
  template: `
    <div class="login-container">
      <p-card styleClass="login-card">
        <ng-template #header>
          <div class="login-header">
            <h2>Sign In</h2>
            <p>Welcome back to CCPP</p>
          </div>
        </ng-template>

        @if (authStore.error()) {
          <p-message severity="error" [text]="authStore.error()!" styleClass="mb-3 w-full" />
        }

        <form (ngSubmit)="onSubmit()" class="login-form">
          <div class="field">
            <label for="email">Email</label>
            <input
              pInputText
              id="email"
              type="email"
              [(ngModel)]="email"
              name="email"
              placeholder="Enter your email"
              class="w-full"
              required
            />
          </div>
          <div class="field">
            <label for="password">Password</label>
            <p-password
              id="password"
              [(ngModel)]="password"
              name="password"
              placeholder="Enter your password"
              [feedback]="false"
              [toggleMask]="true"
              styleClass="w-full"
              inputStyleClass="w-full"
              required
            />
          </div>
          <p-button
            type="submit"
            label="Sign In"
            [loading]="authStore.loading()"
            styleClass="w-full"
          />
        </form>
      </p-card>
    </div>
  `,
  styles: `
    .login-container {
      display: flex;
      align-items: center;
      justify-content: center;
      flex: 1;
      padding: 2rem;
      background: var(--p-surface-ground);
    }
    :host ::ng-deep .login-card {
      width: 100%;
      max-width: 400px;
    }
    .login-header {
      text-align: center;
      padding: 1.5rem 1.5rem 0;
    }
    .login-header h2 {
      margin: 0 0 0.5rem;
    }
    .login-header p {
      color: var(--p-text-muted-color);
      margin: 0;
    }
    .login-form {
      display: flex;
      flex-direction: column;
      gap: 1.25rem;
    }
    .field {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }
    .field label {
      font-weight: 500;
    }
    .w-full {
      width: 100%;
    }
    .mb-3 {
      margin-bottom: 1rem;
    }
  `,
})
export class LoginComponent {
  private readonly router = inject(Router);
  readonly authStore = inject(AuthStore);

  email = '';
  password = '';

  constructor() {
    effect(() => {
      if (this.authStore.isAuthenticated()) {
        this.router.navigate(['/app']);
      }
    });
  }

  onSubmit(): void {
    this.authStore.clearError();

    if (!this.email || !this.password) {
      return;
    }

    this.authStore.login({ email: this.email, password: this.password });
  }
}
