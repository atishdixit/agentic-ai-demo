package com.example.agenticdemo.web;

import com.example.agenticdemo.agent.PipelineOrchestrator;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class RunController {

    private final PipelineOrchestrator orchestrator;

    public RunController(PipelineOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }

    @PostMapping("/run")
    public RunResponse run(@RequestBody RunRequest request) {
        String topic = request.topic() == null ? "" : request.topic().strip();
        if (topic.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "topic must not be empty");
        }
        return orchestrator.run(topic);
    }
}
