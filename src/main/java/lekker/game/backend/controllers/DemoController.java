package lekker.game.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/demo")
@RequiredArgsConstructor
public class DemoController {
    
    @GetMapping()
    public ResponseEntity<String> greet() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
    
}
