import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PortfolioRequest, PortfolioResponse } from '../../../models/portfolio.model';
import { ActivatedRoute, Router } from '@angular/router';
import { PortfolioService } from '../../../services/portfolio.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-portfolio-form.component',
  standalone: false,
  templateUrl: './portfolio-form.component.html',
  styleUrl: './portfolio-form.component.scss'
})
export class PortfolioFormComponent implements OnInit {
  form!: FormGroup;
  saving = false;
  loading = false;
  isEdit = false;
  portfolioId?: number;
  portfolio?: PortfolioResponse | null;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private portfolioSvc: PortfolioService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]]
    });

    // check route for id
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEdit = true;
      this.portfolioId = Number(idParam);
      this.loadPortfolio(this.portfolioId);
    }
  }

  private loadPortfolio(id: number) {
    this.loading = true;
    this.portfolioSvc.getPortfolio(id)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (p) => {
          this.portfolio = p;
          this.form.patchValue({
            name: p.name,
            description: p.description
          });
        },
        error: (err) => {
          console.error(err);
          alert('Failed to load portfolio');
          this.router.navigate(['/dashboard']);
        }
      });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const payload: PortfolioRequest = {
      name: this.form.value.name,
      description: this.form.value.description
    };

    this.saving = true;

    if (this.isEdit && this.portfolioId != null) {
      this.portfolioSvc.updatePortfolio(this.portfolioId, payload)
        .pipe(finalize(() => (this.saving = false)))
        .subscribe({
          next: (updated) => {
            alert('Portfolio updated');
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error(err);
            alert(err.error?.message || 'Update failed');
          }
        });
    } else {
      this.portfolioSvc.createPortfolio(payload)
        .pipe(finalize(() => (this.saving = false)))
        .subscribe({
          next: (created) => {
            alert('Portfolio created');
            // navigate to dashboard and select the portfolio (dashboard selects first by default)
            this.router.navigate(['/dashboard']);
          },
          error: (err) => {
            console.error(err);
            alert(err.error?.message || 'Create failed');
          }
        });
    }
  }

  deletePortfolio() {
    if (!this.isEdit || this.portfolioId == null) return;
    if (!confirm('Delete this portfolio? This action cannot be undone.')) return;

    this.saving = true;
    this.portfolioSvc.deletePortfolio(this.portfolioId)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => {
          alert('Portfolio deleted');
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          console.error(err);
          alert('Delete failed');
        }
      });
  }

  cancel() {
    this.router.navigate(['/dashboard']);
  }
}
