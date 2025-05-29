package zw.co.afc.orbit.sla.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/sla/logs")
public class LogController {
    private static final String LOGS_DIRECTORY = "logs";
    private static final String LOG_FILE_NAME = "sla-service@-human.log";

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLogFile() throws IOException {
        // Path to the log file
        Path logFilePath = Paths.get(LOGS_DIRECTORY, LOG_FILE_NAME);

        // Load file as Resource
        Resource resource = new UrlResource(logFilePath.toUri());

        // Check if file exists and is readable
        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Log file not found or cannot be read");
        }

        // Set Content-Type header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + LOG_FILE_NAME);

        // Return ResponseEntity with file content
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }
}
