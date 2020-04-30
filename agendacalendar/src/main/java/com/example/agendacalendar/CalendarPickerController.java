package com.example.agendacalendar;

import com.example.agendacalendar.models.CalendarEvent;
import com.example.agendacalendar.models.DayItem;
import com.example.agendacalendar.models.IDayItem;

import java.util.Calendar;

public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(CalendarEvent event);

    void onScrollToDate(Calendar calendar);
}
