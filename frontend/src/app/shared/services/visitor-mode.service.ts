import {Injectable, signal} from '@angular/core';

@Injectable({providedIn: 'root'})
export class VisitorModeService {
  private readonly STORAGE_KEY = 'visitor-mode';
  private readonly _isActive = signal(this.loadState());

  readonly isActive = this._isActive.asReadonly();

  toggle(): void {
    this._isActive.update(v => {
      const newValue = !v;
      this.saveState(newValue);
      return newValue;
    });
  }

  activate(): void {
    this._isActive.set(true);
    this.saveState(true);
  }

  deactivate(): void {
    this._isActive.set(false);
    this.saveState(false);
  }

  private loadState(): boolean {
    return localStorage.getItem(this.STORAGE_KEY) === 'true';
  }

  private saveState(value: boolean): void {
    localStorage.setItem(this.STORAGE_KEY, String(value));
  }
}
