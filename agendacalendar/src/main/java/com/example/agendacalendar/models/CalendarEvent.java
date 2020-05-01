package com.example.agendacalendar.models;

import java.time.LocalDateTime;
import java.util.Calendar;

public interface CalendarEvent {

    long getId();

    void setId(long mId);

    LocalDateTime getStartTime();

    void setStartTime(LocalDateTime mStartTime);

    LocalDateTime getEndTime();

    void setEndTime(LocalDateTime mEndTime);

    String getName();

    void setName(String mTitle);

    Calendar getInstanceDay();

    void setInstanceDay(Calendar mInstanceDay);

    DayItem getDayReference();

    void setDayReference(DayItem mDayReference);

    WeekItem getWeekReference();

    void setWeekReference(WeekItem mWeekReference);

    CalendarEvent copy();
}
