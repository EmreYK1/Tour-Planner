# Tour Planner

Ein Projekt von Emre Yüksel und Shez Soltani.

Die Idee: eine Web-App, mit der man Outdoor-Touren planen und nachträglich dokumentieren kann – Wanderungen, Radtouren, Laufstrecken, whatever. Im Kern geht es um eine einfache Tourverwaltung mit Start/Ziel, Transportmittel, Distanz und einer Beschreibung. Langfristig soll da auch eine Karte rein (Leaflet + OpenRouteService) und ein Logbuch für absolvierte Touren.

**Stack:** Angular (Frontend) · Spring Boot / Java (Backend) · PostgreSQL · Docker

---

## Was aktuell funktioniert

- Touren **anlegen, bearbeiten und löschen** (vollständige CRUD-API im Backend)
- **Detailansicht** einer Tour mit allen Attributen (Name, Beschreibung, Start/Ziel, Distanz, Dauer, Transportmittel)
- **Master-Detail-Layout** – Tourliste links, Details rechts
- **Formular** mit Validierung – funktioniert für Erstellen und Bearbeiten (DRY, eine Komponente für beides)
- Saubere Trennung Frontend ↔ Backend über REST-API
- Alles läuft per Docker (PostgreSQL + Backend + Frontend in einem Befehl)

## Was noch fehlt / geplant ist

- Tour Logs (Logbuch für absolvierte Touren)
- Kartenintegration mit Leaflet
- Automatische Routen-Berechnung via OpenRouteService
- Volltextsuche
- Berechnete Attribute (Beliebtheit, Kinderfreundlichkeit) basierend auf Logs

---

## App starten

### Empfohlen: Docker

```bash
docker compose up --build
```

- Frontend: [http://localhost:4200](http://localhost:4200)
- Backend: [http://localhost:8080](http://localhost:8080)

Beenden: `docker compose down`  
Datenbank-Volume löschen (fresh start): `docker compose down -v`

> Voraussetzung: [Docker Desktop](https://www.docker.com/products/docker-desktop/) mit WSL2, oder Docker Engine + Compose unter Linux.

---

### Manuell (ohne Docker)

**Backend** – braucht JDK 17 und Maven:

```bash
cd backend

# Mit lokaler H2-Datenbank (kein PostgreSQL nötig):
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Mit PostgreSQL (Verbindung in application.properties eintragen):
mvn spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm start        # startet auf http://localhost:4200
```

> Kein `mvnw` im Repo – Maven muss lokal installiert sein, oder man startet `TourPlannerApplication` direkt aus der IDE (Profil `dev` setzen wenn kein PostgreSQL läuft).

---

## Projektverzeichnis

```
Tour-Planner/
├── docker-compose.yml              # Orchestriert PostgreSQL, Backend & Frontend
├── .gitignore
│
├── backend/                        # Java / Spring Boot REST-API
│   ├── Dockerfile
│   ├── pom.xml                     # Maven-Projektdefinition & Abhängigkeiten
│   └── src/main/
│       ├── java/com/tourplanner/
│       │   ├── TourPlannerApplication.java   # Spring-Boot-Einstiegspunkt
│       │   ├── config/
│       │   │   ├── WebCorsConfig.java        # CORS-Konfiguration
│       │   │   └── DevDataInitializer.java   # Testdaten für dev-Profil
│       │   ├── controller/
│       │   │   ├── TourController.java       # REST-Endpunkte (CRUD)
│       │   │   └── HealthController.java     # /health Endpoint
│       │   ├── dto/
│       │   │   └── TourDto.java              # Data Transfer Object
│       │   ├── mapper/
│       │   │   └── TourMapper.java           # Entity ↔ DTO Konvertierung
│       │   ├── model/
│       │   │   ├── Tour.java                 # JPA-Entity
│       │   │   └── TransportType.java        # Enum (BIKE, HIKE, RUNNING, …)
│       │   ├── repository/
│       │   │   └── TourRepository.java       # Spring Data JPA Repository
│       │   └── service/
│       │       ├── TourService.java          # Service-Interface
│       │       └── TourServiceImpl.java      # Service-Implementierung
│       └── resources/
│           ├── application.properties        # Produktions-Konfiguration (PostgreSQL)
│           ├── application-dev.properties    # Dev-Profil (H2 In-Memory)
│           └── init.sql                      # Initiales DB-Schema
│
└── frontend/                       # Angular Single-Page-Application
    ├── Dockerfile
    ├── nginx.conf                  # Nginx-Konfiguration (Proxy /api → Backend)
    ├── proxy.conf.json             # Proxy für ng serve (Dev)
    ├── angular.json
    ├── package.json
    ├── tsconfig.json
    └── src/
        ├── index.html
        ├── main.ts                 # Angular-Bootstrapping
        ├── styles.scss             # Globale Styles
        └── app/
            ├── app.component.*     # Root-Komponente (Master-Detail-Layout)
            ├── app.config.ts       # Angular-Konfiguration (Provider, HTTP)
            ├── app.routes.ts       # Routing-Konfiguration
            ├── models/
            │   └── tour.model.ts   # TypeScript-Interface für Tour & Enums
            ├── services/
            │   ├── tour-api.service.ts     # HTTP-Calls ans Backend
            │   └── tour-state.service.ts   # Zentrales State-Management (MVVM ViewModel)
            └── components/
                ├── tour-list/              # Linke Spalte: Tourliste
                │   ├── tour-list.component.ts
                │   ├── tour-list.component.html
                │   └── tour-list.component.scss
                ├── tour-details/           # Rechte Spalte: Detailansicht
                │   ├── tour-details.component.ts
                │   ├── tour-details.component.html
                │   └── tour-details.component.scss
                └── tour-form/              # Formular: Tour erstellen & bearbeiten
                    ├── tour-form.component.ts
                    ├── tour-form.component.html
                    └── tour-form.component.scss
```