import { Component, input } from '@angular/core';
import { ProgressSpinner } from 'primeng/progressspinner';

@Component({
  selector: 'app-loading',
  imports: [ProgressSpinner],
  template: `
    <div class="loading-container">
      <p-progress-spinner [strokeWidth]="strokeWidth()" [style]="{ width: size(), height: size() }" />
      @if (message()) {
        <p class="loading-message">{{ message() }}</p>
      }
    </div>
  `,
  styles: `
    .loading-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 1rem;
    }
    .loading-message {
      color: var(--p-text-muted-color);
    }
  `,
})
export class LoadingComponent {
  readonly message = input<string>();
  readonly size = input('50px');
  readonly strokeWidth = input('4');
}