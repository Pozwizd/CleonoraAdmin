package com.example.cleonoraadmin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "work-schedule")
public class WorkScheduleConfig {

    private Map<String, WorkDay> schedule;

    @Data
    public static class WorkDay {
        private String start; // Время начала работы
        private String end; // Время окончания работы
        private Lunch lunch; // Обеденное время

        @Data
        public static class Lunch {
            private String start; // Время начала обеда
            private String end; // Время окончания обеда
        }
    }
}