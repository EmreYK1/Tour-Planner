// frontend/src/app/app.component.spec.ts
// Unit-Tests für die AppComponent.
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
    fixture.detectChanges();
    httpMock.expectOne('/api/tours').flush([]);
    fixture.detectChanges();
  });

  it('should load tours from the backend', () => {
    const fixture = TestBed.createComponent(AppComponent);
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

    expect(fixture.componentInstance.tours().length).toBe(1);
    expect(fixture.componentInstance.tours()[0].name).toBe('Test');
  });
});
