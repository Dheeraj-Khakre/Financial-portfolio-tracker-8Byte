import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule, provideClientHydration, withEventReplay } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { Login } from './my-component/login/login';
import { Register } from './my-component/register/register';
import { DashboardComponent } from './my-component/dashboard-component/dashboard-component';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
// add these imports at top
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialogModule } from '@angular/material/dialog';
import { AssetDetailComponent } from './my-component/stocks/asset-detail.component/asset-detail.component';
import { AssetDetailDialogComponent } from './my-component/stocks/asset-detail-dialog.component/asset-detail-dialog.component';
import { PortfolioFormComponent } from './my-component/portfolio/portfolio-form.component/portfolio-form.component';
import { PortfolioFormDialogComponent } from './my-component/portfolio/portfolio-form-dialog.component/portfolio-form-dialog.component';
import { AiInsightsDialogComponent } from './my-component/ai/ai-insights-dialog.component/ai-insights-dialog.component';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { MarkdownModule } from 'ngx-markdown';

@NgModule({
  declarations: [
    App,
    Login,
    Register,
    DashboardComponent,
    AssetDetailComponent,
    AssetDetailDialogComponent,
    PortfolioFormComponent,
    PortfolioFormDialogComponent,
    AiInsightsDialogComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
     MatCardModule,
    MatTableModule,
    MatIconModule,
    MatSelectModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    NgxChartsModule,
     MatListModule,
     MatChipsModule,
     MarkdownModule,
  FormsModule
  ],
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideClientHydration(withEventReplay())
  ],
  bootstrap: [App]
})
export class AppModule { }
