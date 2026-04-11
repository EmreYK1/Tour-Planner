/**
 * Rechnet Sekunden in ein lesbares Format um.
 * Beispiel: 3661 → "1h 01min"
 */
export function formatDuration(seconds: number): string {
  if (!seconds) return '0h 00min';
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  return `${hours}h ${minutes.toString().padStart(2, '0')}min`;
}
