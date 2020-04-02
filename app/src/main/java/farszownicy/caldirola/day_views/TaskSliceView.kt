import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.TaskSlice
import kotlinx.android.synthetic.main.task_entry.view.*
import java.text.SimpleDateFormat
import java.util.*
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
            val simpleDateFormat = SimpleDateFormat("HH:mm")
            deadline_tv.text = "${simpleDateFormat.format(deadline.time)}"
//                "${deadline.getDisplayName(Calendar.DAY_OF_MONTH, Calendar.LONG, Locale.ENGLISH)}." +
//                    "${deadline.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)}." +
//                    "${deadline.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.ENGLISH)} " +
//                    "${deadline.getDisplayName(Calendar.HOUR_OF_DAY, Calendar.LONG, Locale.ENGLISH)}:" +
//                    "${deadline.getDisplayName(Calendar.MINUTE, Calendar.LONG, Locale.ENGLISH)}."
            val durationHours = mTaskSlice?.parent!!.duration.inHours.toInt()
            val durationMinutes = mTaskSlice?.parent!!.duration.inMinutes.toInt() - durationHours*60
            duration_tv.text = "${durationHours}hr ${durationMinutes} min"
        }
    var mTaskClickListener: OnTaskClickListener? = null


    init {
        LayoutInflater.from(context).inflate(R.layout.task_entry, this, true)

        super.setOnClickListener {
            if (mTaskClickListener != null) {
                mTaskClickListener!!.onTaskClick(this@TaskSliceView, mTaskSlice)
            }
        }
//        val eventItemClickListener =
//            OnClickListener { v ->
//                if (mTaskClickListener != null) {
//                    mTaskClickListener!!.onTaskViewClick(v, this@TaskView, mTask)
//                }
//            }
    }

    fun setOnEventClickListener(listener: OnTaskClickListener?) {
        mTaskClickListener = listener
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
//        fun onTaskViewClick(
//            view: View?,
//            taskView: TaskView?,
//            data: Task?
//        )
    }
}