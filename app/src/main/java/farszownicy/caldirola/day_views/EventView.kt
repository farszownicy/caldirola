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
            event_name_tv.text = event?.name
            field = event
        }
    var mEventClickListener: OnEventClickListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.event_entry, this, true)

        super.setOnClickListener {
            if (mEventClickListener != null) {
                mEventClickListener!!.onEventClick(this@EventView, mEvent)
            }
        }
        val eventItemClickListener =
            OnClickListener { v ->
                if (mEventClickListener != null) {
                    mEventClickListener!!.onEventViewClick(v, this@EventView, mEvent)
                }
            }
    }

    fun setOnEventClickListener(listener: OnEventClickListener?) {
        mEventClickListener = listener
    }

    fun setPosition(rect: Rect, topMargin: Int, bottomMargin: Int) {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
//        params.topMargin = (rect.top - headerHeight - headerPadding + topMargin
//                - resources.getDimensionPixelSize(R.dimen.cdv_extra_dimen))
        params.height = (rect.height()
                + bottomMargin
                + 3)//resources.getDimensionPixelSize(R.dimen.cdv_extra_dimen))
        params.leftMargin = rect.left
        layoutParams = params
    }

    interface OnEventClickListener {
        fun onEventClick(view: EventView?, data: Event?)
        fun onEventViewClick(
            view: View?,
            eventView: EventView?,
            data: Event?
        )
    }
}