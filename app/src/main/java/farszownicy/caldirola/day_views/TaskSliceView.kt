import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat.getColor
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.TaskSlice
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.Constants.DATETIME_FORMAT
import kotlinx.android.synthetic.main.task_entry.view.*
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

//Widok taska
@ExperimentalTime
class TaskSliceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr)  {

    @ExperimentalTime
    var mTaskSlice: TaskSlice? = null
        set(task){
            field = task
            task_name_tv.text = mTaskSlice?.parent?.name
            val deadline = mTaskSlice?.parent!!.deadline
            val simpleDateFormat = DateTimeFormatter.ofPattern(DATETIME_FORMAT)
            deadline_tv.text = "DEADLINE: ${simpleDateFormat.format(deadline)}"
//                "${deadline.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.ENGLISH)}." +
//                    "${deadline.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)}." +
//                    "${deadline.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.ENGLISH)} " +
//                    "${deadline.getDisplayName(Calendar.HOUR_OF_DAY, Calendar.LONG, Locale.ENGLISH)}:" +
//                    "${deadline.getDisplayName(Calendar.MINUTE, Calendar.LONG, Locale.ENGLISH)}."
            val durationHours = mTaskSlice?.parent!!.duration.inHours.toInt()
            val durationMinutes = mTaskSlice?.parent!!.duration.inMinutes.toInt() - durationHours*60
            val places = mTaskSlice?.parent!!.places.map{e -> e.name}
            var placesToText: String = ""
            places.forEach{e -> placesToText += "$e    "}
            places_tv.text = placesToText
            when(mTaskSlice?.parent!!.priority){
                Constants.PRIORITY_LOW -> item_event_content.setBackgroundColor(getColor(context, R.color.task_low))
                Constants.PRIORITY_MEDIUM -> item_event_content.setBackgroundColor(getColor(context, R.color.task_medium))
                Constants.PRIORITY_HIGH -> item_event_content.setBackgroundColor(getColor(context, R.color.task_high))
                Constants.PRIORITY_URGENT -> item_event_content.setBackgroundColor(getColor(context, R.color.task_urgent))
            }
        }
    var mTaskClickListener: OnTaskClickListener? = null
    var mTaskLongClickListener: OnTaskLongClickListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.task_entry, this, true)
        super.setOnClickListener {
            if (mTaskClickListener != null) {
                mTaskClickListener!!.onTaskClick(this@TaskSliceView, mTaskSlice)
            }
        }
        super.setOnLongClickListener {
            if (mTaskLongClickListener != null) {
                mTaskLongClickListener!!.onTaskClick(this@TaskSliceView, mTaskSlice)
            }
            true
        }

    }

    fun setOnTaskClickListener(listener: OnTaskClickListener?) {
        mTaskClickListener = listener
    }
    fun setOnTaskLongClickListener(listener: OnTaskLongClickListener?) {
        mTaskLongClickListener = listener
    }

    fun setPosition(rect: Rect, hourTextHeight: Int) {
        val params = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = rect.top  + hourTextHeight

        params.height = (rect.height())
        //+ bottomMargin)
        //+ 3)//resources.getDimensionPixelSize(R.dimen.cdv_extra_dimen))
        params.leftMargin = rect.left
        params.rightMargin = rect.right
        layoutParams = params
    }

    interface OnTaskClickListener {
        fun onTaskClick(view: TaskSliceView?, data: TaskSlice?)
    }

    interface OnTaskLongClickListener {
        fun onTaskClick(view: TaskSliceView?, data: TaskSlice?)
    }
}