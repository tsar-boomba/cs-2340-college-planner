package com.example.college_planner;

import android.graphics.Color;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Assignment implements Event, Serializable {
    private final String name;
    protected final Class _class;
    protected final String description;
    protected final LocalDateTime dueDate;

    public Assignment(String name, Class _class, String description, LocalDateTime dueDate) {
        this.name = name;
        this._class = _class;
        this.description = description;
        this.dueDate = dueDate;
    }

    @Override
    public boolean shouldRender(LocalDateTime date) {
        return date.toLocalDate().equals(dueDate.toLocalDate());
    }

    @Override
    public String title() {
        return name;
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
        return _class.getName();
    }

    @Override
    public String longDescription() {
        return description;
    }

    @Override
    public Color color() {
        return _class.color();
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "name='" + name + '\'' +
                ", _class=" + _class +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(name, that.name) && Objects.equals(_class, that._class) && Objects.equals(description, that.description) && Objects.equals(dueDate, that.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, _class, description, dueDate);
    }
}
