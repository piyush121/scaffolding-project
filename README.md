# Scaffolding Project

A minimal full-stack starter: a Java REST API (embedded Tomcat + Jersey, no Spring) backed by MongoDB, and a Next.js frontend that displays and creates data through that API.

## Architecture (MVC)

```
Browser ── Next.js frontend (View) ──HTTP/JSON──> Tomcat + Jersey backend ──> MongoDB
```

| Layer | Where | What it does |
| --- | --- | --- |
| View | `frontend/src/components/HelloWorldDashboard.tsx`, `frontend/src/lib/api.ts` | Renders messages and calls the REST API with `fetch` |
| Controller | `backend-java/.../rest/MessageRestController.java` | JAX-RS (Jersey) REST endpoints under `/api/messages` |
| Service | `backend-java/.../service/MessageService.java` | Business logic |
| DAO | `backend-java/.../dao/MessageDao.java` | Reads and writes the `messages` collection with the MongoDB Java driver |
| Model | `backend-java/.../model/Message.java` | The message; stored in MongoDB and returned as JSON by the API |

```
Scaffolding Project/
├── backend-java/     # Embedded Tomcat + Jersey + MongoDB driver (Gradle wrapper included)
│   └── src/main/java/com/scaffolding/
│       ├── Application.java   main(): wires DAO -> service -> controller, starts Tomcat
│       ├── config/       CorsFilter, DataSeeder
│       ├── model/        Message
│       ├── dao/          MessageDao
│       ├── rest/         MessageRestController
│       └── service/      MessageService
└── frontend/         # Next.js 15 + React 19 + Tailwind CSS
    └── src/
        ├── app/          layout.tsx, page.tsx
        ├── components/   HelloWorldDashboard.tsx
        └── lib/          api.ts (REST client)
```

## Prerequisites

- JDK 17 or higher
- Node.js 18 or higher
- MongoDB on `localhost:27017` (see `backend-java/README.md` for Homebrew and Docker options)

## Run

1. Start the backend (port 8080):

   ```bash
   cd backend-java
   ./gradlew run --console=plain
   ```

2. Start the frontend (port 3000) in a second terminal:

   ```bash
   cd frontend
   npm install
   npm run dev
   ```

3. Open `http://localhost:3000`.

## REST API

| Method | Path | Description |
| --- | --- | --- |
| GET | `/api/messages/latest?limit=10` | Newest messages first (`limit` is optional, default 10) |
| POST | `/api/messages` | Creates a "Hello World" message and returns it |

```bash
curl http://localhost:8080/api/messages/latest
```

## Tests and checks

```bash
cd backend-java && ./gradlew test     # needs MongoDB; uses the scaffolding_test database
```

```bash
cd frontend && npm run check          # ESLint + TypeScript
```

## Adding a new resource

1. Add a model class in `model/`.
2. Add a DAO class in `dao/`.
3. Add a service in `service/` and a controller in `rest/`.
4. Create and register them in `Application.startServer` (there is no dependency injection, so wiring is explicit).
5. Add the matching `fetch` functions in `frontend/src/lib/api.ts` and a component that uses them.
