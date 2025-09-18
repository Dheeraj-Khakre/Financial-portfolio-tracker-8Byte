import { Component, signal } from '@angular/core';
import { TokenStorageService } from './services/token-storage.service';
import { Router } from '@angular/router';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  standalone: false,
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('finance-portfolio-ui');

  constructor(
    private tokenStorage: TokenStorageService,
    private router: Router,
    public auth: AuthService
  ) {}

  isLoggedIn(): boolean {
    return !!this.tokenStorage.getToken();
  }

  logout(): void {
     this.auth.logout();
    this.router.navigate(['/login']);
  }
}

