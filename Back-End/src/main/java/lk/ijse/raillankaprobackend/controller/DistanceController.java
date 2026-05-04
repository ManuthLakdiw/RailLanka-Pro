package lk.ijse.raillankaprobackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project RailLanka Pro - Backend
 * @github https://github.com/ManuthLakdiw
 */

@RestController
@RequestMapping("/api/v1/raillankapro/distance")
@CrossOrigin
@RequiredArgsConstructor
public class DistanceController {

    private final ObjectMapper objectMapper;

    @GetMapping()
    public ResponseEntity<?> getDistance(
            @RequestParam String origin,
            @RequestParam String destination) {

        try {
            String GOOGLE_MAPS_API_KEY = "AIzaSyDjEcUHoJ90JabsN5Irbm77zuZd7Zn9K68";
            String url = String.format(
                    "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s&destinations=%s&key=%s",
                    URLEncoder.encode(origin + ", Sri Lanka", "UTF-8"),
                    URLEncoder.encode(destination + ", Sri Lanka", "UTF-8"),
                    GOOGLE_MAPS_API_KEY
            );

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());

            if ("OK".equals(root.get("status").asText())) {
                JsonNode element = root.get("rows").get(0).get("elements").get(0);

                if ("OK".equals(element.get("status").asText())) {
                    String distance = element.get("distance").get("text").asText();
                    String duration = element.get("duration").get("text").asText();

                    Map<String, Object> result = new HashMap<>();
                    result.put("success", true);
                    result.put("distance", distance);
                    result.put("duration", duration);

                    return ResponseEntity.ok(result);
                }
            }

            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Distance not available");

            return ResponseEntity.ok(errorResult);

        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Error fetching distance: " + e.getMessage());

            return ResponseEntity.status(500).body(errorResult);
        }
    }

}
