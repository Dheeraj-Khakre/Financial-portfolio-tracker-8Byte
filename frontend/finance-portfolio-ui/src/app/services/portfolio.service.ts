// src/app/portfolio/portfolio.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { AssetRequest, AssetResponse, PortfolioRequest, PortfolioResponse } from '../models/portfolio.model';
import { MessageResponse } from '../models/jwt-response';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class PortfolioService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private api = `${environment.apiUrl}/portfolios`;

  private headers(): HttpHeaders {
    const token = this.auth.token;
    return new HttpHeaders(
      token ? { Authorization: `Bearer ${token}` } : {}
    );
  }

  getUserPortfolios(): Observable<PortfolioResponse[]> {
    return this.http.get<PortfolioResponse[]>(this.api, {
      headers: this.headers(),
    });
  }

  getPortfolio(id: number): Observable<PortfolioResponse> {
    return this.http.get<PortfolioResponse>(`${this.api}/${id}`, {
      headers: this.headers(),
    });
  }

  createPortfolio(payload: PortfolioRequest): Observable<PortfolioResponse> {
    return this.http.post<PortfolioResponse>(this.api, payload, {
      headers: this.headers(),
    });
  }

  updatePortfolio(
    id: number,
    payload: PortfolioRequest
  ): Observable<PortfolioResponse> {
    return this.http.put<PortfolioResponse>(`${this.api}/${id}`, payload, {
      headers: this.headers(),
    });
  }

  deletePortfolio(id: number): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.api}/${id}`, {
      headers: this.headers(),
    });
  }

  refreshPortfolioPrices(id: number): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(
      `${this.api}/${id}/refresh`,
      {},
      { headers: this.headers() }
    );
  }

  addAsset(portfolioId: number, payload: AssetRequest): Observable<AssetResponse> {
    return this.http.post<AssetResponse>(
      `${this.api}/${portfolioId}/assets`,
      payload,
      { headers: this.headers() }
    );
  }

  updateAsset(
    portfolioId: number,
    assetId: number,
    payload: AssetRequest
  ): Observable<AssetResponse> {
    return this.http.put<AssetResponse>(
      `${this.api}/${portfolioId}/assets/${assetId}`,
      payload,
      { headers: this.headers() }
    );
  }

  removeAsset(
    portfolioId: number,
    assetId: number
  ): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(
      `${this.api}/${portfolioId}/assets/${assetId}`,
      { headers: this.headers() }
    );
  }
}
