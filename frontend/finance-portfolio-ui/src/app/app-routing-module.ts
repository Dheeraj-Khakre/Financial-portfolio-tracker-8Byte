import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { Login } from './my-component/login/login';
import { Register } from './my-component/register/register';
import { DashboardComponent } from './my-component/dashboard-component/dashboard-component';
import { AssetDetailComponent } from './my-component/stocks/asset-detail.component/asset-detail.component';
import { PortfolioFormComponent } from './my-component/portfolio/portfolio-form.component/portfolio-form.component';
const routes: Routes = [
  { path: 'login', component: Login  },
  { path: 'register', component: Register },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'asset/:symbol', component: AssetDetailComponent },
  { path: 'portfolios/create', component: PortfolioFormComponent },
  { path: 'portfolios/:id/edit', component: PortfolioFormComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
