# Tour Planner - Intermediate Submission

Willkommen zu unserem Tour Planner! Wir freuen uns, den aktuellen Zwischenstand unseres Projekts präsentieren zu können. 

## Über das Projekt

Der Tour Planner ist eine moderne webbasierte Client-Server-Anwendung, die es Nutzern ermöglicht, Outdoor-Abenteuer im Voraus zu planen und im Nachhinein detailliert zu dokumentieren. Egal ob Fahrradtour, Wanderung, Laufstrecke oder ein entspannter Urlaubstrip – die App bietet eine übersichtliche und zentrale Verwaltung für alle Strecken.

**Die Kernfunktionen und Konzepte des Projekts umfassen:**
- **Touren-Planung:** Nutzer können neue Touren anlegen. Jede Tour besteht typischerweise aus einer Bezeichnung, einer Beschreibung, Start- und Zielpunkt sowie dem gewählten Transportmittel. 
- **Logbuch für Touren (Tour Logs):** Nach Abschluss einer Tour können detaillierte Berichte (Logs) angelegt werden. Diese beinhalten Parameter wie Datum, Zeitaufwand, absolvierte Distanz, Schwierigkeitsgrad sowie eine persönliche Bewertung. Jeder Tour können beliebig viele Logs zugeordnet werden.
- **Architektur:** Die Applikation basiert auf einer Zwei-Schichten-Architektur (Two-Tier). Das interaktive Frontend ist eine Single-Page-Application, entwickelt in Angular mit striktem MVVM-Muster. Das stabile Backend basiert auf Java mit dem Spring Boot Framework. Im weiteren Verlauf wird eine Anbindung an eine PostgreSQL-Datenbank mittels JPA/Hibernate (O/R-Mapper) erfolgen.
- **Erweiterungen im finalen Release:** In der fertigen Version berechnet die Anwendung automatisch Attribute wie die Beliebtheit oder die Kinderfreundlichkeit einer Tour basierend auf den Logs. Zudem werden externe APIs wie OpenRouteservice für die automatische Ermittlung von Distanzen und Zeiten sowie Leaflet zur Kartenanzeige nahtlos integriert. Eine umfassende Volltextsuche rundet die Appliance dann ab.

**Studenten:** Emre und Shez

---

## Intermediate Hand-In

Für dieses Intermediate Hand-In haben wir viel Zeit und Mühe in ein solides Fundament gesteckt. Der Fokus lag primär auf einem robusten Angular-Frontend, einer sauberen Architektur und der reibungslosen Datenbindung.

Wir haben hart daran gearbeitet, alle Anforderungen der Intermediate-Checkliste umzusetzen, um eine nutzerfreundliche und stabile Erfahrung zu gewährleisten:

### Architektur & Tech Stack
- **Frontend Framework:** Angular
- **Architektur-Muster:** Wir halten uns strikt an das MVVM-Pattern (Model-View-ViewModel), um unsere Logik von den Views sauber zu trennen.
- **Backend Framework:** Java / Spring Boot (Bereits integriert und lauffähig)

### Features der Anwendung

#### GUI & User Experience
- **Reibungsloses Data Binding:** Die UI-Elemente und die Properties unserer View-Models arbeiten einwandfrei zusammen.
- **Responsive Design:** Die Benutzeroberfläche passt sich dynamisch an unterschiedliche Fenstergrößen an.
- **Wiederverwendbare Komponenten:** Wir haben eigene, wiederverwendbare UI-Komponenten definiert, um den Code strukturiert und wartbar zu halten.

#### Tourenverwaltung (Tours)
- **Gesamte CRUD-Funktionalität:** Touren können problemlos erstellt, bearbeitet und gelöscht werden.
- **Touren-Liste:** Alle Touren werden in einer übersichtlichen Liste dargestellt, mitsamt ihren wichtigsten Attributen (inklusive Platzhalter für Bilder).
- **Tour-Details:** Wählt man eine Tour aus, erscheinen alle erweiterten Details sowie ein Platzhalter für die bald folgende Routenkarte.
- **Eingabeüberprüfung:** Ungültige Eingaben bringen uns nicht aus dem Konzept. Unsere Validierung stellt sicher, dass die App bei Fehlbedienung nicht abstürzt.

#### Logbuch (Tour Logs)
- **Logbuch-Verwaltung:** Auch absolvierte Touren können vollumfänglich verwaltet werden (Create, Modify, Delete).
- **Zuweisung:** Wird eine Tour ausgewählt, erscheinen passend dazu alle protokollierten Logs in einer eigenen Listenansicht.
- **Sichere Eingaben:** Wie bei den Touren ist auch hier das User-Input vollständig validiert.

#### Dokumentation & Protokoll
- **UX & Wireframes:** Unser beigelegtes PDF-Protokoll enthält alle wichtigen Wireframes und beschreibt die UX, die wir uns für dieses Projekt überlegt haben.

---

## Wie man die App startet

### Frontend (Angular)
1. Öffne das Terminal und wechsle in den Frontend-Ordner.
2. Installiere die Abhängigkeiten: `npm install`
3. Starte den Entwicklungsserver: `npm start` oder `ng serve`
4. Öffne im Browser: `http://localhost:4200`

### Backend (Java / Spring Boot)
Voraussetzungen: **JDK 17** und **Apache Maven** installiert (`java` und `mvn` im PATH), oder Start über eine IDE mit Maven-Unterstützung.

1. Wechsle in den Backend-Ordner (`backend`).
2. Start im Terminal:
   - **Ohne PostgreSQL** (lokales Profil mit H2):  
     `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
   - **Mit PostgreSQL** (wie in `application.properties`):  
     `mvn spring-boot:run`
3. Das Backend lauscht standardmäßig auf **`http://localhost:8080`**.

**Hinweis:** In diesem Repository liegt kein Maven Wrapper (`mvnw`). Wer nur die Kommandozeile nutzt, braucht ein installiertes Maven. Alternativ: In der IDE die Klasse `TourPlannerApplication` ausführen und das Profil **`dev`** setzen (`spring.profiles.active=dev`), wenn keine Postgres-Instanz läuft.

### Alles mit Docker (PostgreSQL + Backend + Frontend)

Voraussetzung: [Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows) mit aktiviertem WSL2-Backend, oder Docker Engine + Compose.

Im **Projektroot** (`Tour-Planner`, dort wo `docker-compose.yml` liegt):

```bash
docker compose up --build
```

- **Frontend:** [http://localhost:4200](http://localhost:4200) (Nginx liefert die gebaute Angular-App; `/api` wird an das Backend weitergeleitet)
- **Backend direkt:** [http://localhost:8080](http://localhost:8080)

Beenden: `docker compose down` (Datenbank-Daten bleiben im Volume `pgdata` erhalten; Volume löschen: `docker compose down -v`).