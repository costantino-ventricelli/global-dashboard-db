# Analisi Tecnica: Persistence Service (`global-dashboard-db`)

## 1. Responsabilità
Il servizio **Persistence** è l'unico proprietario dei dati del sistema.
Non espone API REST pubbliche, ma agisce come **Consumer Kafka** per gestire le operazioni di lettura/scrittura richieste dagli altri microservizi (in particolare Auth Service e BFF).

## 2. Stack Tecnologico (Rilevato)
*   **Modulo**: `global-dashboard-db`
*   **Framework**: Micronaut 4.4.2
*   **Language**: Java 21
*   **Build Tool**: Gradle (Kotlin DSL)
*   **Database Access**: Micronaut Data JDBC (Repository Pattern)
*   **Migration Tool**: Flyway (PostgreSQL)

## 3. Data Model Attuale (PostgreSQL)
Il DB `globaldashboard` contiene le seguenti tabelle (definite in `V1__init_schema.sql`):

### A. Identity (Users & Roles)
*   `users`: ID, username, email, password_hash.
*   `roles`: ID, name (es. `ROLE_USER`, `ROLE_ADMIN`).
*   `user_roles`: Tabella di join N:M.

### B. Dashboard Domain
*   `dashboards`: ID, user_id, name, description, timestamps.
*   `widgets`: ID, dashboard_id, title, type, config_json (JSONBLOB), positioning (x,y,w,h).

## 4. Integrazione Kafka (Event-Driven Interface)
Il servizio comunicherà asincronamente tramite i seguenti Topic.

### Topic: `persistence.users` (Input)
Gestisce eventi legati all'anagrafica utente.
*   **Eventi Consumati**:
    *   `USER_CREATE_REQUEST`: Richiesta creazione nuovo utente (da Auth Service).
    *   `USER_FIND_REQUEST`: Richiesta dettagli utente per login (da Auth Service).
*   **Eventi Prodotti**:
    *   `USER_CREATED_SUCCESS` / `USER_CREATED_ERROR`
    *   `USER_FOUND` / `USER_NOT_FOUND`

### Topic: `persistence.dashboards` (Input)
Gestisce CRUD delle dashboard e widget.
*   **Eventi Consumati**:
    *   `DASHBOARD_CREATE_REQUEST` (da BFF)
    *   `DASHBOARD_GET_USER_DASHBOARDS` (da BFF - Get all per user)
    *   `WIDGET_ADD_REQUEST` (da BFF)
*   **Eventi Prodotti**:
    *   `DASHBOARD_CREATED`
    *   `DASHBOARD_LIST_RETURNED` (Payload con lista dashboard e widget)
    *   `WIDGET_ADDED`

## 5. Implementazione Repository
Verranno create interfacce `CrudRepository` tramite Micronaut Data:
1.  `UserRepository`: `Optional<User> findByUsername(String username)`
2.  `DashboardRepository`: `List<Dashboard> findByUserId(Long userId)`
3.  `WidgetRepository`: `List<Widget> findByDashboardId(Long dashboardId)`

## 6. Prossimi Passi (Implementation Plan)
1.  Configurare connettore **Micronaut Kafka** in `build.gradle.kts`.
2.  Creare i DTO per gli eventi (es. `UserCreateEvent`, `DashboardDto`).
3.  Implementare i `KafkaListener` per smistare i messaggi sui Service.
4.  Implementare la logica di business nei Service (`UserService`, `DashboardService`).

## 7. Requisiti di Qualità
*   **Test Coverage**: Il codice deve mantenere una copertura minima dell'**80%**.
*   **Report**: Jacoco verrà configurato per fallire la build se la copertura è insufficiente.
