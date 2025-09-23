// src/app/ai/ai.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
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

getAiChatResponse(query: string): Observable<string> {
  console.log(`Sending message to AI chat: ${query}`);

  const params = new HttpParams().set('q', query);

  return this.http.get<string>(`${this.api}/chat`, {
    params,
    headers: this.getAuthOptions().headers, // assuming getAuthOptions returns { headers }
    responseType: 'text' as 'json'          // tell Angular we expect plain text
  });
}
}
