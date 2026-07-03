import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild, Input, OnChanges, SimpleChanges } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-route-map',
  standalone: true,
  imports: [],
  templateUrl: './route-map.component.html',
  styleUrl: './route-map.component.scss'
})
export class RouteMapComponent implements AfterViewInit, OnDestroy, OnChanges {
  @ViewChild('mapContainer', { static: true })
  private readonly mapContainer!: ElementRef<HTMLDivElement>;

  private map?: L.Map;
  private routeLayer?: L.GeoJSON;

  @Input() geometry: string | null = null;

  ngAfterViewInit(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView([48.2082, 16.3738], 12);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);
    this.updateMap();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['geometry'] && this.map) {
      this.updateMap();
    }
  }

  ngOnDestroy(): void {
    this.map?.remove();
  }

  private updateMap(): void {
    if (this.routeLayer) {
      this.routeLayer.remove();
      this.routeLayer = undefined;
    }

    if (this.geometry) {
      const parsedGeo = JSON.parse(this.geometry);
      this.routeLayer = L.geoJSON(parsedGeo).addTo(this.map!);
      this.map!.fitBounds(this.routeLayer!.getBounds());

      const coords = parsedGeo.coordinates;
      const first = coords[0];
      const last  = coords[coords.length - 1];

      L.circleMarker([first[1], first[0]], { radius: 8, color: '#16a34a', fillColor: '#16a34a', fillOpacity: 1 })
        .addTo(this.map!).bindPopup('Start');
      L.circleMarker([last[1], last[0]], { radius: 8, color: '#dc2626', fillColor: '#dc2626', fillOpacity: 1 })
        .addTo(this.map!).bindPopup('Ende');
    } else {
      this.map!.setView([48.2082, 16.3738], 12);
      L.circleMarker([48.2082, 16.3738], { radius: 8, color: '#3b82f6', fillColor: '#3b82f6', fillOpacity: 1 })
        .addTo(this.map!).bindPopup('Wien').openPopup();
    }
  }
}


