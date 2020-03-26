package farszownicy.caldirola.day_views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.Event
import kotlinx.android.synthetic.main.agenda_view.view.*
import java.util.*

//Widok agendy dnia
class CalendarDayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr){
    private var mHourHeight = 0
    private var mEventMarginSide = 4
    private var mHourTextWidth = 120
    private var mHourTextHeight = 120
    private var mSeparateHourHeight = 0
    private var mStartHour = 0
    private var mEndHour = 24

    var mHandler: AgendaHandler? = null
    set(decorator) {
        field = decorator
        refresh()
    }
    var mEvents: List<Event> = ArrayList()
    set(events){
        field = events
        refresh()
    }

    init{
        LayoutInflater.from(context).inflate(R.layout.agenda_view, this, true)
        mHourHeight = resources.getDimensionPixelSize(R.dimen.day_height)
//        if (attrs != null) {
//            val arr =
//                context.obtainStyledAttributes(attrs, R.styleable.CalendarDayView)
//            try {
//                mEventMarginSide = arr.getDimensionPixelSize(
//                    R.styleable.CalendarDayView_eventMarginLeft,
//                    mEventMarginSide
//                )
//                mDayHeight =
//                    arr.getDimensionPixelSize(R.styleable.CalendarDayView_dayHeight, mDayHeight)
//                mStartHour = arr.getInt(R.styleable.CalendarDayView_startHour, mStartHour)
//                mEndHour = arr.getInt(R.styleable.CalendarDayView_endHour, mEndHour)
//            } finally {
//                arr.recycle()
//            }
//        }
        mHandler = AgendaHandler(context)
        refresh()
    }

    fun refresh() {
        drawHourViews()
        drawEvents()
    }

    private fun drawHourViews() {
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

    private fun drawEvents() {
        event_container.removeAllViews()
        for (event in mEvents) {
            val rect = getTimeBound(event)
            val eventView: EventView =
                mHandler!!.getEventView(event, rect, mHourTextHeight/2, mSeparateHourHeight)
            event_container.addView(eventView, eventView.layoutParams)
        }
    }

    private fun getTimeBound(event: Event): Rect {
        val rect = Rect()
        rect.top = getPositionOfTime(event.startTime) + mSeparateHourHeight
        Log.d("DEBUG", "separator: $mSeparateHourHeight")
        Log.d("Debug", "mHourTextHeight: $mHourTextHeight")
        rect.bottom =
            getPositionOfTime(event.endTime) - mSeparateHourHeight
        rect.left = mHourTextWidth + mEventMarginSide
        Log.d("Debug", "eventMargin: $mEventMarginSide")
        rect.right = width - mEventMarginSide
        return rect
    }

    private fun getPositionOfTime(calendar: Calendar): Int {
        val hour = calendar[Calendar.HOUR_OF_DAY] - mStartHour
        val minute = calendar[Calendar.MINUTE]
        return hour * mHourHeight + minute * mHourHeight / 60
    }

    fun setLimitTime(startHour: Int, endHour: Int) {
        require(startHour < endHour) { "start hour must precede end hour" }
        mStartHour = startHour
        mEndHour = endHour
        refresh()
    }
}