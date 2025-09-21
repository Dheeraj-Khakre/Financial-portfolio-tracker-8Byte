// src/app/ai/ai.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { AIInsightResponse } from '../models/ai.models';
import { environment } from '../../environments/environments';

@Injectable({ providedIn: 'root' })
export class AiService {
  private http = inject(HttpClient);
  private auth = inject(AuthService);

  private api = `${environment.apiUrl}/ai`;

  private getAuthOptions(): { headers?: HttpHeaders } {
    const token = this.auth.token;
    if (token) {
      return { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) };
    }
    // no token -> return empty options
    return {};
  }

  getPortfolioInsights(portfolioId: number): Observable<AIInsightResponse> {
    console.log(`Fetching AI insights for portfolio ID: ${portfolioId}`);
    return this.http.get<AIInsightResponse>(`${this.api}/insights/${portfolioId}`, this.getAuthOptions());
  }
}
