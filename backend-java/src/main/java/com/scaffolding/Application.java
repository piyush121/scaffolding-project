package com.scaffolding;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.scaffolding.config.CorsFilter;
import com.scaffolding.config.DataSeeder;
import com.scaffolding.dao.MessageDao;
import com.scaffolding.rest.MessageRestController;
import com.scaffolding.service.MessageService;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.bson.UuidRepresentation;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Entry point. There is no dependency-injection framework: the DAO, service and
 * controller are created and wired together by hand here, then served by an
 * embedded Tomcat with Jersey (JAX-RS) mapped to /api/*.
 */
public class Application {

    private static final String DEFAULT_MONGODB_URI = "mongodb://localhost:27017/scaffolding";
    private static final String DEFAULT_DATABASE = "scaffolding";

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(env("SERVER_PORT", "8080"));
        MongoDatabase database = connect(env("MONGODB_URI", DEFAULT_MONGODB_URI));

        MessageDao messageDao = new MessageDao(database);
        DataSeeder.seedMessages(messageDao);

        Tomcat tomcat = startServer(port, messageDao);
        System.out.println("Server started on http://localhost:" + tomcat.getConnector().getLocalPort());
        tomcat.getServer().await();
    }

    /** Opens the database named in the URI (or "scaffolding" if the URI has none). */
    public static MongoDatabase connect(String mongoUri) {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClient client = MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                // Store java.util.UUID ids as standard BSON UUIDs (subtype 4)
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build());
        String databaseName = connectionString.getDatabase() != null ? connectionString.getDatabase() : DEFAULT_DATABASE;
        return client.getDatabase(databaseName);
    }

    /** Wires DAO -> service -> controller and starts Tomcat. Pass port 0 to pick a free port. */
    public static Tomcat startServer(int port, MessageDao messageDao) throws IOException, LifecycleException {
        MessageService messageService = new MessageService(messageDao);

        ResourceConfig resourceConfig = new ResourceConfig()
                .register(new MessageRestController(messageService))
                .register(CorsFilter.class)
                .register(JacksonFeature.class)
                .property(ServerProperties.WADL_FEATURE_DISABLE, true);

        String baseDir = Files.createTempDirectory("tomcat").toAbsolutePath().toString();
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir);
        tomcat.setPort(port);
        tomcat.getConnector();

        Context context = tomcat.addContext("", baseDir);
        Tomcat.addServlet(context, "jersey", new ServletContainer(resourceConfig));
        context.addServletMappingDecoded("/api/*", "jersey");

        tomcat.start();
        return tomcat;
    }

    private static String env(String name, String defaultValue) {
        String value = System.getenv(name);
        return value != null && !value.isBlank() ? value : defaultValue;
    }
}
