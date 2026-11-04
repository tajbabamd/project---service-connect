package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/test")
    public String test() {
        return "JWT authentication successful";
    }

    @GetMapping("/api/customers/test")
    public String customerTest() {
        return "Customer access successful";
    }

    @GetMapping("/api/workers/test")
    public String workerTest() {
        return "Worker access successful";
    }

    @GetMapping("/api/admin/test")
    public String adminTest() {
        return "Admin access successful";
    }
}