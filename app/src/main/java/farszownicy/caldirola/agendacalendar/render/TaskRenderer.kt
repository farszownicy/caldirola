package farszownicy.caldirola.agendacalendar.render

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import farszownicy.caldirola.R
import farszownicy.caldirola.models.BaseCalendarEntry
import farszownicy.caldirola.utils.Constants
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime


class TaskRenderer : EntryRenderer<BaseCalendarEntry>() {

    @ExperimentalTime
    override fun render(view: View?, task: BaseCalendarEntry?) {
        val nameView = view!!.findViewById<TextView>(R.id.task_name_tv)
        nameView.text = task!!.name
        val deadlineView = view.findViewById<TextView>(R.id.deadline_tv)
        val simpleDateFormat = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT)
        deadlineView.text = simpleDateFormat.format(task.taskSliceReference.parent.deadline)
        val durationView = view.findViewById<TextView>(R.id.duration_tv)
        val durationHours = task.taskSliceReference?.parent!!.duration.inHours.toInt()
        val durationMinutes = task.taskSliceReference?.parent!!.duration.inMinutes.toInt() - durationHours*60
        val backgroundView = view.findViewById<ConstraintLayout>(R.id.item_event_content)
        when(task.taskSliceReference?.parent!!.priority){
            Constants.PRIORITY_LOW -> backgroundView.setBackgroundColor(getColor(view.context, R.color.task_low))
            Constants.PRIORITY_MEDIUM -> backgroundView.setBackgroundColor(getColor(view.context, R.color.task_medium))
            Constants.PRIORITY_HIGH -> backgroundView.setBackgroundColor(getColor(view.context, R.color.task_high))
            Constants.PRIORITY_URGENT -> backgroundView.setBackgroundColor(getColor(view.context, R.color.task_urgent))
        }
    }

    override fun getEventLayout(): Int {
        return R.layout.view_calendar_task
    }
}