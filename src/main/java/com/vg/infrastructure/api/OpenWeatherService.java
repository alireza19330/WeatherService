package com.vg.infrastructure.api;

import com.vg.application.config.ConfigProperties;
import com.vg.application.exception.WeatherNotFoundException;
import com.vg.application.service.WeatherService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OpenWeatherService implements WeatherService {

    public static final String URL = "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&APPID=%s";

    private final RestTemplate restTemplate;
    private final ConfigProperties properties;

    @PostConstruct
    public void init() {
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler());
    }

    @Override
    public String getWeatherDescription(String country, String city) {
        var response = this.restTemplate
                .getForObject(String.format(URL, city, country, properties.getApiKey()), OpenWeatherResponse.class);

        if (Objects.isNull(response) || Objects.isNull(response.getWeather()) || response.getWeather().isEmpty()) {
            throw new WeatherNotFoundException("Weather not found in the response: " + response);
        }
        return response.getWeather().get(0).getDescription();
    }
}
