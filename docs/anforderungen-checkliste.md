# Anforderungs-Checkliste: Code-Nachweis & Demo-Anleitung

Diese Datei geht jeden Punkt der Grading-Checkliste durch, zeigt **wo genau im Code** er umgesetzt ist und wie man ihn **live in der Web-UI** vorführt (z.B. für die Abgabe/Präsentation).

Stand der Prüfung: aktueller Code auf `main`. Backend-Basis-URL in den Beispielen: `http://localhost:8080`.

---

## Must-Haves

### ✅ Uses C# or Java for Backend
- **Beleg:** [backend/pom.xml](../backend/pom.xml) — Maven-Projekt, Java 17, Spring Boot 3.4.4 Parent.
- **Demo:** `cd backend && mvn spring-boot:run -Dspring-boot.run.profiles=dev` starten, Terminal-Output zeigt Spring-Boot-Banner + Java.

### ✅ Uses Angular as Frontend Framework
- **Beleg:** [frontend/package.json](../frontend/package.json) — `@angular/core: ^19.2.0`. Standalone Components überall (`standalone: true`), z.B. [tour-form.component.ts:16](../frontend/src/app/components/tour-form/tour-form.component.ts).
- **Demo:** `cd frontend && ng serve` → `http://localhost:4200`.

### ⚠️ Uses MVVM for UI
- **Beleg:** Trennung View (Component + `.html`-Template) / ViewModel (State-Service mit Angular `signal()`):
  - [tour-state.service.ts](../frontend/src/app/services/tour-state.service.ts) hält den gesamten Tour-Zustand (`_tours`, `_selectedTour`, `_loading` als Signals, Zeilen 11–21) und stellt ihn read-only als `tours`, `selectedTour` etc. bereit (Zeilen 17–21).
  - Komponenten wie [tour-list.component.ts:33–37](../frontend/src/app/components/tour-list/tour-list.component.ts) binden nur an diese Signals — keine eigene Business-Logik in der Komponente.
  - Analog für Tour-Logs: `tour-log-state.service.ts` + `tour-log-ui-state.service.ts` (getrennter UI-State vom Daten-State, SRP).
- **Was fehlt:** Nirgends steht explizit "MVVM" als Begriff/Kommentar im Code oder in der Doku. Für die Abgabe unbedingt im Protokoll erklären: *"View = Component-Template, ViewModel = State-Service mit Signals, Model = `models/*.model.ts` + Backend-DTOs."*
- **Demo:** Im Browser DevTools bei Aktionen (Tour anlegen) zeigen, dass die Komponente selbst keine Daten hält, sondern über `inject(TourStateService)` liest/schreibt.

### ✅ Implements a layer-based architecture (UI/BL/DAL)
- **Beleg:** Eigene Packages mit dokumentiertem Zweck via `package-info.java`:
  - `controller` = Präsentationsschicht → [controller/package-info.java](../backend/src/main/java/com/tourplanner/controller/package-info.java)
  - `service` = Business-Layer → [service/package-info.java](../backend/src/main/java/com/tourplanner/service/package-info.java)
  - `repository` = DAL → [repository/package-info.java](../backend/src/main/java/com/tourplanner/repository/package-info.java)
  - `model` = Domänenmodell → [model/package-info.java](../backend/src/main/java/com/tourplanner/model/package-info.java)
- **Demo:** Projektstruktur im Editor aufklappen (`backend/src/main/java/com/tourplanner/{controller,service,repository,model}`) und die `package-info.java`-Dateien zeigen.

### ⚠️ Implements at least one design pattern
- **Faktisch vorhanden, aber nirgends benannt:**
  - **Repository Pattern**: `TourRepository extends JpaRepository<Tour, Long>` ([TourRepository.java:14](../backend/src/main/java/com/tourplanner/repository/TourRepository.java)) — kapselt Datenzugriff hinter einem Interface.
  - **Guard/Strategy-artig**: `TourOwnershipGuard` ([TourOwnershipGuard.java](../backend/src/main/java/com/tourplanner/service/tour/TourOwnershipGuard.java)) kapselt die Ownership-Prüfung als wiederverwendbare Regel, von `TourLogOwnershipGuard` per Delegation wiederverwendet.
  - **Facade-artig**: `TourServiceImpl` bündelt `TourEnrichmentService`, `TourSearchService`, `TourDtoAssembler`, `TourOwnershipGuard` hinter einer einfachen Service-Schnittstelle.
- **To-Do vor Abgabe:** Mindestens **eines** davon explizit im Code kommentieren (z.B. `// Repository Pattern: kapselt DB-Zugriff hinter Interface`) und im Protokoll mit Begründung beschreiben. Ohne explizite Benennung zählt es ein Grader evtl. nicht.
- **Demo:** Code-Stelle im Editor zeigen + mündlich erklären, welches GoF-Pattern das ist und warum.

### ✅ Uses a Postgres Database for storing Tour Data
- **Beleg:** [application.properties:8](../backend/src/main/resources/application.properties) `spring.datasource.url=jdbc:postgresql://localhost:5432/tourplanner`, Dialect `PostgreSQLDialect` (Zeile 16). [docker-compose.yml](../docker-compose.yml) startet `postgres:16-alpine`.
- **Demo:** `docker compose up -d db` (oder ganzer Stack), dann `psql` oder DBeaver an Port 5432 anschließen und `SELECT * FROM tours;` zeigen, während parallel im Frontend eine Tour angelegt wird.

### ✅ Does not allow for SQL injection
- **Beleg:** Alle DB-Zugriffe laufen über Spring-Data-JPA-Methodennamen (`findByOwner`) oder **parametrisierte** `@Query`s mit Bind-Parametern (`:query`, `:owner`), z.B. [TourRepository.java:18-22](../backend/src/main/java/com/tourplanner/repository/TourRepository.java) und [TourLogRepository.java:18-26](../backend/src/main/java/com/tourplanner/repository/TourLogRepository.java). Kein String-Concatenation von Nutzereingaben in SQL/JPQL-Text — `CONCAT()` ist eine JPQL-Funktion auf einem gebundenen Parameter, kein Query-Building per String.
- **Demo:** In der Suche testweise `' OR '1'='1` eingeben → liefert nur normale (leere) Textsuche, kein Fehler/Datenleck.

### ✅ Uses an OR-Mapping Library
- **Beleg:** `spring-boot-starter-data-jpa` in [pom.xml](../backend/pom.xml), `@Entity`-Klassen [Tour.java:26](../backend/src/main/java/com/tourplanner/model/Tour.java), [TourLog.java:22](../backend/src/main/java/com/tourplanner/model/TourLog.java), [User.java:17](../backend/src/main/java/com/tourplanner/model/User.java) mit Hibernate als JPA-Provider.
- **Demo:** Log-Output beim Start zeigen (Hibernate-Dialekt-Zeile) oder die Entity-Klassen im Editor.

### ✅ Uses configuration (not in code) at minimum the DB connection string
- **Beleg:** [application.properties](../backend/src/main/resources/application.properties) enthält DB-URL/User/Passwort, ORS-API-Key (`ors.api.key=${ORS_API_KEY:}`, Zeile 32 — per Umgebungsvariable überschreibbar), JWT-Secret. [docker-compose.yml](../docker-compose.yml) injiziert DB-Credentials über Environment-Variablen. Lokale Geheimnisse (`application-local.properties`, echter ORS-Key) sind über [.gitignore](../.gitignore) explizit ausgeschlossen und **nicht** im Git-Repo.
- **Demo:** `application.properties` zeigen + erklären, dass `ORS_API_KEY` als Env-Var kommt (`echo $ORS_API_KEY` bzw. `docker-compose.yml` Zeilen mit `environment:`).

### ✅ Integrates the OpenRouteServices.org API and Leaflet
- **Beleg:**
  - ORS Directions API: [OrsClient.java:42-53](../backend/src/main/java/com/tourplanner/client/OrsClient.java) — POST an `/v2/directions/{profile}/geojson`.
  - ORS Geocoding API: [OrsClient.java:88-110](../backend/src/main/java/com/tourplanner/client/OrsClient.java).
  - Leaflet: `import * as L from 'leaflet'` in [route-map.component.ts:2](../frontend/src/app/shared/route-map/route-map.component.ts), Karte + Route + Start/Ziel-Marker (Zeilen 20-62).
- **Demo:** Neue Tour anlegen mit echten Adressen (z.B. "Stephansplatz, Wien" → "Karlsplatz, Wien") → Distanz/Zeit werden automatisch von ORS befüllt, Route erscheint als Linie auf der Leaflet-Karte in den Tour-Details.

### ❌ Implements at least 20 Unit Tests
- **Ist-Zustand:** Genau **1** Test existiert: [TourPlannerApplicationTests.java](../backend/src/test/java/com/tourplanner/TourPlannerApplicationTests.java) mit `contextLoads()` — nur ein trivialer Spring-Context-Smoketest.
- **Das ist aktuell der einzige Grund, warum die Abgabe laut Regelwerk mit 0 Punkten bewertet würde.** Muss vor Abgabe behoben werden (siehe separates To-Do).

---

## Features

### ✅ Self-Registration + Login
- **Beleg:** `POST /api/auth/register` / `POST /api/auth/login` in [AuthController.java](../backend/src/main/java/com/tourplanner/controller/AuthController.java), Passwort-Hashing via `BCryptPasswordEncoder` ([SecurityConfig.java:43-45](../backend/src/main/java/com/tourplanner/security/SecurityConfig.java)), JWT-Ausstellung in `AuthServiceImpl`. Frontend: `login.component.ts`, `register.component.ts`, Token-Anhängung via [jwt.interceptor.ts](../frontend/src/app/interceptors/jwt.interceptor.ts), Routen-Schutz via [auth.guard.ts](../frontend/src/app/guards/auth.guard.ts).
- **Demo:** Auf `/register` neuen User anlegen → automatisch eingeloggt → `/` zeigt Tourenliste. Abmelden, mit falschem Passwort einloggen → Fehlermeldung. Ohne Login direkt `/` aufrufen → Redirect zu `/login` (Guard).

### ✅ Tour erstellen, CRUD (auch DAL)
- **Beleg:** `TourController` ([TourController.java](../backend/src/main/java/com/tourplanner/controller/TourController.java)) GET/POST/PUT/DELETE → `TourServiceImpl` → `TourRepository` (JPA, DAL).
- **Demo:** In der UI: "Neue Tour" anlegen, danach bearbeiten (Stift-Icon in Tour-Details), danach löschen (Papierkorb-Button) — jeweils Netzwerk-Tab in DevTools zeigen (`POST /api/tours`, `PUT /api/tours/{id}`, `DELETE /api/tours/{id}`).

### ✅ Pflichtfelder der Tour (inkl. Bild) + Listenansicht
- **Beleg:** [Tour.java:34-60](../backend/src/main/java/com/tourplanner/model/Tour.java) — name, description, from, to, transportType, distance, estimatedTime, image, routeGeometry. Bild-Upload separat über [ImageStorageService.java](../backend/src/main/java/com/tourplanner/service/ImageStorageService.java) (Dateisystem, nicht DB). Liste: [tour-list.component.html](../frontend/src/app/components/tour-list/tour-list.component.html).
- **Demo:** Tour mit Bild anlegen (Datei-Upload im Formular) → Bild erscheint als Hero-Image in Tour-Details ([tour-details.component.html:5](../frontend/src/app/components/tour-details/tour-details.component.html)). Im Backend-Ordner `backend/uploads/` die gespeicherte Datei zeigen (nicht in der DB, nur die URL).

### ✅ Berechnete Attribute (Popularity, Kinderfreundlichkeit)
- **Beleg:** [TourAttributeCalculator.java](../backend/src/main/java/com/tourplanner/service/shared/TourAttributeCalculator.java) — `computePopularity()` (Anzahl Logs, Zeile 19-21), `computeChildFriendliness()` (aus Ø Schwierigkeit/Distanz/Zeit, Zeile 24-40). Angezeigt als Badges in [tour-details.component.html:97-127](../frontend/src/app/components/tour-details/tour-details.component.html).
- **Demo:** Zu einer Tour mehrere Logs mit niedriger Schwierigkeit/kurzer Distanz anlegen → Badge "Kinderfreundlich: Hoch" erscheint; Anzahl der Logs als "Beliebtheit"-Badge zeigen.

### ✅ Tour-Details zeigen alle Attribute + Karte
- **Beleg:** [tour-details.component.html](../frontend/src/app/components/tour-details/tour-details.component.html) — Stat-Cards für Distanz/Dauer/Transport/Route (Zeilen 41-94), `<app-route-map>` (Zeile 138).
- **Demo:** Tour anklicken → rechte Spalte zeigt alle Werte + interaktive Karte mit Routenlinie.

### ✅ Validiertes User-Input (kein Crash)
- **Beleg:** Backend: `@NotBlank`, `@Size`, `@DecimalMin`, `@Min` in [CreateTourRequest.java:12-19](../backend/src/main/java/com/tourplanner/dto/CreateTourRequest.java), ausgelöst via `@Valid` in [TourController.java:49](../backend/src/main/java/com/tourplanner/controller/TourController.java), Fehler werden zentral als JSON 400 abgefangen in [GlobalExceptionHandler.java:40-47](../backend/src/main/java/com/tourplanner/controller/GlobalExceptionHandler.java) (`handleValidation`). Frontend: `Validators.required/min/max` in [tour-form.component.ts:43-54](../frontend/src/app/components/tour-form/tour-form.component.ts).
- **Demo:** Formular mit leerem Namen absenden → Submit-Button bleibt durch Angular-Validierung blockiert. Mit DevTools/Postman einen ungültigen Request direkt ans Backend schicken (leerer Name) → sauberer 400-JSON-Fehler statt Stacktrace/Absturz.

### ✅ Tour Logs: CRUD, Pflichtfelder, Listenansicht
- **Beleg:** [TourLog.java:30-46](../backend/src/main/java/com/tourplanner/model/TourLog.java) — dateTime, comment, difficulty, totalDistance, totalTime, rating (exakt lt. Angabe). CRUD über [TourLogController.java](../backend/src/main/java/com/tourplanner/controller/TourLogController.java). Liste: `tour-log-list.component.ts`.
- **Demo:** In Tour-Details "Neuer Log" → Formular ausfüllen → erscheint sofort in der Log-Liste unterhalb der Tour. Bearbeiten/Löschen eines Logs zeigen.

### ✅ Tours/Logs gehören genau einem User (kein Sharing)
- **Beleg:** `TourOwnershipGuard.requireOwnedTour()` ([TourOwnershipGuard.java:24-29](../backend/src/main/java/com/tourplanner/service/tour/TourOwnershipGuard.java)) prüft `tour.getOwner().getId().equals(currentUser.getId())` und wirft `TourNotFoundException` (404, nicht 403 — verrät bewusst nicht, ob die Tour überhaupt existiert). `TourLogOwnershipGuard` delegiert dorthin ([TourLogOwnershipGuard.java:24-30](../backend/src/main/java/com/tourplanner/service/tourlog/TourLogOwnershipGuard.java)). Repository-Queries filtern zusätzlich direkt nach Owner (`findByOwner`, `searchByTextAndOwner`).
- **Demo:** Zwei User registrieren (User A, User B), mit A eine Tour anlegen, deren ID notieren. Mit B eingeloggt versuchen `GET /api/tours/{idVonA}` aufzurufen (z.B. über Browser-URL oder Postman mit B's Token) → 404, nicht die fremde Tour.

### ✅ Full-Text-Search inkl. berechneter Werte
- **Beleg:** [TourSearchService.java](../backend/src/main/java/com/tourplanner/service/tour/TourSearchService.java) — durchsucht Tour-Felder per DB-Query (Zeile 37-38), Tour-Log-Kommentare per Batch-Query (Zeile 42-44), **und** die berechneten Werte Popularity/Kinderfreundlichkeit in-memory (`matchesComputedAttributes`, Zeile 62-69) — Ergebnisse werden vereinigt (Zeile 55-59).
- **Demo:** Im Suchfeld der Tourenliste `"Hoch"` eingeben → zeigt nur Touren mit Kinderfreundlichkeit "Hoch", obwohl das Wort in keinem Namen/keiner Beschreibung steht. Auch Kommentar-Text aus einem Tour-Log als Suchbegriff eingeben → passende Tour erscheint.

### ✅ Import/Export
- **Beleg:** Export: `GET /api/tours/export` ([TourExportController.java](../backend/src/main/java/com/tourplanner/controller/TourExportController.java)) liefert JSON-Datei mit `Content-Disposition: attachment`. Import: `POST /api/tours/import` ([TourImportController.java](../backend/src/main/java/com/tourplanner/controller/TourImportController.java)). Frontend: `import-export.service.ts`, Toast-Benachrichtigungen in [tour-list.component.ts:73-111](../frontend/src/app/components/tour-list/tour-list.component.ts).
- **Demo:** "Exportieren"-Button klicken → JSON-Datei wird heruntergeladen, Inhalt kurz zeigen. Danach eine Tour löschen, dieselbe Datei über "Importieren" wieder einlesen → Tour ist wieder da, Toast-Meldung erscheint.

### ❌ Mandatory Unique Feature
- **Ist-Zustand:** Kein Feature ist explizit als "Unique Feature" deklariert. README nennt "PDF-Report" und "Statistiken über alle Touren" nur unter "noch geplant".
- **To-Do:** Vor Abgabe eines auswählen und umsetzen (PDF-Report pro Tour wäre naheliegend, da Bibliotheken wie `iText`/`OpenPDF` schnell einzubinden sind) und im Protokoll explizit als "Unique Feature" benennen.

---

## Non-Functional Requirements

### ⚠️ Layers only call methods of the immediate layer below
- **Beleg:** Controller → Service → Repository ist die gelebte Struktur (z.B. `TourController` kennt nur `TourService`, nie `TourRepository` direkt). Ausnahme prüfen: einzelne Controller wie `TourExportController`/`TourImportController` rufen jeweils eigene Services auf, das passt.
- **Für die Präsentation:** Ein Beispiel wie [TourController.java](../backend/src/main/java/com/tourplanner/controller/TourController.java) zeigen (nutzt nur `TourService`, Zeile 30).

### ✅ Layers define their own exceptions, no implementation-specific exceptions
- **Beleg:** Eigene Exceptions unter [exception/](../backend/src/main/java/com/tourplanner/exception/): `TourNotFoundException`, `InvalidCredentialsException`, `InvalidImageException`, `InvalidImportFormatException`, `OrsApiException`. Zentral behandelt in [GlobalExceptionHandler.java](../backend/src/main/java/com/tourplanner/controller/GlobalExceptionHandler.java).
- **Demo:** Löschen einer nicht existierenden Tour-ID per Postman → sauberer JSON-Fehler mit eigenem Format (Zeile 59-66), keine Hibernate/SQL-Stacktrace.

### ✅ Uses the OpenRouteServices.org Directions API for tour retrieval
- Siehe oben ("Integrates OpenRouteServices.org API and Leaflet").

### ✅ Uses Leaflet for the map
- Siehe oben.

### ✅ All tour data (except image) is stored in the database
- **Beleg:** `Tour.image` speichert nur die URL-Zeichenkette ([Tour.java:57](../backend/src/main/java/com/tourplanner/model/Tour.java)), die eigentliche Datei liegt unter `app.image.base-dir` auf der Festplatte ([ImageStorageService.java:29-38](../backend/src/main/java/com/tourplanner/service/ImageStorageService.java)). Alle anderen Felder sind normale DB-Spalten.
- **Demo:** DB-Tabelle `tours` zeigen (Spalte `image` enthält nur `/api/images/xyz.png`), parallel den Ordner `backend/uploads/` zeigen, in dem die echte Bilddatei liegt.

### ✅ All configuration information is stored in configuration (not in code)
- Siehe oben ("Uses configuration...").

### ⚠️ Logs exceptions, errors and other useful technical information
- **Ist-Zustand:** Logging-Framework technisch vorhanden (Spring Boot Default = SLF4J + Logback, **kein** log4j/log4net explizit eingebunden), aber nur eine einzige Klasse loggt tatsächlich: [TourEnrichmentService.java:17,41,43](../backend/src/main/java/com/tourplanner/service/tour/TourEnrichmentService.java) (`log.warn(...)`, 2 Aufrufe).
- **To-Do vor Abgabe:** Mindestens in `GlobalExceptionHandler` (Zeile 54-57, `handleGeneric`) und in den Ownership-Guards/Auth-Flows Logging ergänzen, damit "Logging als Querschnittsthema" glaubhaft demonstrierbar ist. Optional: `log4j2.xml`/eigenes Logback-Konfigurationsfile mit Datei-Output hinzufügen, um explizit zu zeigen, dass es konfiguriert (nicht nur Default) ist.
- **Demo (aktuell):** Tour mit ungültigen/nicht auflösbaren Adressen anlegen → im Server-Log erscheint die `log.warn`-Zeile aus `TourEnrichmentService`.

### ❌ Quality of unit tests (usefulness, no duplicates, ...)
- **Ist-Zustand:** Nicht bewertbar, da praktisch keine Tests existieren (siehe Must-Have oben).

---

## Protocol (separates PDF-Dokument — aktuell nicht vorhanden)

Alles hier ist **Dokumentationsarbeit**, kein Code. Es existiert noch **kein PDF-Protokoll** im Repo. Folgendes muss dafür jeweils erstellt/geschrieben werden:

| Punkt | Status | Hinweis |
|---|---|---|
| App-Architektur (Layer, Klassendiagramm) | ❌ fehlt | Klassendiagramm aus `model`/`service`/`repository`/`controller` ableiten, z.B. mit PlantUML |
| Use-Cases (Use-Case- + Sequenzdiagramm für Volltextsuche) | ❌ fehlt | Sequenzdiagramm kann direkt aus `TourSearchService.filterByQuery()` (s.o.) abgeleitet werden |
| UX (Wireframes) | ✅ vorhanden | [docs/ux-description.md](../docs/ux-description.md) + 3 PNGs in `docs/images/` — nur noch ins PDF übernehmen |
| Bibliotheks-Entscheidungen, Lessons Learned | ❌ fehlt | Freitext |
| Beschreibung des Design Patterns | ❌ fehlt | Hängt von obigem To-Do ab (Pattern zuerst im Code benennen) |
| Unit-Testing-Entscheidungen | ❌ fehlt | Erst sinnvoll schreibbar, wenn die 20+ Tests existieren |
| Unique Feature | ❌ fehlt | Hängt vom offenen Feature-To-Do ab |
| Zeiterfassung | ❌ fehlt | Sollte laufend mitgeführt werden |
| Link zum Git-Repo | ✅ trivial | Nur einfügen |

---

## Zusammenfassung: Reihenfolge der offenen To-Dos

1. **20+ echte JUnit-Tests** schreiben (Must-Have, aktuell 1 vorhanden) — größter Blocker.
2. **Design Pattern** im Code explizit benennen/kommentieren (z.B. Repository-Pattern in `TourRepository`, oder Guard/Strategy in `TourOwnershipGuard`).
3. **Unique Feature** festlegen und umsetzen.
4. **Logging** auf mehr Klassen ausweiten (mind. GlobalExceptionHandler, Auth-Flow).
5. **PDF-Protokoll** schreiben: UML (Klassendiagramm, Use-Case-Diagramm, Sequenzdiagramm Volltextsuche), Wireframes (schon vorhanden) einfügen, Pattern/Test/Feature-Begründungen, Zeiterfassung, Git-Link.
