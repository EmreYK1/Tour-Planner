// frontend/src/app/models/tour.model.ts
// Typen und Konstanten für Touren – spiegeln exakt das JSON-Format der Backend-API wider.

// Alle erlaubten Transportmittel als unveränderliches Tupel (as const = TypeScript kennt die genauen Werte)
export const TRANSPORT_TYPES = [
  'WALK',
  'BICYCLE',
  'CAR',
  'PUBLIC_TRANSPORT',
] as const;

// Abgeleiteter Union-Typ aus dem Array – nur diese vier Strings sind gültig
export type TransportType = (typeof TRANSPORT_TYPES)[number];

// Das zentrale Tour-Interface; id ist null solange die Tour noch nicht gespeichert wurde
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

// Erzeugt eine leere Tour als Startwert – nützlich fürs Formular-Reset
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

// Beispiel-Tour für Entwicklung und manuelle Tests ohne Backend
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
