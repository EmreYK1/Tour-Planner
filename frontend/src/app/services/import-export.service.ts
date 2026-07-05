// frontend/src/app/services/import-export.service.ts

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ImportExportService {

  // Stellt Daten als Datei-Download bereit
  downloadJson(data: any, filename: string): void {
    const dataStr = JSON.stringify(data, null, 2);
    const blob = new Blob([dataStr], { type: 'application/json' });
    const url = window.URL.createObjectURL(blob);
    
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.click();
    
    window.URL.revokeObjectURL(url);
  }

  // Liest ein File-Objekt ein und gibt ein Observable mit den parsten Daten zurück
  parseJsonFile(file: File): Observable<any> {
    const result$ = new Subject<any>();
    const reader = new FileReader();

    reader.onload = () => {
      try {
        const parsed = JSON.parse(reader.result as string);
        result$.next(parsed);
        result$.complete();
      } catch (e) {
        result$.error(new Error('Die Datei hat kein gültiges JSON-Format.'));
      }
    };

    reader.onerror = () => {
      result$.error(new Error('Fehler beim Lesen der Datei.'));
    };

    reader.readAsText(file);
    return result$.asObservable();
  }
}
