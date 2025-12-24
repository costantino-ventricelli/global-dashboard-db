# Implementation Plan: Persistence Service (`global-dashboard-db`)

Questo piano dettaglia i passaggi per trasformare il modulo `global-dashboard-db` in un microservizio Event-Driven pienamente funzionante.

## Obiettivo
Implementare la gestione asincrona dei dati tramite Kafka, esponendo consumer per le operazioni su Utenti e Dashboard e garantendo una copertura dei test > 80%.

## User Review Required
> [!IMPORTANT]
> **Breaking Change**: Il modulo non sarà più solo una libreria di dominio ma un'applicazione runnable che ascolta su Kafka.
> **Dependencies**: Verrà aggiunta `micronaut-kafka`.

## Proposed Changes

### 1. Configurazione & Dipendenze
#### [MODIFY] `build.gradle.kts`
*   Aggiungere dipendenza `io.micronaut.kafka:micronaut-kafka`.
*   Configurare plugin Jacoco per enforcement coverage 80%.

#### [MODIFY] `application.yml`
*   Configurare `kafka.bootstrap.servers` (default: `localhost:9092`).
*   Definire i nomi dei topic come proprietà (es. `kafka.topics.users.input: persistence.users`).

### 2. Domain & DTOs (Events)
Creazione delle classi POJO/Record per mappare gli eventi JSON.
#### [NEW] `com.globaldashboard.db.event.user`
*   `UserCreateRequest` (username, email, passwordHash)
*   `UserFindRequest` (username)
*   `UserEvent` (payload generico di risposta)

#### [NEW] `com.globaldashboard.db.event.dashboard`
*   `DashboardCreateRequest`
*   `WidgetAddRequest`
*   `DashboardEvent`

### 3. Data Access Layer (Repositories)
Implementazione dei Repository con Micronaut Data JDBC.
#### [NEW] `com.globaldashboard.db.repository`
*   `UserRepository` (`findByUsername`, `existsByEmail`...)
*   `DashboardRepository` (`findAllByUserId`)
*   `WidgetRepository`

### 4. Kafka Listeners (Consumers)
I listener che ricevono i comandi e invocano i Service.
#### [NEW] `com.globaldashboard.db.listener`
*   `UserListener`: Ascolta su `persistence.users`. Metodi `@Topic("persistence.users") void onUserRequest(@KafkaKey String key, UserCreateRequest request)`.
*   `DashboardListener`: Ascolta su `persistence.dashboards`.

### 5. Services & Business Logic
Logica di orchestrazione: chiama il Repository e produce l'evento di risposta.
#### [NEW] `com.globaldashboard.db.service`
*   `UserService`:
    *   `createUser(...)` -> Salva su DB -> Invia `USER_CREATED_SUCCESS` su Kafka.
    *   `findUser(...)` -> Cerca su DB -> Invia `USER_FOUND` su Kafka.
*   `DashboardService`: Gestione transazionale creazione dashboard/widget.

### 6. Kafka Producers
Client Kafka per inviare le risposte.
#### [NEW] `com.globaldashboard.db.producer`
*   `UserProducer`: Interfaccia annotata `@KafkaClient`.
*   `DashboardProducer`: Interfaccia annotata `@KafkaClient`.

## Verification Plan

### Automated Tests (Coverage > 80%)
*   **Unit Tests**: Testare `UserService` e `DashboardService` mockando Repositories e Producers.
*   **Integration Tests**:
    *   Usare **Testcontainers** (Kafka + Postgres).
    *   Verificare che inviando un messaggio al topic `persistence.users`, il record venga creato su DB e un messaggio di risposta appaia sul topic di output.

### Manual Verification
1.  Avviare Zookeeper/Kafka locale (Docker).
2.  Avviare `global-dashboard-db`.
3.  Usare un tool (es. Kafka UI o script CLI) per inviare un JSON di creazione utente.
4.  Verificare la presenza del record nella tabella `users` (via IDE o psql).
