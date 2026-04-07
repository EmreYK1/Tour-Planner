import type { Tour } from '../models/tour.model';
export declare class TourStateService {
    private readonly tourApi;
    private readonly _tours;
    private readonly _loading;
    private readonly _loadError;
    private readonly _selectedTour;
    readonly tours: import("@angular/core").Signal<Tour[]>;
    readonly loading: import("@angular/core").Signal<boolean>;
    readonly loadError: import("@angular/core").Signal<string | null>;
    readonly selectedTour: import("@angular/core").Signal<Tour | null>;
    private hasLoaded;
    loadTours(): void;
    selectTour(tour: Tour | null): void;
}
