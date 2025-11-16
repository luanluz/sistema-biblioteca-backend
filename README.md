# Sistema de Biblioteca - Backend

API REST para gerenciamento de biblioteca desenvolvida com Spring Boot.

## Tecnologias

- Java 17
- Spring Boot 3.5.7
- PostgreSQL 15
- Flyway (migrations)
- Docker & Docker Compose

## Como executar

### Pré-requisitos

- Docker e Docker Compose instalados

### Subindo o projeto

1. Clone o repositório
2. Copie o arquivo de variáveis de ambiente:
```bash
cp .env.example .env
```

3. Execute o Docker Compose:
```bash
docker compose up -d --build
```

A aplicação estará disponível em [http://localhost:8080](http://localhost:8080).

## Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `POSTGRES_DB` | Nome do banco de dados | `biblioteca_db` |
| `POSTGRES_USER` | Usuário do banco | `biblioteca_user` |
| `POSTGRES_PASSWORD` | Senha do banco | `biblioteca_password` |
| `POSTGRES_HOST` | Host do banco | `postgres` |
| `POSTGRES_PORT` | Porta interna do banco | `5432` |
| `POSTGRES_EXTERNAL_PORT` | Porta externa do banco | `5432` |
| `APP_EXTERNAL_PORT` | Porta externa da aplicação | `8080` |
| `SPRING_PROFILES_ACTIVE` | Perfil ativo | `dev` |

## Perfis

- **dev** (padrão): Desenvolvimento
- **prod**: Produção

## Documentação da API

Swagger UI disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Healthcheck

[http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
