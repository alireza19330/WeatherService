package com.vg.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vg.application.config.ConfigProperties;
import com.vg.application.config.SpringTestConfig;
import com.vg.infrastructure.api.OpenWeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringTestConfig.class)
public class WeatherServiceTest {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ConfigProperties properties;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getWeatherDescription_returnsDescription_whenCityAndCountryIsProvided() throws Exception {
        // assumption
        String country = "uk";
        String city = "london";
        OpenWeatherResponse response = new OpenWeatherResponse();
        OpenWeatherResponse.Weather weather = new OpenWeatherResponse.Weather();
        weather.setDescription("Sunny");
        response.setWeather(Arrays.asList(weather));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI("https://api.openweathermap.org/data/2.5/weather?q="+city+","+country+"&APPID="+properties.getApiKey())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(response))
                );

        // action
        var actualResponse = weatherService.getWeatherDescription(country, city);
        mockServer.verify();

        // assertion
        assertThat(actualResponse).isEqualTo(response.getWeather().get(0).getDescription());
    }
}
