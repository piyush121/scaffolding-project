# Java Backend (Tomcat + Jersey + MongoDB)

A REST API served by an embedded **Tomcat** with **Jersey** (JAX-RS), without Spring. `Application.main` creates the DAO, service and controller by hand and starts the server. It stores messages in **MongoDB**, so data survives restarts. The frontend calls these REST endpoints.

Requires JDK 17 or higher and a running MongoDB.

## 1. Start MongoDB

**Option A: Homebrew (macOS)**

```bash
brew tap mongodb/brew
brew install mongodb-community
brew services start mongodb-community
```

**Option B: Docker**

```bash
cd backend-java
docker compose up -d
```

Either way MongoDB listens on `mongodb://localhost:27017`. The app uses the `scaffolding` database and creates the `messages` collection on first start, seeded with one "Hello World" message.

To use a different server (e.g. MongoDB Atlas), set `MONGODB_URI`:

```bash
export MONGODB_URI="mongodb+srv://user:pass@cluster.example.mongodb.net/scaffolding"
```

## 2. Run

```bash
cd backend-java
./gradlew run --console=plain
```

- REST API: `http://localhost:8080/api/messages/latest` (GET, optional `?limit=`) and `POST http://localhost:8080/api/messages`

Configuration is by environment variable: `SERVER_PORT` (default `8080`) and `MONGODB_URI` (default `mongodb://localhost:27017/scaffolding`). There is no hot reload; restart `./gradlew run` after changing Java code.

Inspect the data with `mongosh`:

```bash
mongosh scaffolding --eval 'db.messages.find().sort({created_at: -1}).limit(5)'
```

## Test

The tests need MongoDB running. They use a separate `scaffolding_test` database (cleared before each test), so your dev data is untouched. Override with `MONGODB_TEST_URI`.

```bash
./gradlew test
```

## Layout

```
src/main/java/com/scaffolding/
├── Application.java   main(): connects to MongoDB, wires the layers, starts Tomcat
├── config/       CorsFilter (JAX-RS response filter), DataSeeder (inserts a sample message into an empty DB)
├── model/        Message (plain Java class)
├── dao/          MessageDao (MongoDB Java driver, collection "messages")
├── rest/         MessageRestController (JAX-RS resource, mapped under /api)
└── service/      MessageService
```

Document shape in the `messages` collection:

```json
{ "_id": UUID("..."), "content": "Hello World", "created_at": ISODate("...") }
```
