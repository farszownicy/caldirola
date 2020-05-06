package farszownicy.caldirola.models;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;

import farszownicy.caldirola.models.data_classes.Event;
import farszownicy.caldirola.models.data_classes.Place;
import farszownicy.caldirola.models.data_classes.TaskSlice;

/**
 * Event model class containing the information to be displayed on the agenda view.
 */
public class BaseCalendarEntry {

    public static final String NO_EVENTS = "No events";
    /**
     * Id of the event.
     */
    private long mId;
    /**
     * Title of the event.
     */
    private String mName;
    /**
     * Description of the event.
     */
    private String mDescription;
    /**
     * Where the event takes place.
     */
    private String mLocation;
    /**
     * Calendar instance helping sorting the events per section in the agenda view.
     */
    private Calendar mInstanceDay;
    /**
     * Start time of the event.
     */
    private LocalDateTime mStartTime;
    /**
     * End time of the event.
     */
    private LocalDateTime mEndTime;
    /**
     * Tells if this BaseCalendarEvent instance is used as a placeholder in the agenda view, if there's
     * no event for that day.
     */
    private boolean mPlaceHolder;

    /**
     * References to a DayItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private DayItem mDayReference;
    /**
     * References to a WeekItem instance for that event, used to link interaction between the
     * calendar view and the agenda view.
     */
    private WeekItem mWeekReference;

    private Event eventReference;
    private TaskSlice taskSliceReference;

    // region Constructor

    /**
     * Initializes the event
     *
     * @param id          The id of the event.
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param dateStart   The start date of the event.
     * @param dateEnd     The end date of the event.
     * @param allDay      Int that can be equal to 0 or 1.
     * @param duration    The duration of the event in RFC2445 format.
     */
    public BaseCalendarEntry(long id, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration) {
        this.mId = id;
        this.mName = title;
        this.mDescription = description;
        this.mLocation = location;

        this.mStartTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateStart), ZoneId.systemDefault());
        //LocalDateTime.now();
//        this.mStartTime.setTimeInMillis(dateStart);
        this.mEndTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dateEnd), ZoneId.systemDefault());
//        this.mEndTime.setTimeInMillis(dateEnd);
    }

    /**
     * Initializes the event
     * @param title The title of the event.
     * @param description The description of the event.
     * @param location The location of the event.
     * @param startTime The start time of the event.
     * @param endTime The end time of the event.
     * @param allDay Indicates if the event lasts the whole day.
     */
    public BaseCalendarEntry(String title, String description, String location,
                             LocalDateTime startTime, LocalDateTime endTime, boolean allDay) {
        this.mName = title;
        this.mDescription = description;
        this.mLocation = location;
        this.mStartTime = startTime;
        this.mEndTime = endTime;
    }

    public BaseCalendarEntry(BaseCalendarEntry calendarEvent) {
        this.mId = calendarEvent.getId();
        this.mName = calendarEvent.getName();
        this.mDescription = calendarEvent.getDescription();
        this.mLocation = calendarEvent.getLocation();
        this.mStartTime = calendarEvent.getStartTime();
        this.mEndTime = calendarEvent.getEndTime();
    }

    public BaseCalendarEntry(Event event){
        eventReference = event;
        mName = event.getName();
        mDescription = event.getDescription();
        if(event.getLocation() != null)
            mLocation = event.getLocation().getName();
        else
            mLocation = "";
    }

    public BaseCalendarEntry(TaskSlice slice){
        taskSliceReference = slice;
        mName = slice.getParent().getName();
        mDescription = slice.getParent().getDescription();
        StringBuilder sb = new StringBuilder();
        for(Place pl : slice.getParent().getPlaces()) {
            sb.append(pl.getName());
            sb.append(',');
        }
        mLocation = sb.toString();
    }

    /**
     * Constructor for placeholder events, used if there are no events during one dat
     *
     * @param day   The instance day of the event.
     * @param title The title of the event.
     */
    public BaseCalendarEntry(Calendar day, String title) {
        this.mPlaceHolder = true;
        this.mName = title;
        this.mLocation = "";
        setInstanceDay(day);
    }

    // endregion

    // region Getters/Setters


    public String getDescription() {
        return mDescription;
    }


    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public Calendar getInstanceDay() {
        return mInstanceDay;
    }

    public void setInstanceDay(Calendar mInstanceDay) {
        //Log.d("SET INSTANCE DAY", "day" + mInstanceDay.get(Calendar.DAY_OF_MONTH) + " " +
        //        mInstanceDay.get(Calendar.MONTH) + " " + mInstanceDay.get(Calendar.YEAR));
        this.mInstanceDay = mInstanceDay;
        this.mInstanceDay.set(Calendar.HOUR, 0);
        this.mInstanceDay.set(Calendar.MINUTE, 0);
        this.mInstanceDay.set(Calendar.SECOND, 0);
        this.mInstanceDay.set(Calendar.MILLISECOND, 0);
        this.mInstanceDay.set(Calendar.AM_PM, 0);
    }

    public LocalDateTime getEndTime() {
        return mEndTime;
    }

    public void setEndTime(LocalDateTime mEndTime) {
        this.mEndTime = mEndTime;
    }

    public long getId() {
        return mId;
    }

    public void setId(long mId) {
        this.mId = mId;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public LocalDateTime getStartTime() {
        return mStartTime;
    }

    public void setStartTime(LocalDateTime mStartTime) {
        this.mStartTime = mStartTime;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mTitle) {
        this.mName = mTitle;
    }

    public boolean isPlaceHolder() {
        return mPlaceHolder;
    }

    public void setPlaceHolder(boolean mPlaceHolder) {
        this.mPlaceHolder = mPlaceHolder;
    }

    public DayItem getDayReference() {
        return mDayReference;
    }

    public void setDayReference(DayItem mDayReference) {
        this.mDayReference = mDayReference;
    }

    public WeekItem getWeekReference() {
        return mWeekReference;
    }

    public void setWeekReference(WeekItem mWeekReference) {
        this.mWeekReference = mWeekReference;
    }


    public BaseCalendarEntry copy() {
        return new BaseCalendarEntry(this);
    }

    public TaskSlice getTaskSliceReference(){
        return taskSliceReference;
    }

    public Event getEventReference(){
        return eventReference;
    }

    // endregion

    @Override
    public String toString() {
        return "BaseCalendarEvent{"
                + "title='"
                + mName
                + ", instanceDay= "
                + mInstanceDay.getTime()
                + "}";
    }
}
