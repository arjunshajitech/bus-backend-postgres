package com.bus.location;

import com.bus.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    final LocationRepository locationRepository;

    @GetMapping("")
    public ResponseEntity<?> searchLocation(@RequestParam("q") String query) {
        return ResponseEntity.status(200).body(locationRepository.findByName(query));
    }
}
