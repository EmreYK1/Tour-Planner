import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDetailsComponent } from './tour-details.component';

describe('TourDetailsComponent', () => {
  let component: TourDetailsComponent;
  let fixture: ComponentFixture<TourDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourDetailsComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TourDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
