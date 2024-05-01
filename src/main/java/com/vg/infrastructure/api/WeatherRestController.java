package com.vg.infrastructure.api;

import com.vg.application.exception.WeatherNotFoundException;
import com.vg.application.service.RateLimitService;
import com.vg.application.service.RateLimitServiceResponse;
import com.vg.application.service.WeatherService;
import com.vg.infrastructure.persistence.entity.WeatherItem;
import com.vg.infrastructure.persistence.entity.WeatherItemId;
import com.vg.infrastructure.persistence.repository.ConsumerRepo;
import com.vg.infrastructure.persistence.repository.WeatherItemRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class WeatherRestController {

    public static final String X_RATE_LIMIT_REMAINING = "X-Rate-Limit-Remaining";
    public static final String X_RATE_LIMIT_RETRY_AFTER_SECONDS = "X-Rate-Limit-Retry-After-Seconds";

    private final WeatherService weatherService;
    private final RateLimitService rateLimitService;
    private final WeatherItemRepo weatherItemRepo;
    private final ConsumerRepo consumerRepo;

    @GetMapping("weather")
    @Transactional
    public ResponseEntity<String> getWeather(@Valid @NotBlank @RequestParam String country,
                                             @Valid @NotBlank @RequestParam String city,
                                             @Valid @NotBlank @RequestParam String apiKey) {

        if (!validateApiKey(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("API Key is invalid");
        }

        var rateLimitServiceResponse = rateLimitService.consumeToken(apiKey);
        if (rateLimitServiceResponse.isSuccessful()) {
            try {
                Optional<WeatherItem> weatherItem = weatherItemRepo.findById(new WeatherItemId(country, city));
                String description;

                if (weatherItem.isPresent()) {
                    description = weatherItem.get().getDescription();
                } else {
                    description = weatherService.getWeatherDescription(country, city);
                    weatherItemRepo.save(new WeatherItem(new WeatherItemId(country, city), description));
                }

                return ResponseEntity.ok()
                        .header(X_RATE_LIMIT_REMAINING, Long.toString(rateLimitServiceResponse.remainingCalls()))
                        .body(description);

            } catch (WeatherNotFoundException exc) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Weather Not Found", exc);
            }
        }

        return buildTooManyRequestResponse(rateLimitServiceResponse);
    }

    private ResponseEntity<String> buildTooManyRequestResponse(RateLimitServiceResponse rateLimitServiceResponse) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(X_RATE_LIMIT_RETRY_AFTER_SECONDS, String.valueOf(rateLimitServiceResponse.secondsToWaitForRefill()))
                .build();
    }

    private boolean validateApiKey(String apiKey){
        return consumerRepo.findByApiKey(apiKey).isPresent();
    }
}
