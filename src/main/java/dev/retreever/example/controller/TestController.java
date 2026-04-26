package dev.retreever.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/test/404")
    public ResponseEntity<String> test404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not Found");
    }
}
