package com.example.college_planner;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Optional;

public class Exam extends Assignment {
    private final Duration duration;
    private final String location;

    public Exam(String name, Class _class, String description, LocalDateTime dueDate, Duration duration, String location) {
        super(name, _class, description, dueDate);
        this.duration = duration;
        this.location = location;
    }

    @Override
    public Optional<String> time() {
        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        LocalTime examEnd = dueDate.plus(duration).toLocalTime();
        return Optional.of(
                String.format(Locale.getDefault(), "%s - %s", timeFormat.format(dueDate.toLocalTime()), timeFormat.format(examEnd))
        );
    }

    @Override
    public String shortDescription() {
        return location;
    }
}
