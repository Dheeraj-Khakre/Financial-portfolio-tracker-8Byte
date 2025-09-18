import { Component, OnInit } from '@angular/core';
import { HistoricalPrice, StockData } from '../../../models/stock.models';
import { ActivatedRoute, Router } from '@angular/router';
import { StockService } from '../../../services/stock.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-asset-detail.component',
  standalone: false,
  templateUrl: './asset-detail.component.html',
  styleUrl: './asset-detail.component.scss'
})
export class AssetDetailComponent implements OnInit {
  symbol!: string;
  loading = false;
  loadingHistory = false;

  stock?: StockData | null;
  history: HistoricalPrice[] = [];
  // ngx-charts expects [{ name: string, series: [{ name: string, value: number }] }]
  lineSeries: Array<{ name: string; series: Array<{ name: string; value: number }> }> = [];

  days = 30; // default

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private stockSvc: StockService
  ) {}

  ngOnInit(): void {
    this.symbol = this.route.snapshot.paramMap.get('symbol') || '';
    if (!this.symbol) {
      // if no symbol, go back
      this.router.navigate(['/dashboard']);
      return;
    }
    this.loadStock();
    this.loadHistory();
  }

  loadStock(): void {
    this.loading = true;
    this.stockSvc.getStockData(this.symbol)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (s) => this.stock = s,
        error: (err) => {
          console.error(err);
          alert('Failed to load stock data');
          this.router.navigate(['/dashboard']);
        }
      });
  }

  loadHistory(days: number = this.days): void {
    this.loadingHistory = true;
    this.stockSvc.getHistoricalPrices(this.symbol, days)
      .pipe(finalize(() => (this.loadingHistory = false)))
      .subscribe({
        next: (hist) => {
          this.history = hist;
          const series = hist.map(h => ({ name: h.date, value: Number(h.price) }));
          this.lineSeries = [{ name: this.symbol, series }];
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load historical data');
          this.lineSeries = [];
        }
      });
  }

  onChangeRange(value: number) {
    this.days = value;
    this.loadHistory(value);
  }

  refreshAll() {
    this.loadStock();
    this.loadHistory(this.days);
  }

  back() {
    this.router.navigate(['/dashboard']);
  }
}
