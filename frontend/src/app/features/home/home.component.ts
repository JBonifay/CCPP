import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Button } from 'primeng/button';

@Component({
  selector: 'app-home',
  imports: [RouterLink, Button],
  template: `
    <section class="hero">
      <div class="hero-content">
        <h1>Content Creator Planning Platform</h1>
        <p class="hero-subtitle">
          Plan your video projects with budget tracking, team collaboration, and timeline management.
        </p>
        <div class="hero-actions">
          <a routerLink="/login">
            <p-button label="Get Started" icon="pi pi-arrow-right" iconPos="right" />
          </a>
        </div>
      </div>
    </section>
    <section class="features">
      <div class="feature">
        <i class="pi pi-wallet feature-icon"></i>
        <h3>Budget Tracking</h3>
        <p>Keep track of your project expenses with detailed budget management.</p>
      </div>
      <div class="feature">
        <i class="pi pi-users feature-icon"></i>
        <h3>Team Collaboration</h3>
        <p>Invite team members and manage project participants effortlessly.</p>
      </div>
      <div class="feature">
        <i class="pi pi-calendar feature-icon"></i>
        <h3>Timeline Management</h3>
        <p>Plan and track your project timeline from start to finish.</p>
      </div>
    </section>
  `,
  styles: `
    :host {
      display: flex;
      flex-direction: column;
      flex: 1;
    }
    .hero {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 4rem 2rem;
      background: linear-gradient(135deg, var(--p-primary-50) 0%, var(--p-surface-ground) 100%);
      flex: 1;
    }
    .hero-content {
      text-align: center;
      max-width: 600px;
    }
    h1 {
      font-size: 2.5rem;
      margin-bottom: 1rem;
      color: var(--p-text-color);
    }
    .hero-subtitle {
      font-size: 1.25rem;
      color: var(--p-text-muted-color);
      margin-bottom: 2rem;
    }
    .hero-actions {
      display: flex;
      justify-content: center;
      gap: 1rem;
    }
    .features {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 2rem;
      padding: 4rem 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }
    .feature {
      text-align: center;
      padding: 2rem;
      border-radius: var(--p-border-radius);
      background: var(--p-surface-card);
      border: 1px solid var(--p-surface-border);
    }
    .feature-icon {
      font-size: 2.5rem;
      color: var(--p-primary-color);
      margin-bottom: 1rem;
    }
    .feature h3 {
      margin-bottom: 0.5rem;
    }
    .feature p {
      color: var(--p-text-muted-color);
    }
  `,
})
export class HomeComponent {}