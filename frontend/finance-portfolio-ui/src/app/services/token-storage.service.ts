import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class TokenStorageService {
  private TOKEN_KEY = 'portfolio_token';
  private USER_KEY = 'portfolio_user';

  signOut(): void {
    localStorage.clear();
  }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  saveUser(user: any): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  getUser(): any {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }
}
