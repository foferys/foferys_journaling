# Foferys Journaling
=========
[![Java](https://img.shields.io/badge/Java-17-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.x-6DB33F?logo=springboot&logoColor=fff)](#)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=fff)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=fff)](#)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=fff)](#)

## Description

Web app di journaling con Spring Boot + Thymeleaf.

Permette di:

- login con **GitHub OAuth2** oppure **account locale**
- creare/modificare/eliminare pensieri (“**fusa**”) con immagine
- visualizzare un grafico stile **GitHub contributions** basato sulla costanza giornaliera (`JournalingActivity`)
- gestire il **profilo utente** (account page + edit profilo).

## Example Usage

Avvia l’app e apri:

``` 
http://localhost:8080
```

Rotte principali:

``` 
GET  /             (home: richiede login)
GET  /formlogin    (pagina login)
GET  /signup       (registrazione)
GET  /fusa         (lista fusa + contribution graph)
GET  /account      (pagina account)
```

Endpoint utile per debug:

``` 
GET /whoami
```

## Setup (Local)

### Requirements

- Java **17**
- **Maven**
- **Docker** (consigliato per Postgres)

### 1) Start database (Docker Compose)

Il progetto include `docker-compose.yaml` con:

- PostgreSQL su `localhost:5434` (user/pass: `foferys`)
- pgAdmin su `localhost:5051`

```bash
docker compose up -d
```

### 2) Configure `application.properties`

File: `src/main/resources/application.properties`

Esempio (da adattare):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5434/journalingdb
spring.datasource.username=foferys
spring.datasource.password=foferys

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.security.oauth2.client.registration.github.client-id=TUO_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=TUO_CLIENT_SECRET
```

Nota: per un repository pubblico è consigliato spostare i segreti in **variabili d’ambiente** o in un file non versionato (es. `application-local.properties`).

### 3) Run

```bash
mvn spring-boot:run
```

## Features

- **Auth**
  - OAuth2 GitHub (`spring-boot-starter-oauth2-client`) con `CustomOAuth2UserService`
  - login/registrazione locale con password cifrata (BCrypt)
  - security config in `WebSecurityConfig` (form login + oauth2 + logout).

- **Fusa (CRUD)**
  - create/edit/delete sotto `/fusa` tramite `FusaController`
  - immagini salvate in `src/main/resources/static/images/`
  - entità `Fusa` collegata a `User`.

- **Git-style activity graph**
  - `JournalingActivity` conta quante fusa vengono create in un giorno
  - render del grafico in `templates/fusa/index.html` via JS con livelli 0–4.

- **Account**
  - `/account` (profile page) + `/account/modificautente` (edit profilo)
  - gestione errori password con `PasswordMismatchException`.

## Project structure (high level)

- **config**: `WebSecurityConfig`, `CustomOAuth2User*`
- **controller**: `HomeController`, `FusaController`, `AccountController`, `SignupController`
- **models**: `User`, `Fusa`, `JournalingActivity` + DTO/builder
- **services**: `UserService`, `FusaService` + repository JPA
- **templates**: pagine Thymeleaf (`formlogin`, `signup`, `fusa/*`, `account/*`).

## Testing

```bash
mvn test
```

____

## Contributors ✨
[![](https://img.shields.io/badge/contributors-1-46CC12)](#contributors- "Contributors")

<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%">
        <a href="https://github.com/foferys">
          <img src="https://avatars.githubusercontent.com/u/123701797?v=4" width="100px;" alt="foferys"/><br />
          <sub><b>foferys</b></sub>
        </a>
      </td>
    </tr>
  </tbody>
</table>

