# REST API Dokumentation

> **Base URL:** `http://localhost:8080`
>
> Alle Endpunkte (außer `/api/auth/*` und `/health`) sind **JWT-geschützt**.
> Token muss als `Authorization: Bearer <token>` Header mitgeschickt werden.

---

## Authentifizierung

### `POST /api/auth/register`
Neuen Benutzer registrieren.

**Request Body:**
```json
{
  "username": "emre",
  "password": "sicheresPasswort123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### `POST /api/auth/login`
Einloggen und JWT-Token erhalten.

**Request Body:**
```json
{
  "username": "emre",
  "password": "sicheresPasswort123"
}
```

**Response `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Fehler:**
- `401 Unauthorized` – Falsche Anmeldedaten

---

## Touren

### `GET /api/tours`
Alle Touren des eingeloggten Nutzers abrufen.

**Query-Parameter (optional):**

| Parameter | Typ    | Beschreibung                                    |
|-----------|--------|-------------------------------------------------|
| `search`  | string | Freitextsuche über Name, Beschreibung, Start, Ziel, Transportmittel |

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Wienerwald Tour",
    "description": "Rundtour durch den Wienerwald.",
    "from": "Wien Hietzing",
    "to": "Kaltenleutgeben",
    "transportType": "BICYCLE",
    "distance": 35.2,
    "estimatedTime": 14400,
    "image": "/api/images/abc123.jpg",
    "routeGeometry": "...(GeoJSON)...",
    "popularity": 7,
    "childFriendliness": "Mittel"
  }
]
```

---

### `GET /api/tours/{id}`
Eine einzelne Tour nach ID abrufen.

**Pfad-Parameter:**

| Parameter | Typ  | Beschreibung |
|-----------|------|--------------|
| `id`      | long | Tour-ID      |

**Response `200 OK`:** Wie oben (einzelnes Tour-Objekt)

**Fehler:**
- `404 Not Found` – Tour existiert nicht oder gehört einem anderen Nutzer

---

### `POST /api/tours`
Neue Tour anlegen. Start- und Zielkoordinaten werden automatisch per Geocoding aufgelöst und die Route via ORS berechnet.

**Request Body:**
```json
{
  "name": "Wienerwald Tour",
  "description": "Rundtour durch den Wienerwald.",
  "from": "Wien Hietzing",
  "to": "Kaltenleutgeben",
  "transportType": "BICYCLE",
  "distance": 0,
  "estimatedTime": 0,
  "image": "",
  "fromLon": null,
  "fromLat": null,
  "toLon": null,
  "toLat": null
}
```

> `distance` und `estimatedTime` können mit `0` übergeben werden – sie werden vom Backend via ORS überschrieben.

**Erlaubte `transportType`-Werte:** `WALK` · `BICYCLE` · `CAR` · `PUBLIC_TRANSPORT`

**Response `201 Created`:** Tour-Objekt mit berechneten Werten

**Fehler:**
- `400 Bad Request` – Validierungsfehler (z. B. leerer Name)

---

### `PUT /api/tours/{id}`
Bestehende Tour aktualisieren. Route wird neu berechnet.

**Pfad-Parameter:**

| Parameter | Typ  | Beschreibung |
|-----------|------|--------------|
| `id`      | long | Tour-ID      |

**Request Body:** Gleiche Struktur wie `POST /api/tours`

**Response `200 OK`:** Aktualisiertes Tour-Objekt

**Fehler:**
- `404 Not Found` – Tour nicht gefunden oder fremde Tour

---

### `DELETE /api/tours/{id}`
Tour löschen (inkl. aller zugehörigen Logs).

**Response `204 No Content`**

**Fehler:**
- `404 Not Found` – Tour nicht gefunden oder fremde Tour

---

### `POST /api/tours/{id}/image`
Bild für eine Tour hochladen (Multipart-Upload).

**Content-Type:** `multipart/form-data`

**Form-Feld:**

| Feld   | Typ  | Beschreibung           |
|--------|------|------------------------|
| `file` | File | Bilddatei (JPG/PNG/…)  |

**Response `200 OK`:** Aktualisiertes Tour-Objekt mit neuem `image`-Pfad

---

### `GET /api/tours/export`
Alle eigenen Touren inkl. Logs als JSON exportieren.

**Response `200 OK`:**
```
Content-Disposition: attachment; filename="tour-planner-export.json"
Content-Type: application/json
```

```json
[
  {
    "name": "Wienerwald Tour",
    "description": "...",
    "from": "Wien Hietzing",
    "to": "Kaltenleutgeben",
    "transportType": "BICYCLE",
    "distance": 35.2,
    "estimatedTime": 14400,
    "logs": [
      {
        "dateTime": "2024-06-01T10:00:00",
        "comment": "Schöne Tour!",
        "difficulty": 3,
        "totalDistance": 35.2,
        "totalTime": 7200,
        "rating": 4
      }
    ]
  }
]
```

---

### `POST /api/tours/import`
Touren aus einer exportierten JSON-Datei importieren.

**Request Body:** Gleiche Struktur wie Export-Response

**Response `200 OK`**

---

## Tour-Logs

### `GET /api/tours/{tourId}/logs`
Alle Logs einer Tour abrufen.

**Response `200 OK`:**
```json
[
  {
    "id": 1,
    "tourId": 5,
    "dateTime": "2024-06-01T10:00:00",
    "comment": "Tolle Strecke, leicht bergig.",
    "difficulty": 3,
    "totalDistance": 35.2,
    "totalTime": 7200,
    "rating": 4
  }
]
```

---

### `POST /api/tours/{tourId}/logs`
Neues Log für eine Tour anlegen.

**Request Body:**
```json
{
  "dateTime": "2024-06-01T10:00:00",
  "comment": "Tolle Strecke, leicht bergig.",
  "difficulty": 3,
  "totalDistance": 35.2,
  "totalTime": 7200,
  "rating": 4
}
```

**Felder:**

| Feld            | Typ      | Pflicht | Beschreibung                   |
|-----------------|----------|---------|--------------------------------|
| `dateTime`      | datetime | ✅      | ISO-8601 Zeitstempel           |
| `comment`       | string   | ❌      | Freitextkommentar              |
| `difficulty`    | int      | ✅      | 1–5 (1 = leicht, 5 = schwer)  |
| `totalDistance` | double   | ✅      | Zurückgelegte Distanz in km    |
| `totalTime`     | long     | ✅      | Gesamtzeit in Sekunden         |
| `rating`        | int      | ✅      | 1–5 (1 = schlecht, 5 = super)  |

**Response `201 Created`:** Das erstellte Log-Objekt

---

### `PUT /api/tours/{tourId}/logs/{logId}`
Bestehendes Log aktualisieren.

**Request Body:** Gleiche Struktur wie `POST`

**Response `200 OK`:** Aktualisiertes Log-Objekt

---

### `DELETE /api/tours/{tourId}/logs/{logId}`
Log löschen.

**Response `204 No Content`**

---

## Geocoding

### `GET /api/geocode?q={adresse}`
Freitextadresse in Koordinaten (lon/lat) umwandeln (via OpenRouteService).

**Beispiel:** `GET /api/geocode?q=Wien+Hietzing`

**Response `200 OK`:**
```json
{
  "lon": 16.2955,
  "lat": 48.1767
}
```

**Fehler:**
- `400 Bad Request` – leerer Query-Parameter
- `404 Not Found` – Adresse nicht gefunden / ORS nicht erreichbar

---

## Health Check

### `GET /health`
Prüft ob der Backend-Service läuft (kein Auth nötig).

**Response `200 OK`:**
```json
{
  "status": "UP"
}
```

---

## Fehler-Format

Alle Fehler geben ein einheitliches JSON-Objekt zurück:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Tour not found with id: 99"
}
```

| HTTP-Status | Bedeutung                                         |
|-------------|---------------------------------------------------|
| `400`       | Ungültige Eingabe / Validierungsfehler            |
| `401`       | Nicht authentifiziert (kein oder ungültiger Token)|
| `403`       | Keine Berechtigung (fremde Ressource)             |
| `404`       | Ressource nicht gefunden                          |
| `500`       | Interner Serverfehler                             |
