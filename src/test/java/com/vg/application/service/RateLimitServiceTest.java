package com.vg.application.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RateLimitServiceTest {

    @Autowired
    private RateLimitService rateLimitService;


    @Test
    void rateLimitServiceWorksFineForOneRequest() {
        // assumption
        String apiKey = "api-key";

        // action
        var response = rateLimitService.consumeToken(apiKey);

        // assertion
        assertThat(response.remainingCalls()).isEqualTo(4);
        assertThat(response.isSuccessful()).isEqualTo(true);
    }

    @Test
    void rateLimitServiceLimitTheRate() {
        // assumption
        String apiKey = "api-key";

        // action
        RateLimitServiceResponse response = null;
        for (int i = 0; i < 6; i++) {
            response = rateLimitService.consumeToken(apiKey);
        }

        // assertion
        assertThat(response.remainingCalls()).isEqualTo(0);
        assertThat(response.isSuccessful()).isEqualTo(false);
        assertThat(response.secondsToWaitForRefill()).isGreaterThan(59);
    }

}
