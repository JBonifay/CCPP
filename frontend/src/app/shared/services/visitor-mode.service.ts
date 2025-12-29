import {Injectable, signal} from '@angular/core';

@Injectable({providedIn: 'root'})
export class VisitorModeService {
  private readonly _isActive = signal(false);

  readonly isActive = this._isActive.asReadonly();

  toggle(): void {
    this._isActive.update(v => !v);
  }

  activate(): void {
    this._isActive.set(true);
  }

  deactivate(): void {
    this._isActive.set(false);
  }
}
