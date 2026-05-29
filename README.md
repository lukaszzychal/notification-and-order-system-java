# notification-and-order-system-java
Notification and order system - java

# System Zamówień i Powiadomień (Java + Spring Boot)

Prosta aplikacja backendowa realizująca proces składania zamówień, weryfikację magazynu oraz asynchroniczne wysyłanie powiadomień.

## 🏗️ Jak to działa (Przepływ)

1. Klient wysyła zamówienie przez HTTP (REST API).
2. Aplikacja w jednej transakcji sprawdza bazę (PostgreSQL), czy jest towar:
   * Jeśli jest: zmniejsza stan magazynu, zapisuje zamówienie i rzuca hasło na Kafkę.
   * Jeśli nie ma: przerywa wszystko, cofa zmiany i zwraca błąd.
3. Osobny komponent (Konsument Kafki) odbiera komunikat w tle i symuluje wysyłkę powiadomienia.

---

## 🛠️ Technologie

* Spring Boot 3 (Web, Data JPA, Kafka)
* PostgreSQL (Baza danych)
* Apache Kafka (Broker wiadomości w trybie KRaft)
* Lombok & JUnit 5 / Mockito

---

## ⚙️ Funkcje systemu (Do zrobienia / Gotowe)

### 1. Zarządzanie Zamówieniami i Magazynem
* [ ] **Dodawanie zamówienia (`POST /api/orders`):** Przyjmuje JSON (produkt, ilość, cena). Sprawdza stan w magazynie. Aktualizuje bazę danych lub zwraca błąd 400, jeśli towaru brakuje.
* [ ] **Pobieranie zamówień (`GET /api/orders`):** Zwraca listę wszystkich zapisanych zamówień.

### 2. Komunikacja asynchroniczna (Kafka)
* [ ] **Producent:** Po udanym zapisie zamówienia wysyła komunikat na topik `order-topic`.
* [ ] **Konsument:** Nasłuchuje `order-topic` i wypisuje info o nowym zamówieniu w konsoli (symulacja wysyłki maila).

---

## 📝 Plan rozwoju (Zadania rekrutacyjne z ogłoszenia)

- [ ] **Zabezpieczenie API (Spring Security):** Dodać blokadę endpointu `POST` za pomocą tokenów **JWT**.
- [ ] **Obsługa błędów Kafki (DLQ):** Skonfigurować Dead Letter Queue, żeby uszkodzone wiadomości nie blokowały kolejki.
- [ ] **Zmiana formatu ID:** Przejść z auto-inkrementacji (1, 2, 3...) na bezpieczne i wydajne dla bazy danych **UUID v7**.
- [ ] **Automatyczne testy (CI/CD):** Stworzyć skrypt GitHub Actions, który sam odpala testy JUnit przy każdym wrzuceniu kodu na repozytorium.

---

## 🚀 Szybki start

1. Odpalenie bazy i Kafki: `docker compose up -d`
2. Start aplikacji: `cd shop && ./gradlew bootRun`
3. Start testów: `cd shop && ./gradlew test`

---

## 🔌 Przykładowe Flow & REST API (Instrukcja testowania)

Aby prawidłowo przetestować działanie systemu i uniknąć błędu `500` (wynikającego z braku produktu), należy najpierw zasilić magazyn, a dopiero potem złożyć zamówienie.

### KROK 1: Dodanie produktu do magazynu (Zasilenie stocku)

**Endpoint:** `POST http://localhost:8080/api/orders/stock`

**Nagłówki:** `Content-Type: application/json`

**Przykładowy Payload (Request Body):**
```json
{
  "productCode": "Klawiatura mechaniczna",
  "availableQuantity": 10
}
```

**Przykładowe żądanie cURL:**
```bash
curl -X POST http://localhost:8080/api/orders/stock \
  -H "Content-Type: application/json" \
  -d '{"productCode": "Klawiatura mechaniczna", "availableQuantity": 10}'
```

---

### KROK 2: Złożenie zamówienia (Sprawdzenie magazynu i wysyłka Kafka)

**Endpoint:** `POST http://localhost:8080/api/orders`

**Nagłówki:** `Content-Type: application/json`

**Przykładowy Payload (Request Body):**
```json
{
  "productName": "Klawiatura mechaniczna",
  "quantity": 1,
  "price": 350.00
}
```

**Przykładowe żądanie cURL:**
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"productName": "Klawiatura mechaniczna", "quantity": 1, "price": 350.00}'
```

*Uwaga: Próba zamówienia ilości większej niż dostępna w kroku 1 (np. `quantity: 15`) zwróci poprawny status `400 Bad Request` z informacją o braku towaru.*