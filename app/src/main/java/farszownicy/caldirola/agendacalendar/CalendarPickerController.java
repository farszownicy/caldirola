package farszownicy.caldirola.agendacalendar;

import farszownicy.caldirola.models.BaseCalendarEvent;
import farszownicy.caldirola.models.DayItem;

import java.util.Calendar;

public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(BaseCalendarEvent event);

    void onScrollToDate(Calendar calendar);
}
