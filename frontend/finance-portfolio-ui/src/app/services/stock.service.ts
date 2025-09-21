// src/app/stocks/stock.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { HistoricalPrice, StockData } from '../models/stock.models';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class StockService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  private api = `${environment.apiUrl}/stocks`;

  private headers() {
    const token = this.auth.token;
    return token ? { headers: { Authorization: `Bearer ${token}` } } : {};
  }

  getStockData(symbol: string): Observable<StockData> {
    return this.http.get<StockData>(`${this.api}/${symbol}`, this.headers());
  }

  getHistoricalPrices(symbol: string, days = 30): Observable<HistoricalPrice[]> {
    return this.http.get<HistoricalPrice[]>(
      `${this.api}/${symbol}/history?days=${days}`,
      this.headers()
    );
  }
}
