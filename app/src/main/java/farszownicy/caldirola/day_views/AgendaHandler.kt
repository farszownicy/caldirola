package farszownicy.caldirola.day_views

import android.content.Context
import android.graphics.Rect
import farszownicy.caldirola.data_classes.Event

//Tworzy i zwraca widoki eventów i godzin
class AgendaHandler(private var mContext: Context) {

    var mEventClickListener: EventView.OnEventClickListener? = null

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

    fun setOnEventClickListener(listener: EventView.OnEventClickListener?) {
        mEventClickListener = listener
    }

}