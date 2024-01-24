package com.example.college_planner;

import android.graphics.Color;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Describes information needed to render an event in the event list
 */
public interface Event {
    /**
     * Whether the event should be rendered for <code>date</code>
     *
     * @param date the date that is being checked
     * @return if the event should be rendered
     */
    boolean shouldRender(LocalDateTime date);

    String title();

    /**
     * The time at which the event occurs. If none is provided, it is considered all-day.
     *
     * @return the time the event occurs at
     */
    Optional<String> time();

    /**
     * The icon which should be shown with this event
     * TODO: figure out what the best representation of an icon is (may not be string)
     *
     * @return the icon to show with this event
     */
    Optional<String> icon();

    /**
     * A short description shown on the main events page.
     *
     * @return a short description shown on the main events page
     */
    String shortDescription();

    /**
     * A longer description shown on the event's dedicated page.
     *
     * @return a short description shown on the event's dedicated page
     */
    String longDescription();

    /**
     * The color the event should be displayed with.
     *
     * @return the color the event will display with
     */
    Color color();
}
