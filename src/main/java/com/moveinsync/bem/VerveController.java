package com.moveinsync.bem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController("/verve")
public class VerveController {


    private final VerveService verveService;
    public final VerveCaching caching;

    public VerveController(VerveService verveService, VerveCaching caching) {
        this.verveService = verveService;
        this.caching = caching;
    }


    @GetMapping("/accept")
    public ResponseEntity<String> acceptRequest(
            @RequestParam("id") Integer id,
            @RequestParam(value = "endpoint", required = false) String endpoint,
            @RequestParam(value = "method", defaultValue = "GET") String method) {

        try {
            verveService.registerRequestIds(id);

            if (endpoint != null) {
                verveService.httpRequest("https://api.example.com", "/data", method, caching.getUniqueCount());
            }

            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            log.error("Error processing request: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("failed");
        }
    }
}
