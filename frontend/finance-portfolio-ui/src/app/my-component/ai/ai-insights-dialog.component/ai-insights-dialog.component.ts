import { Component, Inject, OnInit } from '@angular/core';
import { AiDialogData, AIInsightResponse, AssetAllocation } from '../../../models/ai.models';
import { finalize } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AiService } from '../../../services/ai.service';

@Component({
  selector: 'app-ai-insights-dialog.component',
  standalone: false,
  templateUrl: './ai-insights-dialog.component.html',
  styleUrl: './ai-insights-dialog.component.scss'
})
export class AiInsightsDialogComponent implements OnInit {
  portfolioId!: number;
  portfolioName?: string;
  loading = false;
  insights?: AIInsightResponse | null;
  allocationChartData: Array<{ name: string; value: number }> = [];

  constructor(
    private dialogRef: MatDialogRef<AiInsightsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) data: AiDialogData,
    private aiSvc: AiService
  ) {
    this.portfolioId = data.portfolioId;
    this.portfolioName = data.portfolioName;
  }

  ngOnInit(): void {
    this.loadInsights();
  }

  loadInsights(): void {
    this.loading = true;
    this.insights = undefined;
    this.allocationChartData = [];

    this.aiSvc.getPortfolioInsights(this.portfolioId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: res => {
          console.log(res);
          this.insights = res;
          if (res.assetAllocation && res.assetAllocation.length) {
            this.allocationChartData = res.assetAllocation.map((a: AssetAllocation) => ({
              name: a.category,
              value: Number(a.value ?? 0)
            }));
          }
        },
        error: err => {
          console.error(err);
          alert(err?.error?.message || 'Failed to load insights');
          this.dialogRef.close();
        }
      });
  }

  close() {
    this.dialogRef.close();
  }
}
