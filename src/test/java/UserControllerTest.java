import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {

    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldPostUsersBirthdayInFuture() throws JsonProcessingException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/users");

        User user = new User(0,
                "email2@yandex.ru",
                "login2",
                "Name",
                LocalDate.of(2030,12,10));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(user)))
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
