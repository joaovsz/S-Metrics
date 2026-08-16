# S'Metrics — TP1 (Arquitetura de Microservices)

Sistema de estatísticas de música integrado à Spotify Web API. Trabalho individual — sou responsável por toda a arquitetura (Config Server, Discovery Server, API Gateway) e pela implementação dos microservices de domínio `user-service` e `stats-service`.

Escopo desta entrega (TP1): estrutura base da arquitetura, cada microservice de domínio com seu próprio banco, e uma chamada síncrona (OpenFeign) entre `stats-service` e `user-service`. Conforme orientado em aula, **resiliência (Circuit Breaker) não faz parte desta entrega**. Da mesma forma, o token do Spotify é gravado manualmente via API (ver seção "Fluxo de uso").

## Arquitetura

| Serviço | Responsabilidade | Porta | Banco |
|---|---|---|---|
| `config-server` | Centraliza as configurações dos demais serviços (perfil `native`, lendo de `config-server/src/main/resources/config-repo`) | 8888 | — |
| `discovery-server` | Eureka — registro e descoberta dos serviços | 8761 | — |
| `api-gateway` | Ponto único de entrada, roteia `/api/users/**` → user-service e `/api/stats/**` → stats-service | 8080 | — |
| `user-service` | Cadastro de usuários e armazenamento dos tokens do Spotify | 8081 | PostgreSQL |
| `stats-service` | Sincroniza histórico de escuta via Spotify API e calcula Top Artists | 8082 | MongoDB |

Todas as requisições externas devem passar pelo Gateway (porta 8080). As portas dos microservices (8081/8082) ficam expostas apenas para debug local.

## Como rodar (Docker Compose)

Pré-requisito: Docker e Docker Compose.

```bash
docker compose up -d
```

Isso sobe, nesta ordem: `postgres`, `mongo`, `config-server`, `discovery-server` e, em seguida, `api-gateway`, `user-service` e `stats-service`.

Verificações:
- Eureka Dashboard: http://localhost:8761 — deve listar `API-GATEWAY`, `USER-SERVICE` e `STATS-SERVICE`.
- Config Server: http://localhost:8888/user-service/default — deve retornar o JSON com as propriedades do `user-service`.

## Como rodar localmente (sem Docker)

1. Subir os bancos: `docker compose up postgres mongo`
2. Rodar cada serviço, na ordem, via Maven (um terminal por serviço):

```bash
mvn -pl config-server -am spring-boot:run
mvn -pl discovery-server -am spring-boot:run
mvn -pl api-gateway -am spring-boot:run
mvn -pl user-service -am spring-boot:run
mvn -pl stats-service -am spring-boot:run
```

## Fluxo de uso (Postman/Insomnia)

1. **Criar usuário**
   `POST http://localhost:8080/api/users`
   ```json
   { "name": "João Vitor", "spotifyId": "seu_spotify_user_id" }
   ```

2. **Consultar usuário**
   `GET http://localhost:8080/api/users/{id}`

## Estrutura do repositório

```
config-server/     # Spring Cloud Config Server (perfil native)
discovery-server/  # Eureka Server
api-gateway/        # Spring Cloud Gateway (WebFlux)
user-service/       # PostgreSQL + JPA
stats-service/       # MongoDB + OpenFeign + Spotify Web API
docker-compose.yml
```
