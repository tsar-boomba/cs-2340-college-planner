package com.example.college_planner;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

public class Exam extends Assignment {
    private final LocalTime endTime;
    private final String location;

    public Exam(String name, Class _class, String description, LocalDateTime dueDate, LocalTime endTime, String location) {
        super(name, _class, description, dueDate);
        this.endTime = endTime;
        this.location = location;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public Optional<String> time() {
        DateTimeFormatter timeFormat = new DateTimeFormatterBuilder().appendPattern("hh:mm a").toFormatter();
        return Optional.of(
                String.format(Locale.getDefault(), "%s - %s", dueDate.format(new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd hh:mm a").toFormatter()), timeFormat.format(endTime))
        );
    }

    @Override
    public String shortDescription() {
        return location;
    }

    @Override
    public String toString() {
        return "Exam{" +
                "endTime=" + endTime +
                ", location='" + location + '\'' +
                ", _class=" + _class +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Exam exam = (Exam) o;
        return Objects.equals(endTime, exam.endTime) && Objects.equals(location, exam.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endTime, location);
    }
}
