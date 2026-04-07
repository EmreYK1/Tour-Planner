export declare const TRANSPORT_TYPES: readonly ["WALK", "BICYCLE", "CAR", "PUBLIC_TRANSPORT"];
export type TransportType = (typeof TRANSPORT_TYPES)[number];
export interface Tour {
    id: number | null;
    name: string;
    description: string;
    from: string;
    to: string;
    transportType: TransportType;
    distance: number;
    estimatedTime: number;
    image: string;
}
export declare function createEmptyTour(): Tour;
export declare const EXAMPLE_WIENERWALD_TOUR: Tour;
