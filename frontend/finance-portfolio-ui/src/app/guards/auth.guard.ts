import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { TokenStorageService } from "../services/token-storage.service";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private token: TokenStorageService, private router: Router) {}
  canActivate(): boolean {
    if (this.token.getToken()) { return true; }
    this.router.navigate(['/login']);
    return false;
  }
}
