package com.example.college_planner;

import android.graphics.Color;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

public class Todo implements Event, Serializable {
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

    @Override
    public String toString() {
        return "Todo{" +
                "task='" + task + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", startTimeAndEndTime=" + startTimeAndEndTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return Objects.equals(task, todo.task) && Objects.equals(description, todo.description) && Objects.equals(date, todo.date) && Objects.equals(startTimeAndEndTime, todo.startTimeAndEndTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(task, description, date, startTimeAndEndTime);
    }
}
