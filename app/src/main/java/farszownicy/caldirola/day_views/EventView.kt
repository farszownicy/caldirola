package farszownicy.caldirola.day_views

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.FrameLayout
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.Event
import kotlinx.android.synthetic.main.event_entry.view.*

//Widok eventu
class EventView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr)  {
    var mEvent: Event? = null
        set(event){
            field = event
            event_name_tv.text = event?.name
            location_tv.text = event?.Location?.name
        }
    var mEventClickListener: OnEventClickListener? = null
    var mEventLongClickListener: OnEventLongClickListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.event_entry, this, true)

        super.setOnClickListener {
            if (mEventClickListener != null) {
                mEventClickListener!!.onEventClick(this@EventView, mEvent)
            }
        }
        super.setOnLongClickListener {
            if (mEventLongClickListener != null) {
                mEventLongClickListener!!.onEventClick(this@EventView, mEvent)
            }
            true
        }
    }

    fun setOnEventClickListener(listener: OnEventClickListener?) {
        mEventClickListener = listener
    }

    fun setOnEventLongClickListener(listener: OnEventLongClickListener?) {
        mEventLongClickListener = listener
    }

    fun setPosition(rect: Rect, topMargin: Int) {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = rect.top  + topMargin

        params.height = (rect.height())
                //+ bottomMargin)
                //+ 3)//resources.getDimensionPixelSize(R.dimen.cdv_extra_dimen))
        params.leftMargin = rect.left
        params.rightMargin = rect.right
        layoutParams = params
    }

    interface OnEventClickListener {
        fun onEventClick(view: EventView?, data: Event?)
//        fun onEventViewClick(
//            view: View?,
//            eventView: EventView?,
//            data: Event?
//        )
    }

    interface OnEventLongClickListener {
        fun onEventClick(view: EventView?, data: Event?)
    }
}