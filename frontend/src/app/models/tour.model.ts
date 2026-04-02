// frontend/src/app/models/tour.model.ts
// Tour-Typen und Konstanten; JSON-Shape wie das Backend (/api/tours).

export const TRANSPORT_TYPES = [
  'WALK',
  'BICYCLE',
  'CAR',
  'PUBLIC_TRANSPORT',
] as const;

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

export function createEmptyTour(): Tour {
  return {
    id: null,
    name: '',
    description: '',
    from: '',
    to: '',
    transportType: 'WALK',
    distance: 0,
    estimatedTime: 0,
    image: '',
  };
}

export const EXAMPLE_WIENERWALD_TOUR: Tour = {
  id: 1,
  name: 'Wienerwald Tour',
  description:
    'Rundtour durch den Wienerwald mit Aussichtspunkten und ruhigen Waldwegen.',
  from: 'Wien Hietzing',
  to: 'Kaltenleutgeben',
  transportType: 'BICYCLE',
  distance: 35.2,
  estimatedTime: 14_400,
  image: '/assets/tours/wienerwald.jpg',
};
