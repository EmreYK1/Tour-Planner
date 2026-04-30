import { AfterViewInit, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-route-map',
  standalone: true,
  imports: [],
  templateUrl: './route-map.component.html',
  styleUrl: './route-map.component.scss'
})
export class RouteMapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapContainer', { static: true} )
  private readonly mapContainer!: ElementRef<HTMLDivElement>;

  private map?: L.Map;

  ngAfterViewInit(): void {
    this.map = L.map(this.mapContainer.nativeElement).setView([48.2082, 16.3738], 12);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap'
  }).addTo(this.map);

  L.marker([48.2082, 16.3738])
  .addTo(this.map)
  .bindPopup('Wien')
  .openPopup();
  }

  ngOnDestroy(): void {
  this.map?.remove();
  }

}
