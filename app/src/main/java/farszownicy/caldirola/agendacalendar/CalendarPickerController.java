package farszownicy.caldirola.agendacalendar;

import farszownicy.caldirola.models.BaseCalendarEntry;
import farszownicy.caldirola.models.DayItem;

import java.util.Calendar;

public interface CalendarPickerController {
    void onDaySelected(DayItem dayItem);

    void onEventSelected(BaseCalendarEntry event);

    void onScrollToDate(Calendar calendar);
}
