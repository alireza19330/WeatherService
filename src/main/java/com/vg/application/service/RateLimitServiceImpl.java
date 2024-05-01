package com.vg.application.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public RateLimitServiceResponse consumeToken(String apiKey) {
        Bucket bucket = this.resolveBucket(apiKey);
        ConsumptionProbe consumptionProbe = bucket.tryConsumeAndReturnRemaining(1);
        return new RateLimitServiceResponse(consumptionProbe.isConsumed(),
                consumptionProbe.getNanosToWaitForRefill() / 1_000_000_000, consumptionProbe.getRemainingTokens());
    }

    private Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String apiKey) {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(60)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

}
