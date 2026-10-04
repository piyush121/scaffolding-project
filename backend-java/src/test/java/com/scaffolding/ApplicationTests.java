package com.scaffolding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaffolding.dao.MessageDao;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests. They need a MongoDB running on localhost:27017 (or MONGODB_TEST_URI),
 * and use a separate "scaffolding_test" database that is cleared before every test.
 * The real server is started on a free port and called over HTTP.
 */
class ApplicationTests {

    private static final String DEFAULT_TEST_URI = "mongodb://localhost:27017/scaffolding_test";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static Tomcat tomcat;
    private static MessageDao messageDao;
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws Exception {
        String testUri = System.getenv("MONGODB_TEST_URI");
        messageDao = new MessageDao(Application.connect(testUri != null ? testUri : DEFAULT_TEST_URI));
        tomcat = Application.startServer(0, messageDao);
        baseUrl = "http://localhost:" + tomcat.getConnector().getLocalPort() + "/api";
    }

    @AfterAll
    static void stopServer() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    @BeforeEach
    void clearDatabase() {
        messageDao.deleteAll();
    }

    @Test
    void restCreateThenListLatest() throws Exception {
        HttpResponse<String> created = send(HttpRequest.newBuilder(URI.create(baseUrl + "/messages"))
                .POST(HttpRequest.BodyPublishers.noBody()));
        assertEquals(200, created.statusCode());
        JsonNode message = objectMapper.readTree(created.body());
        assertFalse(message.get("id").asText().isEmpty());
        assertEquals("Hello World", message.get("content").asText());
        assertTrue(message.get("createdAt").isNumber());

        assertEquals(200, send(HttpRequest.newBuilder(URI.create(baseUrl + "/messages"))
                .POST(HttpRequest.BodyPublishers.noBody())).statusCode());

        HttpResponse<String> limited = send(HttpRequest.newBuilder(URI.create(baseUrl + "/messages/latest?limit=1")));
        assertEquals(200, limited.statusCode());
        assertEquals(1, objectMapper.readTree(limited.body()).size());

        HttpResponse<String> all = send(HttpRequest.newBuilder(URI.create(baseUrl + "/messages/latest")));
        assertEquals(200, all.statusCode());
        assertEquals(2, objectMapper.readTree(all.body()).size());
    }

    @Test
    void invalidLimitReturnsBadRequest() throws Exception {
        for (String limit : new String[] {"0", "-1", "abc"}) {
            HttpResponse<String> response = send(
                    HttpRequest.newBuilder(URI.create(baseUrl + "/messages/latest?limit=" + limit)));
            assertEquals(400, response.statusCode());
            assertFalse(objectMapper.readTree(response.body()).get("error").asText().isEmpty());
        }
    }

    @Test
    void corsHeadersForLocalhostOrigin() throws Exception {
        HttpResponse<String> response = send(HttpRequest.newBuilder(URI.create(baseUrl + "/messages/latest"))
                .header("Origin", "http://localhost:3000"));
        assertEquals("http://localhost:3000",
                response.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
    }

    private static HttpResponse<String> send(HttpRequest.Builder request) throws Exception {
        return httpClient.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }
}
