import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AiInsightsDialogComponent } from './ai-insights-dialog.component';

describe('AiInsightsDialogComponent', () => {
  let component: AiInsightsDialogComponent;
  let fixture: ComponentFixture<AiInsightsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AiInsightsDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AiInsightsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
