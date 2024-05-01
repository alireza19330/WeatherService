package com.vg.application.service;

public record RateLimitServiceResponse(boolean isSuccessful, long secondsToWaitForRefill, long remainingCalls) {

}
