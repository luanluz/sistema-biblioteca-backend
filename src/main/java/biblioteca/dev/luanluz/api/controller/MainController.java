package biblioteca.dev.luanluz.api.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @Value("${api.info.description}")
    private String description;

    @Hidden
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok(description);
    }
}
