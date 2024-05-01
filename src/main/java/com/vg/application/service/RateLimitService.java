package com.vg.application.service;

public interface RateLimitService {

    RateLimitServiceResponse consumeToken(String apiKey);
}
