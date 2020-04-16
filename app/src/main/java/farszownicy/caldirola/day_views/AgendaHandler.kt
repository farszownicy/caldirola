package farszownicy.caldirola.day_views

import TaskSliceView
import android.content.Context
import android.graphics.Rect
import farszownicy.caldirola.data_classes.Event
import farszownicy.caldirola.data_classes.TaskSlice
import kotlin.time.ExperimentalTime

//Tworzy i zwraca widoki eventów i godzin
class AgendaHandler(private var mContext: Context) {

    var mEventClickListener: EventView.OnEventClickListener? = null
    @ExperimentalTime
    var mTaskSliceClickListener: TaskSliceView.OnTaskClickListener? = null

    fun getEventView(
        event: Event, eventBound: Rect?, hourHeight: Int,
        separateHeight: Int
    ): EventView {
        val eventView = EventView(mContext)
        eventView.mEvent = event
        eventView.setPosition(eventBound!!, hourHeight)//- separateHeight * 2)
        eventView.setOnEventClickListener(mEventClickListener)
        return eventView
    }


    fun getHourView(hour: Int): HourView {
        val hourView = HourView(mContext)
        hourView.setText(String.format("%1$2s:00", hour))
        return hourView
    }

    @ExperimentalTime
    fun getTaskSliceView(
        slice: TaskSlice, eventBound: Rect?, hourTextHeight: Int,
        separateHeight: Int
    ): TaskSliceView {
        val tsView = TaskSliceView(mContext)
        tsView.mTaskSlice = slice
        tsView.setPosition(eventBound!!, hourTextHeight)//- separateHeight * 2)
        tsView.setOnEventClickListener(mTaskSliceClickListener)
        return tsView
    }

    fun setOnEventClickListener(listener: EventView.OnEventClickListener?) {
        mEventClickListener = listener
    }

    @ExperimentalTime
    fun setOnTaskSliceClickListener(listener: TaskSliceView.OnTaskClickListener?) {
        mTaskSliceClickListener = listener
    }

}