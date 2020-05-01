package farszownicy.caldirola.utils;

import farszownicy.caldirola.models.DayItem;
import farszownicy.caldirola.models.IDayItem;

import java.util.Calendar;

/**
 * Events emitted by the bus provider.
 */
public class Events {

    public static class DayClickedEvent {

        public Calendar mCalendar;
        public DayItem mDayItem;

        public DayClickedEvent(DayItem dayItem) {
            this.mCalendar = Calendar.getInstance();
            this.mCalendar.setTime(dayItem.getDate());
            this.mDayItem = dayItem;
        }

        public Calendar getCalendar() {
            return mCalendar;
        }

        public DayItem getDay() {
            return mDayItem;
        }
    }

    public static class CalendarScrolledEvent {
    }

    public static class AgendaListViewTouchedEvent {
    }

    public static class EventsFetched {
    }

    public static class ForecastFetched {
    }
}
