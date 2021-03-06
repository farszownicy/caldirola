package farszownicy.caldirola.utils;


import farszownicy.caldirola.R;
import farszownicy.caldirola.agendacalendar.CalendarManager;
import farszownicy.caldirola.models.WeekItem;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Class containing helper functions for dates
 */
public class DateHelper {

    // region Public methods

    /**
     * Check if two Calendar instances have the same time (by month, year and day of month)
     *
     * @param cal          The first Calendar instance.
     * @param selectedDate The second Calendar instance.
     * @return True if both instances have the same time.
     */
    public static boolean sameDate(Calendar cal, Calendar selectedDate) {
        return cal.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH)
                && cal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_MONTH) == selectedDate.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean sameDate(LocalDateTime date, LocalDateTime selectedDate) {
        return date.getMonthValue() == selectedDate.getMonthValue()
                && date.getYear() == selectedDate.getYear()
                && date.getDayOfMonth() == selectedDate.getDayOfMonth();
    }

    public static boolean sameDate(LocalDateTime date, Date selectedDate) {
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        return date.getMonthValue() == selectedCal.get(Calendar.MONTH)
                && date.getYear() == selectedCal.get(Calendar.YEAR)
                && date.getDayOfMonth() == selectedCal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Check if a Date instance and a Calendar instance have the same time (by month, year and day
     * of month)
     *
     * @param cal          The Calendar instance.
     * @param selectedDate The Date instance.
     * @return True if both have the same time.
     */
    public static boolean sameDate(Calendar cal, Date selectedDate) {
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(selectedDate);
        return cal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH)
                && cal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR)
                && cal.get(Calendar.DAY_OF_MONTH) == selectedCal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Check if a Date instance is between two Calendar instances' dates (inclusively) in time.
     *
     * @param selectedDate The date to verify.
     * @param startDate     The start time.
     * @param endDate       The end time.
     * @return True if the verified date is between the two specified dates.
     */
    public static boolean isBetweenInclusive(Date selectedDate, LocalDateTime startDate, LocalDateTime endDate) {
        // Check if we deal with the same day regarding startCal and endCal
        LocalDateTime ldtSelectedDate = convertToLDT(selectedDate);
        if (sameDate(ldtSelectedDate, startDate) || sameDate(ldtSelectedDate, endDate)) {
            return true;
        } else {
            return ldtSelectedDate.isAfter(startDate) && ldtSelectedDate.isBefore(endDate);
        }
    }

    public static boolean isBetweenInclusive(LocalDateTime selectedDate, LocalDateTime startDate, LocalDateTime endDate) {
        if (sameDate(selectedDate, startDate)) {
            return true;
        } else {
            return selectedDate.isAfter(startDate) && selectedDate.isBefore(endDate);
        }
    }

    public static LocalDateTime convertToLDT(Date date){
        return Instant.ofEpochMilli( date.getTime() )
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
    }

    public static Date convertToDate(LocalDateTime date){
        return Date.from(date.
                atZone( ZoneId.systemDefault()).
                toInstant());
    }

    /**
     * Check if Calendar instance's date is in the same week, as the WeekItem instance.
     *
     * @param cal  The Calendar instance to verify.
     * @param week The WeekItem instance to compare to.
     * @return True if both instances are in the same week.
     */
    public static boolean sameWeek(Calendar cal, WeekItem week) {
        return (cal.get(Calendar.WEEK_OF_YEAR) == week.getWeekInYear() && cal.get(Calendar.YEAR) == week.getYear());
    }

    /**
     * Convert a millisecond duration to a string format
     *
     * @param millis A duration to convert to a string form
     * @return A string of the form "Xd" or either "XhXm".
     */
    public static String getDuration(Context context, long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) {
            sb.append(days);
            sb.append(context.getResources().getString(R.string.agenda_event_day_duration));
            return (sb.toString());
        } else {
            if (hours > 0) {
                sb.append(hours);
                sb.append("h");
            }
            if (minutes > 0) {
                sb.append(minutes);
                sb.append("m");
            }
        }

        return (sb.toString());
    }

    /**
     * Used for displaying the date in any section of the agenda view.
     *
     * @param calendar The date of the section.
     * @param locale   The locale used by the Sunrise calendar.
     * @return The formatted date without the year included.
     */
    public static String getYearLessLocalizedDate(Calendar calendar, Locale locale) {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.FULL, CalendarManager.getInstance().getLocale());
        String pattern = sdf.toPattern();

        String yearLessPattern = pattern.replaceAll("\\W?[Yy]+\\W?", "");
        SimpleDateFormat yearLessSDF = new SimpleDateFormat(yearLessPattern, locale);
        String yearLessDate = yearLessSDF.format(calendar.getTime()).toUpperCase();
        if (yearLessDate.endsWith(",")) {
            yearLessDate = yearLessDate.substring(0, yearLessDate.length() - 1);
        }
        return yearLessDate;
    }

    // endregion
}