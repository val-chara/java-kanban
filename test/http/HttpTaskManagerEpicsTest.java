package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerEpicsTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    public HttpTaskManagerEpicsTest() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = BaseHttpHandler.getGson();
        client = HttpClient.newHttpClient();
    }

    @BeforeEach
    void setUp() {
        manager.deleteAllTasks();
        manager.deleteAllSubtasks();
        manager.deleteAllEpics();
        taskServer.start();
    }

    @AfterEach
    void shutDown() {
        taskServer.stop();
    }

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic", Status.NEW);
        String epicJson = gson.toJson(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = manager.getAllEpics();
        assertEquals(1, epicsFromManager.size());
        assertEquals("Test epic", epicsFromManager.get(0).getTitle());
    }

    @Test
    void testGetEpics() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic", Status.NEW);
        manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(1, epics.length);
        assertEquals("Test epic", epics[0].getTitle());
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic", Status.NEW);
        Epic createdEpic = manager.createEpic(epic);

        URI url = URI.create("http://localhost:8080/epics/" + createdEpic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic responseEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(createdEpic.getId(), responseEpic.getId());
        assertEquals("Test epic", responseEpic.getTitle());
    }

    @Test
    void testGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test epic", "Testing epic", Status.NEW);
        Epic createdEpic = manager.createEpic(epic);

        Subtask subtask = new Subtask("Test subtask", "Testing subtask", Status.NEW,
                createdEpic.getId(), LocalDateTime.now(), Duration.ofMinutes(30));
        manager.createSubtask(subtask);

        URI url = URI.create("http://localhost:8080/epics/" + createdEpic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length);
        assertEquals("Test subtask", subtasks[0].getTitle());
    }

    @Test
    void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}