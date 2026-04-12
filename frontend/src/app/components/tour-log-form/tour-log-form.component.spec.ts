import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourLogFormComponent } from './tour-log-form.component';

describe('TourLogFormComponent', () => {
  let component: TourLogFormComponent;
  let fixture: ComponentFixture<TourLogFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourLogFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourLogFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
