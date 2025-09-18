import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PortfolioFormDialogComponent } from './portfolio-form-dialog.component';

describe('PortfolioFormDialogComponent', () => {
  let component: PortfolioFormDialogComponent;
  let fixture: ComponentFixture<PortfolioFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PortfolioFormDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PortfolioFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
