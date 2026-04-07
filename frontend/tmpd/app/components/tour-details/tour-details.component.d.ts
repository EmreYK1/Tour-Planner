export declare class TourDetailsComponent {
    private readonly tourState;
    private readonly tourApi;
    readonly selectedTour: import("@angular/core").Signal<import("../../models/tour.model").Tour | null>;
    formatDuration(seconds: number): string;
    deleteTour(): void;
}
