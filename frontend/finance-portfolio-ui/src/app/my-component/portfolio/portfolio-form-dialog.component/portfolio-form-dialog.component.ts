import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PortfolioRequest, PortfolioResponse } from '../../../models/portfolio.model';
import { PortfolioService } from '../../../services/portfolio.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { finalize } from 'rxjs';
export interface PortfolioFormDialogData {
  id?: number; // when provided -> edit mode; else create
}
@Component({
  selector: 'app-portfolio-form-dialog.component',
  standalone: false,
  templateUrl: './portfolio-form-dialog.component.html',
  styleUrl: './portfolio-form-dialog.component.scss'
})
export class PortfolioFormDialogComponent implements OnInit {
  form!: FormGroup;
  saving = false;
  loading = false;
  isEdit = false;
  portfolio?: PortfolioResponse | null;

  constructor(
    private fb: FormBuilder,
    private portfolioSvc: PortfolioService,
    private dialogRef: MatDialogRef<PortfolioFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PortfolioFormDialogData
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]]
    });

    if (this.data?.id) {
      this.isEdit = true;
      this.loadPortfolio(this.data.id);
    }
  }

  private loadPortfolio(id: number) {
    this.loading = true;
    this.portfolioSvc.getPortfolio(id)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: p => {
          this.portfolio = p;
          this.form.patchValue({
            name: p.name,
            description: p.description
          });
        },
        error: err => {
          console.error(err);
          alert('Failed to load portfolio');
          this.dialogRef.close();
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

    if (this.isEdit && this.data.id != null) {
      this.portfolioSvc.updatePortfolio(this.data.id, payload)
        .pipe(finalize(() => (this.saving = false)))
        .subscribe({
          next: updated => this.dialogRef.close({ id: updated.id }),
          error: err => {
            console.error(err);
            alert(err.error?.message || 'Update failed');
          }
        });
    } else {
      this.portfolioSvc.createPortfolio(payload)
        .pipe(finalize(() => (this.saving = false)))
        .subscribe({
          next: created => this.dialogRef.close({ id: created.id }),
          error: err => {
            console.error(err);
            alert(err.error?.message || 'Create failed');
          }
        });
    }
  }

  cancel() {
    this.dialogRef.close();
  }

  deletePortfolio() {
    if (!this.isEdit || this.data.id == null) return;
    if (!confirm('Delete this portfolio? This action cannot be undone.')) return;

    this.saving = true;
    this.portfolioSvc.deletePortfolio(this.data.id)
      .pipe(finalize(() => (this.saving = false)))
      .subscribe({
        next: () => this.dialogRef.close({ deleted: true }),
        error: err => {
          console.error(err);
          alert('Delete failed');
        }
      });
  }
}