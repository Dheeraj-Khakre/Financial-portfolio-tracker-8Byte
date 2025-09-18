import { Component, Inject, OnInit } from '@angular/core';
import { AssetDetailDialogData, StockData } from '../../../models/stock.models';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { StockService } from '../../../services/stock.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-asset-detail-dialog.component',
  standalone: false,
  templateUrl: './asset-detail-dialog.component.html',
  styleUrl: './asset-detail-dialog.component.scss'
})
export class AssetDetailDialogComponent implements OnInit {
  symbol: string;
  loading = false;
  loadingHistory = false;
  stock?: StockData | null;
  lineSeries: Array<{ name: string; series: Array<{ name: string; value: number }> }> = [];
  days = 30;

  constructor(
    private dialogRef: MatDialogRef<AssetDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) data: AssetDetailDialogData,
    private stockSvc: StockService
  ) {
    this.symbol = data.symbol;
  }

  ngOnInit(): void {
    this.loadStock();
    this.loadHistory(this.days);
  }

  loadStock(): void {
    this.loading = true;
    this.stockSvc.getStockData(this.symbol)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: s => this.stock = s,
        error: err => {
          console.error(err);
          this.dialogRef.close();
        }
      });
  }

  loadHistory(days: number): void {
    this.loadingHistory = true;
    this.stockSvc.getHistoricalPrices(this.symbol, days)
      .pipe(finalize(() => (this.loadingHistory = false)))
      .subscribe({
        next: hist => {
          const series = hist.map(h => ({ name: h.date, value: Number(h.price) }));
          this.lineSeries = [{ name: this.symbol, series }];
        },
        error: err => {
          console.error(err);
          this.lineSeries = [];
        }
      });
  }

  changeRange(days: number) {
    this.days = days;
    this.loadHistory(days);
  }

  close() {
    this.dialogRef.close();
  }
}
