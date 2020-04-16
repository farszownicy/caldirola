package farszownicy.caldirola.day_views

import TaskSliceView
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.AgendaDrawableEntry
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.Task
import farszownicy.caldirola.data_classes.TaskSlice
import kotlinx.android.synthetic.main.agenda_view.view.*
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.collections.ArrayList
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

//Widok agendy dnia
class CalendarDayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr){

    /**
     * SECTION CLASS VARIABLES
     * */

    //TODO: przerob AllInsertedEntries na SortedListe, ogarnij zeby jak w activity dodasz taska juz po onCreate to zeby on sie pojawił

    private var mHourHeight = 0
    private var mEventMarginSide = 4
    private var mHourTextWidth = 120
    private var mHourTextHeight = 120
    private var mSeparateHourHeight = 0
    private var mStartHour = 0
    private var mEndHour = 24

    var mHandler: AgendaHandler? = null

    init{
        LayoutInflater.from(context).inflate(R.layout.agenda_view, this, true)
        mHourHeight = resources.getDimensionPixelSize(R.dimen.day_height)
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
        for (event in PlanManager.mEvents) {
            val rect = getTimeBound(event)
            val eventView: EventView =
                mHandler!!.getEventView(event, rect, mHourTextHeight/2, mSeparateHourHeight)
            event_container.addView(eventView, eventView.layoutParams)
        }
    }

    @ExperimentalTime
    fun drawTasks() {
        for(taskSlice in PlanManager.mTaskSlices) {
            val rect = getTimeBound(taskSlice)
            val taskSliceView: TaskSliceView =
                mHandler!!.getTaskSliceView(taskSlice, rect, mHourTextHeight/2 , mSeparateHourHeight)
            event_container.addView(taskSliceView, taskSliceView.layoutParams)
        }
    }

    private fun getTimeBound(entry: AgendaDrawableEntry): Rect {
        val rect = Rect()
        rect.top = getPositionOfTime(entry.startTime) + mSeparateHourHeight
        Log.d("DEBUG", "separator: $mSeparateHourHeight")
        Log.d("Debug", "mHourTextHeight: $mHourTextHeight")
        rect.bottom =
            getPositionOfTime(entry.endTime) - mSeparateHourHeight
        rect.left = mHourTextWidth + mEventMarginSide
        Log.d("Debug", "eventMargin: $mEventMarginSide")
        rect.right = -mEventMarginSide//width - mEventMarginSide
        Log.d("Debug", "width: $width")
        return rect
    }

    private fun getPositionOfTime(dateTime: LocalDateTime): Int {
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

}