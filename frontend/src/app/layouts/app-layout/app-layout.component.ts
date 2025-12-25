import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { Button } from 'primeng/button';
import { Menu } from 'primeng/menu';
import { Avatar } from 'primeng/avatar';
import { Ripple } from 'primeng/ripple';
import { AuthStore } from '../../core';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, Button, Menu, Avatar, Ripple],
  template: `
    <div class="app-layout">
      <aside class="sidebar" [class.collapsed]="sidebarCollapsed()">
        <div class="sidebar-header">
          <a routerLink="/app" class="logo">
            @if (!sidebarCollapsed()) {
              CCPP
            } @else {
              C
            }
          </a>
        </div>
        <nav class="sidebar-nav">
          <a
            routerLink="/app/projects"
            routerLinkActive="active"
            class="nav-item"
            pRipple
          >
            <i class="pi pi-folder"></i>
            @if (!sidebarCollapsed()) {
              <span>Projects</span>
            }
          </a>
        </nav>
        <div class="sidebar-footer">
          <p-button
            [icon]="sidebarCollapsed() ? 'pi pi-angle-right' : 'pi pi-angle-left'"
            [text]="true"
            (click)="toggleSidebar()"
          />
        </div>
      </aside>
      <div class="main-area">
        <header class="app-header">
          <div class="header-left"></div>
          <div class="header-right">
            <p-avatar
              [label]="authStore.userInitial()"
              shape="circle"
              (click)="userMenu.toggle($event)"
              styleClass="cursor-pointer"
            />
            <p-menu #userMenu [model]="userMenuItems" [popup]="true" />
          </div>
        </header>
        <main class="app-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `,
  styles: `
    .app-layout {
      display: flex;
      min-height: 100vh;
    }
    .sidebar {
      width: 240px;
      background: var(--p-surface-ground);
      border-right: 1px solid var(--p-surface-border);
      display: flex;
      flex-direction: column;
      transition: width 0.2s ease;
    }
    .sidebar.collapsed {
      width: 64px;
    }
    .sidebar-header {
      padding: 1rem;
      border-bottom: 1px solid var(--p-surface-border);
    }
    .logo {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--p-primary-color);
      text-decoration: none;
    }
    .sidebar-nav {
      flex: 1;
      padding: 0.5rem;
    }
    .nav-item {
      display: flex;
      align-items: center;
      gap: 0.75rem;
      padding: 0.75rem 1rem;
      border-radius: var(--p-border-radius);
      color: var(--p-text-color);
      text-decoration: none;
      transition: background 0.2s;
    }
    .nav-item:hover {
      background: var(--p-surface-hover);
    }
    .nav-item.active {
      background: var(--p-primary-color);
      color: var(--p-primary-contrast-color);
    }
    .sidebar-footer {
      padding: 0.5rem;
      border-top: 1px solid var(--p-surface-border);
    }
    .main-area {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }
    .app-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.75rem 1.5rem;
      border-bottom: 1px solid var(--p-surface-border);
      background: var(--p-surface-card);
    }
    .header-right {
      display: flex;
      align-items: center;
      gap: 1rem;
    }
    .app-content {
      flex: 1;
      padding: 1.5rem;
      overflow: auto;
      background: var(--p-surface-ground);
    }
    .cursor-pointer {
      cursor: pointer;
    }
  `,
})
export class AppLayoutComponent {
  readonly authStore = inject(AuthStore);

  readonly sidebarCollapsed = signal(false);

  readonly userMenuItems = [
    {
      label: 'Sign Out',
      icon: 'pi pi-sign-out',
      command: () => this.authStore.logout(),
    },
  ];

  toggleSidebar(): void {
    this.sidebarCollapsed.update((v) => !v);
  }
}