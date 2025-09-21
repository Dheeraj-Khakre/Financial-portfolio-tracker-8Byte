import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AssetResponse, PortfolioResponse } from '../../models/portfolio.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PortfolioService } from '../../services/portfolio.service';
import { StockService } from '../../services/stock.service';
import { finalize } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { AssetDetailDialogComponent } from '../stocks/asset-detail-dialog.component/asset-detail-dialog.component';
import { PortfolioFormDialogComponent } from '../portfolio/portfolio-form-dialog.component/portfolio-form-dialog.component';
import { AiInsightsDialogComponent } from '../ai/ai-insights-dialog.component/ai-insights-dialog.component';

@Component({
  selector: 'app-dashboard-component',
  standalone: false,
  templateUrl: './dashboard-component.html',
  styleUrl: './dashboard-component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  portfolios: PortfolioResponse[] = [];
  selectedPortfolio?: PortfolioResponse | null;
  assets: AssetResponse[] = [];
  loading = false;
  actionInProgress = false;

  // chart data
  allocationData: Array<{ name: string; value: number }> = [];
  lineSeries: Array<{ name: string; series: Array<{ name: string; value: number }> }> = [];
  selectedAsset?: AssetResponse | null;
  addForm!: FormGroup;

  displayedColumns = ['ticker', 'company_name', 'quantity', 'currentPrice', 'totalValue', 'actions'];

  constructor(
    private fb: FormBuilder,
    private portfolioSvc: PortfolioService,
    private stockSvc: StockService,
    private dialog: MatDialog
  ) {
    // Initialize the form here — FormBuilder is safe to use now
    this.addForm = this.fb.group({
      tickerSymbol: ['', [Validators.required, Validators.maxLength(10)]],
      quantity: [1, [Validators.required, Validators.min(0.0001)]],
      purchasePrice: [0, [Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    this.loadPortfolios();
  }

  loadPortfolios() {
    this.loading = true;
    this.portfolioSvc.getUserPortfolios()
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (res) => {
          this.portfolios = res;
          if (res.length) {
            this.selectPortfolio(res[0].id);
          } else {
            this.selectedPortfolio = null;
            this.assets = [];
            this.updateAllocation();
          }
        },
        error: (err) => {
          console.error(err);
          this.portfolios = [];
        }
      });
  }

  selectPortfolio(id: number) {
    this.selectedAsset = null;
    this.loading = true;
    this.portfolioSvc.getPortfolio(id)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (p) => {
          this.selectedPortfolio = p;
          this.assets = p.assets || [];
          this.updateAllocation();
        },
        error: (err) => {
          console.error(err);
        }
      });
  }

  addAsset() {
    if (this.addForm.invalid || !this.selectedPortfolio) return;
    this.actionInProgress = true;

    const payload = {
      tickerSymbol: (this.addForm.value.tickerSymbol || '').toUpperCase(),
      quantity: Number(this.addForm.value.quantity),
      purchasePrice: Number(this.addForm.value.purchasePrice)
    };

    this.portfolioSvc.addAsset(this.selectedPortfolio.id, payload)
      .pipe(finalize(() => this.actionInProgress = false))
      .subscribe({
        next: (asset) => {
          this.assets = [...this.assets, asset];
          this.updateAllocation();
          this.addForm.reset({ tickerSymbol: '', quantity: 1, purchasePrice: 0 });
        },
        error: (err) => {
          console.error(err);
          alert(err.error?.message || 'Failed to add asset');
        }
      });
  }

  removeAsset(assetId: number) {
    if (!this.selectedPortfolio || !confirm('Remove this asset?')) return;
    this.actionInProgress = true;
    this.portfolioSvc.removeAsset(this.selectedPortfolio.id, assetId)
      .pipe(finalize(() => this.actionInProgress = false))
      .subscribe({
        next: () => {
          this.assets = this.assets.filter(a => a.id !== assetId);
          this.updateAllocation();
        },
        error: (err) => {
          console.error(err);
          alert('Failed to remove asset');
        }
      });
  }

  refreshPrices() {
    if (!this.selectedPortfolio) return;
    this.actionInProgress = true;
    this.portfolioSvc.refreshPortfolioPrices(this.selectedPortfolio.id)
      .pipe(finalize(() => this.actionInProgress = false))
      .subscribe({
        next: () => this.selectPortfolio(this.selectedPortfolio!.id),
        error: (err) => {
          console.error(err);
          alert('Failed to refresh prices');
        }
      });
  }

  updateAllocation() {
    this.allocationData = (this.assets || []).map(a => ({
      name: a.tickerSymbol,
      value: Number(a.totalValue ?? (a.currentPrice * a.quantity))
    }));
    this.allocationData.sort((x, y) => y.value - x.value);
  }

  onSelectAsset(asset: AssetResponse) {
    this.selectedAsset = asset;
    this.stockSvc.getHistoricalPrices(asset.tickerSymbol, 30).subscribe({
      next: (hist) => {
        const series = hist.map(h => ({ name: h.date, value: Number(h.price) }));
        this.lineSeries = [{ name: asset.tickerSymbol, series }];
      },
      error: (err) => {
        console.error(err);
        this.lineSeries = [];
      }
    });
  }

  // add method
  openAssetDialog(symbol: string) {
    this.dialog.open(AssetDetailDialogComponent, {
      data: { symbol },
      width: '900px',
      maxHeight: '80vh'
    });
  }
  openPortfolioDialog(id?: number) {
    const ref = this.dialog.open(PortfolioFormDialogComponent, {
      data: { id },
      width: '720px',
      maxHeight: '80vh'
    });

    ref.afterClosed().subscribe(result => {
      if (result?.id) {
        this.loadPortfolios(); // we reload; after loadPortfolios will select first by default
        setTimeout(() => {
          const found = this.portfolios.find(p => p.id === result.id);
          if (found) this.selectPortfolio(found.id);
        }, 400);
      } else if (result?.deleted) {
        this.loadPortfolios();
      } else {
        this.loadPortfolios();
      }
    });
  }

  openAiInsights() {
    if (!this.selectedPortfolio) return;

    const ref = this.dialog.open(AiInsightsDialogComponent, {
      data: {
        portfolioId: this.selectedPortfolio.id,
        portfolioName: this.selectedPortfolio.name
      },
      width: '1500px',
      maxHeight: '80vh'
    });

    ref.afterClosed().subscribe(() => {
    });
  }

}
