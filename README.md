# Tour Planner

Projektarbeit von Emre Yüksel und Shez Soltani im Rahmen des SWE2-Kurses.

Die Idee: eine Web-App mit der man Outdoor-Touren planen und nachträglich dokumentieren kann. Du gibst Start und Ziel ein, wählst ein Transportmittel – der Rest (Route, Distanz, Dauer) wird automatisch über OpenRouteService berechnet. Absolvierte Touren kann man danach mit Logs versehen, die dann die Beliebtheit und Kinderfreundlichkeit einer Tour beeinflussen.

**Stack:** Angular 18 · Spring Boot 3 / Java 21 · PostgreSQL · Docker

---

## Features

Die App hat eine eigene Benutzerverwaltung – jeder Nutzer sieht nur seine eigenen Touren. Authentifizierung läuft über JWT, der Token wird automatisch an alle API-Anfragen angehängt.

Für Touren gibt es komplettes CRUD. Beim Anlegen oder Bearbeiten einer Tour ruft das Backend automatisch die ORS-API auf, löst die eingegebenen Adressen per Geocoding in Koordinaten auf und berechnet Route, Distanz und Dauer. Die resultierende Route wird auf einer Leaflet-Karte in der Detailansicht angezeigt.

Die Tourliste hat eine Suchleiste – getippt wird direkt gefiltert (300ms Debounce), gesucht wird über Name, Beschreibung, Start, Ziel und Transportmittel.

In der Detailansicht tauchen zwei berechnete Badges auf: Beliebtheit (wie viele Logs hat die Tour) und Kinderfreundlichkeit – beides wird serverseitig aus den vorhandenen Logs berechnet und farblich codiert angezeigt (grün/gelb/rot).

Touren lassen sich komplett (inkl. aller Logs) als JSON exportieren und wieder importieren.

## App starten

Am einfachsten per Docker:

```bash
docker compose up --build
```

Frontend läuft dann auf [http://localhost:4200](http://localhost:4200), Backend auf [http://localhost:8080](http://localhost:8080).

Zum Beenden: `docker compose down`. Mit `-v` wird auch das Datenbank-Volume gelöscht (sauberer Neustart).

Voraussetzung: Docker Desktop (Windows/Mac mit WSL2) oder Docker Engine + Compose (Linux).

Für die Routenberechnung und das Geocoding wird ein API-Key von [openrouteservice.org](https://openrouteservice.org) benötigt – als Umgebungsvariable `ORS_API_KEY`, z.B. in einer `.env`-Datei im Projektordner.

**Manuell (ohne Docker)**

Backend braucht JDK 21 und Maven:

```bash
cd backend

# lokale H2-Datenbank, kein PostgreSQL nötig:
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# mit PostgreSQL:
mvn spring-boot:run
```

Frontend:

```bash
cd frontend
npm install
npm start
```

Kein `mvnw` im Repo, Maven muss lokal installiert sein. Alternativ `TourPlannerApplication` direkt aus der IDE starten (Profil `dev` setzen wenn kein PostgreSQL läuft).

## Benutzung

Beim ersten Öffnen landet man auf der Login-Seite. Über den Link darunter kommt man zur Registrierung. Nach dem Login ist man direkt drin.

Die Hauptansicht ist zweigeteilt: links die Tourliste, rechts die Detailansicht der ausgewählten Tour (oder das Formular, wenn man eine Tour anlegt oder bearbeitet).

**Tour anlegen:** Oben links auf „+ Neue Tour" klicken, Formular ausfüllen (Name, Start, Ziel, Transportmittel) und speichern. Route wird automatisch berechnet.

**Tour bearbeiten:** In der Detailansicht auf „Bearbeiten" (oben rechts im Hero-Bild) klicken. Bild kann man dort auch hochladen.

**Suchen:** Einfach in die Suchleiste tippen. Kein Enter nötig, wird automatisch gesucht.

**Log hinzufügen:** Tour auswählen, im Log-Bereich auf „+ Neues Log" klicken. Nach dem Speichern aktualisieren sich die Badges in der Detailansicht.

**Export/Import:** Über die API direkt, siehe [docs/API.md](docs/API.md).

**Abmelden:** Link unten links in der Tourliste.

## REST-API

Vollständige Dokumentation mit Beispielen: [docs/API.md](docs/API.md)

## Noch geplant

- PDF-Report für eine Tour
- Statistiken über alle Touren

## Projektstruktur

```
Tour-Planner/
├── docker-compose.yml
├── docs/                           # API-Doku, UX-Beschreibung, Kanban
│
├── backend/                        # Spring Boot 3 REST-API
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/tourplanner/
│       │   ├── TourPlannerApplication.java
│       │   ├── client/
│       │   │   └── OrsClient.java              # HTTP-Client für OpenRouteService
│       │   ├── config/
│       │   │   ├── DevDataInitializer.java     # Testdaten für dev-Profil
│       │   │   ├── ImageResourceConfig.java    # statische Ressource für Bilder
│       │   │   └── WebCorsConfig.java
│       │   ├── controller/
│       │   │   ├── AuthController.java         # /api/auth/register + /login
│       │   │   ├── GeocodingController.java    # /api/geocode
│       │   │   ├── GlobalExceptionHandler.java
│       │   │   ├── HealthController.java
│       │   │   ├── TourController.java         # CRUD + Suche
│       │   │   ├── TourExportController.java   # /api/tours/export
│       │   │   ├── TourImageController.java    # /api/tours/{id}/image
│       │   │   ├── TourImportController.java   # /api/tours/import
│       │   │   └── TourLogController.java
│       │   ├── dto/
│       │   │   ├── CreateTourRequest.java      # Eingabe beim Anlegen/Bearbeiten
│       │   │   ├── TourResponse.java           # Ausgabe inkl. berechneter Attribute
│       │   │   ├── TourExportDto.java          # für Export/Import (inkl. Logs)
│       │   │   ├── TourLogDto.java
│       │   │   ├── AuthRequest.java / AuthResponse.java
│       │   │   └── GeocodingResultDto.java
│       │   ├── exception/                      # eigene Exception-Klassen
│       │   ├── mapper/                         # Entity ↔ DTO Konvertierung
│       │   ├── model/
│       │   │   ├── Tour.java
│       │   │   ├── TourLog.java
│       │   │   ├── TransportType.java          # WALK, BICYCLE, CAR, PUBLIC_TRANSPORT
│       │   │   └── User.java
│       │   ├── repository/
│       │   ├── security/
│       │   │   ├── JwtAuthFilter.java
│       │   │   ├── JwtService.java
│       │   │   └── SecurityConfig.java
│       │   └── service/
│       │       ├── AuthServiceImpl.java
│       │       ├── ImageStorageService.java
│       │       ├── TourServiceImpl.java
│       │       ├── TourLogServiceImpl.java
│       │       ├── TourExportServiceImpl.java
│       │       ├── TourImportServiceImpl.java
│       │       ├── shared/
│       │       │   └── SecurityContextService.java
│       │       └── tour/
│       │           ├── TourDtoAssembler.java   # baut TourResponse inkl. Logs (kein N+1)
│       │           ├── TourEnrichmentService.java  # ORS-Aufruf beim Speichern
│       │           ├── TourOwnershipGuard.java
│       │           └── TourSearchService.java
│       └── resources/
│           ├── application.properties          # PostgreSQL (Produktion)
│           ├── application-dev.properties      # H2 In-Memory (Entwicklung)
│           └── init.sql
│
└── frontend/                       # Angular 18 SPA
    ├── Dockerfile
    ├── nginx.conf
    └── src/app/
        ├── app.routes.ts           # /login, /register, / (hinter AuthGuard)
        ├── guards/
        │   └── auth.guard.ts
        ├── interceptors/
        │   ├── jwt.interceptor.ts          # hängt Token an jeden Request
        │   └── auth-error.interceptor.ts   # 401 → automatischer Logout
        ├── models/
        │   ├── tour.model.ts
        │   ├── tour-log.model.ts
        │   └── auth.model.ts
        ├── services/
        │   ├── auth.service.ts
        │   ├── geocoding.service.ts
        │   ├── tour-api.service.ts
        │   ├── tour-log-api.service.ts
        │   ├── tour-state.service.ts       # Signal-basiertes State Management
        │   ├── tour-log-state.service.ts
        │   ├── tour-ui-state.service.ts
        │   └── tour-log-ui-state.service.ts
        ├── shared/
        │   ├── button/
        │   └── route-map/                  # Leaflet-Karte
        ├── components/
        │   ├── login/
        │   ├── register/
        │   ├── tour-planner/               # Haupt-Shell mit Master-Detail-Layout
        │   ├── tour-list/                  # Liste + Suchleiste + Logout
        │   ├── tour-details/               # Detailansicht + Badges + Karte + Logs
        │   ├── tour-form/
        │   ├── tour-log-list/
        │   └── tour-log-form/
        └── utils/
            └── format-duration.util.ts     # Sekunden → "1h 23min"
```
