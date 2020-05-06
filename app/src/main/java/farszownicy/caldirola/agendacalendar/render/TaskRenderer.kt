package farszownicy.caldirola.agendacalendar.render

import android.view.View
import android.widget.TextView
import farszownicy.caldirola.R
import farszownicy.caldirola.models.BaseCalendarEntry
import farszownicy.caldirola.utils.Constants
import kotlinx.android.synthetic.main.task_entry.view.*
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
        durationView.text = "$durationHours hr $durationMinutes min"
    }

    override fun getEventLayout(): Int {
        return R.layout.view_calendar_task
    }
}