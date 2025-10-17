package com.needbackend_app.needapp.ai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AIResponseMonitor {
    
    private final Map<String, AtomicInteger> invalidResponseCounts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> firstOccurrence = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> lastOccurrence = new ConcurrentHashMap<>();
    
    /**
     * Records an invalid response from AI
     */
    public void recordInvalidResponse(String response) {
        LocalDateTime now = LocalDateTime.now();
        
        invalidResponseCounts.computeIfAbsent(response, k -> new AtomicInteger(0)).incrementAndGet();
        firstOccurrence.putIfAbsent(response, now);
        lastOccurrence.put(response, now);
        
        int count = invalidResponseCounts.get(response).get();
        
        // Log warnings at specific thresholds
        if (count == 5) {
            log.warn("AI returning invalid response '{}' frequently: {} times", response, count);
        } else if (count == 15) {
            log.error("AI returning invalid response '{}' very frequently: {} times. " +
                     "First occurred: {}, Last occurred: {}", 
                     response, count, firstOccurrence.get(response), now);
        } else if (count % 25 == 0) {
            log.error("AI invalid response '{}' count reached: {} times", response, count);
        }
    }
    
    /**
     * Records a successful AI response for monitoring
     */
    public void recordSuccessfulResponse(String category) {
        // Could be used for success rate monitoring in the future
        log.debug("AI successfully suggested category: {}", category);
    }
    
    /**
     * Gets the count of invalid responses for a specific response
     */
    public int getInvalidResponseCount(String response) {
        return invalidResponseCounts.getOrDefault(response, new AtomicInteger(0)).get();
    }
    
    /**
     * Gets all invalid response statistics
     */
    public Map<String, Integer> getAllInvalidResponseStats() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        invalidResponseCounts.forEach((key, value) -> stats.put(key, value.get()));
        return stats;
    }
    
    /**
     * Clears all statistics (useful for testing or periodic cleanup)
     */
    public void clearStats() {
        invalidResponseCounts.clear();
        firstOccurrence.clear();
        lastOccurrence.clear();
        log.info("AI response monitoring statistics cleared");
    }
    
    /**
     * Scheduled task to log statistics every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void logStatistics() {
        if (!invalidResponseCounts.isEmpty()) {
            log.info("AI Invalid Response Statistics: {}", getAllInvalidResponseStats());
            
            // Log summary
            int totalInvalidResponses = invalidResponseCounts.values().stream()
                .mapToInt(AtomicInteger::get)
                .sum();
            log.info("Total invalid AI responses in monitoring period: {}", totalInvalidResponses);
        }
    }
    
    /**
     * Daily cleanup task to prevent memory leaks
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    public void dailyCleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7); // Keep data for 7 days
        
        firstOccurrence.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        lastOccurrence.entrySet().removeIf(entry -> entry.getValue().isBefore(cutoff));
        
        // Also clean up counts for entries that are no longer tracked
        invalidResponseCounts.entrySet().removeIf(entry -> !firstOccurrence.containsKey(entry.getKey()));
        
        log.info("Completed daily cleanup of AI response monitoring data");
    }}