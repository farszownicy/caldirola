package farszownicy.caldirola.agendacalendar.render

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getColor
import farszownicy.caldirola.Logic.PlanManager
import androidx.core.content.ContextCompat.getDrawable
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
        val simpleDateFormat = DateTimeFormatter.ofPattern(Constants.SHORT_DATETIME_FORMAT)
        deadlineView.text = simpleDateFormat.format(task.taskSliceReference.parent.deadline)
        val partView = view.findViewById<TextView>(R.id.duration_tv)
        val numOfParts = PlanManager.mTaskSlices.filter { ts -> ts.parent.id == task.taskSliceReference.parent.id}.count()
        val whichPart = PlanManager.mTaskSlices.filter { ts ->
            ts.parent.id == task.taskSliceReference.parent.id && PlanManager.isBefore(
                ts.startTime,
                task.taskSliceReference.startTime
            )
        }.count() + 1
        partView.text = "part ${whichPart}/${numOfParts}"
        val durationHours = task.taskSliceReference?.parent!!.duration.inHours.toInt()
        val durationMinutes = task.taskSliceReference?.parent!!.duration.inMinutes.toInt() - durationHours*60
        //durationView.text = "EST. TIME: $durationHours"+"hr $durationMinutes"+"min"
        val backgroundView = view.findViewById<ConstraintLayout>(R.id.item_event_content)

        val bgrDrawable = getDrawable(view.context, R.drawable.round_outline)
        bgrDrawable!!.colorFilter=PorterDuffColorFilter(getColor(view.context, R.color.task_low), PorterDuff.Mode.MULTIPLY)
        when(task.taskSliceReference?.parent!!.priority){
            Constants.PRIORITY_LOW -> bgrDrawable!!.colorFilter= PorterDuffColorFilter(getColor(view.context, R.color.task_low), PorterDuff.Mode.MULTIPLY)
            Constants.PRIORITY_MEDIUM -> bgrDrawable!!.colorFilter=PorterDuffColorFilter(getColor(view.context, R.color.task_medium), PorterDuff.Mode.MULTIPLY)
            Constants.PRIORITY_HIGH -> bgrDrawable!!.colorFilter=PorterDuffColorFilter(getColor(view.context, R.color.task_high), PorterDuff.Mode.MULTIPLY)
            Constants.PRIORITY_URGENT -> bgrDrawable!!.colorFilter=PorterDuffColorFilter(getColor(view.context, R.color.task_urgent), PorterDuff.Mode.MULTIPLY)
        }
        backgroundView.background=bgrDrawable
        backgroundView.clipToOutline = true
    }

    override fun getEventLayout(): Int {
        return R.layout.view_calendar_task
    }
}