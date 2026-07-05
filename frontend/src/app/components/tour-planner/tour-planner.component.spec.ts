// frontend/src/app/components/tour-planner/tour-planner.component.spec.ts
// Unit-Tests für die TourPlannerComponent (geschützte Hauptansicht).
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TourPlannerComponent } from './tour-planner.component';
import { TourStateService } from '../../services/tour-state.service';

describe('TourPlannerComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TourPlannerComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the component', () => {
    const fixture = TestBed.createComponent(TourPlannerComponent);
    const component = fixture.componentInstance;
    expect(component).toBeTruthy();
    fixture.detectChanges();
    httpMock.expectOne('/api/tours').flush([]);
    fixture.detectChanges();
  });

  it('should load tours from the backend', () => {
    const fixture = TestBed.createComponent(TourPlannerComponent);
    fixture.detectChanges();

    httpMock.expectOne('/api/tours').flush([
      {
        id: 1,
        name: 'Test',
        description: '',
        from: 'A',
        to: 'B',
        transportType: 'WALK',
        distance: 1,
        estimatedTime: 60,
        image: ''
      }
    ]);
    fixture.detectChanges();

    const tourState = TestBed.inject(TourStateService);
    expect(tourState.tours().length).toBe(1);
    expect(tourState.tours()[0].name).toBe('Test');
  });
});
