import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldPostFilmsWithTooEarlyReleaseDate() throws JsonProcessingException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/films");

        Film film = new Film(0,
                "zombi",
                "very interesting",
                LocalDate.of(1000,01,01),
                100);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(film)))
                .header("Content-type", "application/json")
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(500, response.statusCode());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
