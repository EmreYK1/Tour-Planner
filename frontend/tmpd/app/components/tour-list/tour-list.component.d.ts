import type { Tour } from '../../models/tour.model';
export declare class TourListComponent {
    private readonly tourState;
    readonly tours: import("@angular/core").Signal<Tour[]>;
    readonly loading: import("@angular/core").Signal<boolean>;
    readonly loadError: import("@angular/core").Signal<string | null>;
    readonly selectedTour: import("@angular/core").Signal<Tour | null>;
    onTourClick(tour: Tour): void;
}
