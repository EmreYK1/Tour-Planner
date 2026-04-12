# Tour Planner

Ein Projekt von Emre Yüksel und Shez Soltani.

Die Idee: eine Web-App, mit der man Outdoor-Touren planen und nachträglich dokumentieren kann – Wanderungen, Radtouren, Laufstrecken, whatever. Im Kern geht es um eine einfache Tourverwaltung mit Start/Ziel, Transportmittel, Distanz und einer Beschreibung. Langfristig soll da auch eine Karte rein (Leaflet + OpenRouteService) und ein Logbuch für absolvierte Touren.

**Stack:** Angular (Frontend) · Spring Boot / Java (Backend) · PostgreSQL · Docker

---

## Was aktuell funktioniert

- Touren **anlegen, bearbeiten und löschen** (vollständige CRUD-API im Backend)
- Tour Logs (Logbuch für absolvierte Touren)
- **Detailansicht** einer Tour mit allen Attributen (Name, Beschreibung, Start/Ziel, Distanz, Dauer, Transportmittel)
- **Master-Detail-Layout** – Tourliste links, Details rechts
- **Formular** mit Validierung – funktioniert für Erstellen und Bearbeiten (DRY, eine Komponente für beides)
- Saubere Trennung Frontend ↔ Backend über REST-API
- Alles läuft per Docker (PostgreSQL + Backend + Frontend in einem Befehl)

## Was noch fehlt / geplant ist

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
│       │   │   ├── TourController.java       # REST-Endpunkte für Touren
│       │   │   ├── TourLogController.java    # REST-Endpunkte für Tour-Logs
│       │   │   └── HealthController.java     # /health Endpoint
│       │   ├── dto/
│       │   │   ├── TourDto.java              # Data Transfer Object für Touren
│       │   │   └── TourLogDto.java           # DTO für Tour-Logs
│       │   ├── mapper/
│       │   │   ├── TourMapper.java           # Entity ↔ DTO Konvertierung (Tour)
│       │   │   └── TourLogMapper.java        # Entity ↔ DTO Konvertierung (Log)
│       │   ├── model/
│       │   │   ├── Tour.java                 # Tour Entity (JPA)
│       │   │   ├── TourLog.java              # Tour Log Entity (JPA)
│       │   │   └── TransportType.java        # Enum (WALK, BICYCLE, CAR, …)
│       │   ├── repository/
│       │   │   ├── TourRepository.java       # Repository für Touren
│       │   │   └── TourLogRepository.java    # Repository für Logs
│       │   └── service/
│       │       ├── TourService.java          # Service für Touren
│       │       └── TourLogService.java       # Service für Logs
│       └── resources/
│           ├── application.properties        # Produktions-Konfiguration (PostgreSQL)
│           ├── application-dev.properties    # Dev-Profil (H2 In-Memory)
│           └── init.sql                      # Initiales DB-Schema
│
└── frontend/                       # Angular Single-Page-Application
    ├── Dockerfile
    ├── nginx.conf                  # Nginx-Konfiguration
    └── src/app/
        ├── app.component.*         # Root-Layout & Shell
        ├── app.routes.ts           # Routing-Konfiguration
        ├── models/
        │   ├── tour.model.ts       # Interface & Enums für Touren
        │   └── tour-log.model.ts   # Interface für Tour-Logs
        ├── services/
        │   ├── tour-api.service.ts         # API-Aufrufe (Tours)
        │   ├── tour-log-api.service.ts     # API-Aufrufe (Logs)
        │   ├── tour-state.service.ts       # State Management (Tours)
        │   ├── tour-log-state.service.ts   # State Management (Logs)
        │   ├── tour-ui-state.service.ts    # UI-Logik für Tour-Formular
        │   └── tour-log-ui-state.service.ts # UI-Logik für Log-Formular
        ├── shared/
        │   └── button/             # Wiederverwendbare Button-Komponente
        ├── components/
        │   ├── tour-list/          # Linke Spalte: Liste aller Touren
        │   ├── tour-details/       # Rechte Spalte: Hauptansicht & Tour-Details
        │   ├── tour-form/          # Formular für Tour Erstellen/Bearbeiten
        │   ├── tour-log-list/      # Liste der Logs unter den Tour-Details
        │   └── tour-log-form/      # Formular für Log Erstellen/Bearbeiten
        └── utils/
            └── format-duration.util.ts # Utility für Zeit-Formatierung
```
