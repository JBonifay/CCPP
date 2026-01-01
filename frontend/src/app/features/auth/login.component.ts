import {Component, effect, inject} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {PasswordModule} from 'primeng/password';
import {Card} from 'primeng/card';
import {Message} from 'primeng/message';
import {AuthStore} from '../../core';
import {AppRoutePaths} from '../../app.routes';
import {Button} from 'primeng/button';
import {InputText} from 'primeng/inputtext';

@Component({
  selector: 'app-login',
  imports: [PasswordModule, Card, Message, ReactiveFormsModule, Button, InputText],
  template: `
    <div class="flex items-center justify-center flex-1 p-8">
      <div class="w-full max-w-sm">
        <p-card>
          <ng-template #header>
            <div class="text-center pt-6 px-6">
              <h2 class="m-0 mb-2 text-2xl font-semibold">Sign In</h2>
              <p class="text-surface-500 m-0">Welcome back to CCPP</p>
            </div>
          </ng-template>

          @if (authStore.error()) {
            <p-message severity="error" class="w-full mb-4">{{ authStore.error() }}</p-message>
          }

          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-5">
            <div class="flex flex-col gap-2">
              <label for="email" class="font-medium">Email</label>
              <input
                id="email"
                type="email"
                pInputText
                formControlName="email"
                placeholder="Enter your email"
                class="w-full"
                [class.ng-invalid]="isInvalid('email')"
                [class.ng-dirty]="isInvalid('email')"
              />
              @if (isInvalid('email')) {
                <p-message severity="error" size="small" variant="simple">
                  @if (loginForm.get('email')?.hasError('required')) {
                    Email is required.
                  } @else if (loginForm.get('email')?.hasError('email')) {
                    Please enter a valid email.
                  }
                </p-message>
              }
            </div>

            <div class="flex flex-col gap-2">
              <label for="password" class="font-medium">Password</label>
              <p-password
                id="password"
                formControlName="password"
                placeholder="Enter your password"
                [feedback]="false"
                [toggleMask]="true"
                [invalid]="isInvalid('password')"
                fluid
              />
              @if (isInvalid('password')) {
                <p-message severity="error" size="small" variant="simple">
                  Password is required.
                </p-message>
              }
            </div>

            <p-button
              type="submit"
              label="Sign In"
              [loading]="authStore.loading()"
              class="w-full mt-2"
            />
          </form>
        </p-card>
      </div>
    </div>
  `,
})
export class LoginComponent {
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  readonly authStore = inject(AuthStore);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  formSubmitted = false;

  constructor() {
    effect(() => {
      if (this.authStore.isAuthenticated()) {
        void this.router.navigate(AppRoutePaths.selectWorkspace());
      }
    });
  }

  onSubmit(): void {
    this.formSubmitted = true;
    this.authStore.clearError();

    if (this.loginForm.valid) {
      this.authStore.login({
        email: this.loginForm.get('email')?.value,
        password: this.loginForm.get('password')?.value
      });
    }
  }

  isInvalid(controlName: string): boolean {
    const control = this.loginForm.get(controlName);
    return !!(control?.invalid && (control.touched || this.formSubmitted));
  }
}
