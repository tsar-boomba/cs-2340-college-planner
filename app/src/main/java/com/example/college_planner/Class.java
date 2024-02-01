package com.example.college_planner;

import android.graphics.Color;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

public class Class implements Event {
    private final String name;
    private final String teacher;
    private final String lectureLocation;
    private final HashSet<DayOfWeek> lectureDays;
    private final LocalTime lectureStartTime;
    private final LocalTime lectureEndTime;
    private final Color color;

    public Class(String name, String teacher, String lectureLocation, HashSet<DayOfWeek> lectureDays, LocalTime lectureStartTime, LocalTime lectureEndTime, Color color) {
        this.name = name;
        this.teacher = teacher;
        this.lectureLocation = lectureLocation;
        this.lectureDays = lectureDays;
        this.lectureStartTime = lectureStartTime;
        this.lectureEndTime = lectureEndTime;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean shouldRender(LocalDateTime date) {
        return lectureDays.contains(date.getDayOfWeek());
    }

    @Override
    public String title() {
        return String.format("%s Lecture", name);
    }

    @Override
    public Optional<String> time() {
        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        return Optional.of(
                String.format(Locale.getDefault(), "%s - %s", timeFormat.format(lectureStartTime), timeFormat.format(lectureEndTime))
        );
    }

    @Override
    public Optional<String> icon() {
        return Optional.empty();
    }

    @Override
    public String shortDescription() {
        return lectureLocation;
    }

    @Override
    public String longDescription() {
        return String.format(Locale.getDefault(), "Lecture for %s, with %s in %s", name, teacher, lectureLocation);
    }

    @Override
    public Color color() {
        return color;
    }

    @Override
    public String toString() {
        return "Class{" +
                "name='" + name + '\'' +
                ", teacher='" + teacher + '\'' +
                ", lectureLocation='" + lectureLocation + '\'' +
                ", lectureDays=" + lectureDays +
                ", lectureTime=" + lectureStartTime +
                ", lectureDuration=" + lectureEndTime +
                ", color=" + color +
                '}';
    }
}
