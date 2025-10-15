package com.needbackend_app.needapp.globalconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    // This enables @Scheduled annotations for the AIResponseMonitor
}