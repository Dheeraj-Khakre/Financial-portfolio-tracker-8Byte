// src/app/stocks/stock.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { HistoricalPrice, StockData } from '../models/stock.models';

@Injectable({ providedIn: 'root' })
export class StockService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private api = 'http://localhost:8080/api/stocks';

  /** Helper to add Authorization header if token exists */
  private headers() {
    const token = this.auth.token;
    return token ? { headers: { Authorization: `Bearer ${token}` } } : {};
  }

  /** Get latest data for a symbol */
  getStockData(symbol: string): Observable<StockData> {
    return this.http.get<StockData>(`${this.api}/${symbol}`, this.headers());
  }

  /** Get historical prices for the past `days` (default 30) */
  getHistoricalPrices(symbol: string, days = 30): Observable<HistoricalPrice[]> {
    return this.http.get<HistoricalPrice[]>(
      `${this.api}/${symbol}/history?days=${days}`,
      this.headers()
    );
  }
}
