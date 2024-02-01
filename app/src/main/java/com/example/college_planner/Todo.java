package com.example.college_planner;

import android.graphics.Color;
import android.util.Pair;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public class Todo implements Event {
    private final String task;
    private final String description;
    private final Optional<LocalDate> date;
    private final Optional<Pair<LocalTime, Optional<LocalTime>>> startTimeAndEndTime;

    public Todo(String task, String description, Optional<LocalDate> date, Optional<Pair<LocalTime, Optional<LocalTime>>> startTimeAndEndTime) {
        this.task = task;
        this.description = description;
        this.date = date;
        this.startTimeAndEndTime = startTimeAndEndTime;
    }

    public boolean isConstant() {
        return !date.isPresent() && !startTimeAndEndTime.isPresent();
    }

    @Override
    public boolean shouldRender(LocalDateTime date) {
        if (isConstant()) {
            // Neither date nor timeAndDuration is set, will render separately
            return false;
        }

        // If date is present, we just use it to decide what day to render on
        // If no date, time must be present and means it recurs every day
        return this.date.map(localDate -> localDate.equals(date.toLocalDate())).orElse(true);
    }

    @Override
    public String title() {
        return task;
    }

    @Override
    public Optional<String> time() {
        return Optional.empty();
    }

    @Override
    public Optional<String> icon() {
        return Optional.empty();
    }

    @Override
    public String shortDescription() {
        return null;
    }

    @Override
    public String longDescription() {
        return null;
    }

    @Override
    public Color color() {
        return null;
    }
}
