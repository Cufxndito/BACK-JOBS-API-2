# Jobs API – GraphQL

Backend Spring Boot con GraphQL. Endpoint: `POST http://localhost:8080/graphql` — interfaz GraphiQL: `http://localhost:8080/graphiql`.

## Ejecutar

```bash
./mvnw spring-boot:run
```

## Operaciones (definidas en `src/main/resources/graphql/schema.graphqls`)

### Query `jobById(id: ID!)`
Busca un trabajo por identificador (datos de `jobs.json`).

```graphql
query {
  jobById(id: 1) { id name salary hours resources }
}
```

### Mutation `addUser(request: UserRequest)`
Registra un usuario en la API externa ReqRes (`https://reqres.in/api/users`).

```graphql
mutation {
  addUser(request: {
    email: "ana@example.com"
    firstName: "Ana"
    lastName: "Pérez"
    avatar: "https://example.com/ana.png"
    jobId: 1
  }) { id firstName lastName email jobId job { name } }
}
```

## Estructura
- `controller/GraphqlController` – `@QueryMapping` / `@MutationMapping`
- `service/JobService`, `service/UserService` – lógica
- `client/UserApiRepository` – cliente HTTP de ReqRes
