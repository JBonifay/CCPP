import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-public-layout',
  imports: [RouterOutlet, RouterLink, Button],
  template: `
    <div class="public-layout">
      <header class="public-header">
        <a routerLink="/" class="logo">CCPP</a>
        <nav class="nav-actions">
          <a routerLink="/login">
            <p-button label="Sign In" [text]="true" />
          </a>
        </nav>
      </header>
      <main class="public-content">
        <router-outlet />
      </main>
      <footer class="public-footer">
        <p>&copy; 2025 Content Creator Planning Platform</p>
      </footer>
    </div>
  `,
  styles: `
    .public-layout {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    .public-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 1rem 2rem;
      border-bottom: 1px solid var(--p-surface-border);
    }
    .logo {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--p-primary-color);
      text-decoration: none;
    }
    .public-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }
    .public-footer {
      padding: 1rem 2rem;
      text-align: center;
      border-top: 1px solid var(--p-surface-border);
      color: var(--p-text-muted-color);
    }
  `,
})
export class PublicLayoutComponent {}