package farszownicy.caldirola.day_views

import TaskSliceView
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.models.data_classes.Event
import farszownicy.caldirola.models.data_classes.TaskSlice
import farszownicy.caldirola.utils.DateHelper
import kotlinx.android.synthetic.main.agenda_view.view.*
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime

//Widok agendy dnia
class CalendarDayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr){

    /**
     * SECTION CLASS VARIABLES
     * */

    //TODO: przerob AllInsertedEntries na SortedListe, ogarnij zeby jak w activity dodasz taska juz po onCreate to zeby on sie pojawił

    private var mHourHeight = 0f
    private var mEventMarginSide = 4
    private var mHourTextWidth = 120
    private var mHourTextHeight = 120
    private var mSeparateHourHeight = 0
    private var mStartHour = 0
    private var mEndHour = 24

    private lateinit var mDay:LocalDateTime
    lateinit var mEvents: List<Event>
    lateinit var mTaskSlices: List<TaskSlice>

    var mHandler: AgendaHandler? = null

    init{
        LayoutInflater.from(context).inflate(R.layout.agenda_view, this, true)
        mHourHeight = resources.getDimensionPixelSize(R.dimen.day_height).toFloat()
        mHandler = AgendaHandler(context)
        //extractTaskSlices()
        drawHourViews()
    }

    companion object {
        const val SECOND_MILLIS: Long = 1000
        const val MINUTE_MILLIS = SECOND_MILLIS * 60
    }

    /**
     * SECTION: DRAWING AND POSITIONING
     * */

    @ExperimentalTime
    fun refreshWholeView() {
        drawHourViews()
        event_container.removeAllViews()
        drawEvents()
        //distributeTasks()
        drawTasks()
    }

    @ExperimentalTime
    fun refreshEntries() {
        event_container.removeAllViews()
        drawEvents()
        //distributeTasks()
        drawTasks()
    }

    fun drawHourViews() {
        if(dayview_container != null)
            dayview_container.removeAllViews()
        var hourView: HourView? = null
        for (i in mStartHour..mEndHour) {
            hourView = mHandler!!.getHourView(i)
            dayview_container.addView(hourView)
        }
        mHourTextWidth = hourView!!.hourTextWidth.toInt()
        mHourTextHeight = hourView.hourTextHeight.toInt()
        mSeparateHourHeight = hourView.separateHeight.toInt()
    }

    @ExperimentalTime
    fun drawEvents() {
//        event_container.removeAllViews()
        for (event in mEvents) {
            val rect = getTimeBound(event)
            val eventView: EventView =
                mHandler!!.getEventView(event, rect, mHourTextHeight/2, mSeparateHourHeight)
            event_container.addView(eventView, eventView.layoutParams)
        }
    }

    @ExperimentalTime
    fun drawTasks() {
        for(taskSlice in mTaskSlices) {
            val rect = getTimeBound(taskSlice)
            val taskSliceView: TaskSliceView =
                mHandler!!.getTaskSliceView(taskSlice, rect, mHourTextHeight/2 , mSeparateHourHeight)
            event_container.addView(taskSliceView, taskSliceView.layoutParams)
        }
    }

    private fun getTimeBound(entry: AgendaDrawableEntry): Rect {
        val isItOneDayEntry = DateHelper.sameDate(entry.startTime,entry.endTime)
        val startTime: LocalDateTime
        val endTime: LocalDateTime
        if(isItOneDayEntry){
            startTime = entry.startTime
            endTime = entry.endTime
        }
        else{
            val isCurrentDayTheStartDate = DateHelper.sameDate(entry.startTime, mDay)
            startTime = if(isCurrentDayTheStartDate)
                entry.startTime
            else
                entry.startTime.withHour(0).withMinute(0).withSecond(0)
            val isCurrentDayTheEndDate = DateHelper.sameDate(entry.endTime, mDay)
            endTime = if(isCurrentDayTheEndDate)
                entry.endTime
            else
                entry.endTime.withHour(23).withMinute(59)
        }

        val rect = Rect()
        rect.top = (getPositionOfTime(startTime) + mSeparateHourHeight).toInt()
        Log.d("DEBUG", "separator: $mSeparateHourHeight")
        Log.d("Debug", "mHourTextHeight: $mHourTextHeight")
        rect.bottom =
            (getPositionOfTime(endTime) - mSeparateHourHeight).toInt()
        rect.left = mHourTextWidth + mEventMarginSide
        Log.d("Debug", "eventMargin: $mEventMarginSide")
        rect.right = -mEventMarginSide//width - mEventMarginSide
        Log.d("Debug", "width: $width")
        return rect
    }

    fun getPositionOfTime(dateTime: LocalDateTime): Float {
        val hour = dateTime.hour - mStartHour
        val minute = dateTime.minute//calendar[Calendar.MINUTE]
        return hour * mHourHeight + minute * mHourHeight / 60
    }

    @ExperimentalTime
    fun setLimitTime(startHour: Int, endHour: Int) {
        require(startHour < endHour) { "start hour must precede end hour" }
        mStartHour = startHour
        mEndHour = endHour
        refreshWholeView()
    }

    @ExperimentalTime
    fun setDay(day: Int, month: Int, year: Int) {
        Log.d("DEBUG", "${day} ${month} ${year}")
        mDay = LocalDateTime.now().withYear(year).withMonth(month).withDayOfMonth(day).
                withHour(0).withMinute(0).withSecond(0)
        mEvents = PlanManager.getEventsByDate(mDay)
        mTaskSlices = PlanManager.getTaskSlicesByDate(mDay)
    }

    @ExperimentalTime
    fun updateEvents(){
        mEvents = PlanManager.getEventsByDate(mDay)
    }

    @ExperimentalTime
    fun updateTasks(){
        mTaskSlices = PlanManager.getTaskSlicesByDate(mDay)
    }

}