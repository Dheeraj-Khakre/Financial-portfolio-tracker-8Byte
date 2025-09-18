import { Injectable, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { SignupRequest } from '../models/signup-request';
import { JwtResponse, MessageResponse } from '../models/jwt-response';
import { LoginRequest } from '../models/login-request';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private platformId = inject(PLATFORM_ID);

  private tokenKey = 'jwt-token';
  private api = 'http://localhost:8080/api/auth';

  /** 🔑 logged-in state is only true if we're in the browser and a token exists */
  private _loggedIn = new BehaviorSubject<boolean>(this.hasStoredToken());
  loggedIn$ = this._loggedIn.asObservable();

  /** ---------- API calls ---------- */

  register(payload: SignupRequest): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.api}/signup`, payload);
  }

  login(payload: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.api}/signin`, payload).pipe(
      tap(res => {
        if (isPlatformBrowser(this.platformId)) {
          localStorage.setItem(this.tokenKey, res.accessToken);
        }
        this._loggedIn.next(true);
      })
    );
  }

  logout(): void {
    if (isPlatformBrowser(this.platformId)) {
      localStorage.removeItem(this.tokenKey);
    }
    this._loggedIn.next(false);
  }

  /** ---------- Helpers ---------- */
  get token(): string | null {
    return isPlatformBrowser(this.platformId)
      ? localStorage.getItem(this.tokenKey)
      : null;
  }

  get isLoggedIn(): boolean {
    return this._loggedIn.value;
  }

  /** check safely for SSR */
  private hasStoredToken(): boolean {
    return isPlatformBrowser(this.platformId) && !!localStorage.getItem(this.tokenKey);
  }
}
