package farszownicy.caldirola.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.Task
import kotlinx.android.synthetic.main.task_list_card.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

class TaskListAdapter() : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    var showPastTasks: Boolean = false
    var onTaskClick: ((Task) -> Unit)? = null

    @ExperimentalTime
    override fun getItemCount(): Int = if (showPastTasks) PlanManager.mTasks.size
                                        else PlanManager.getFutureAndCurrentTasks().size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).
                inflate(R.layout.task_list_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = if (showPastTasks) PlanManager.mTasks[position]
                                else PlanManager.getFutureAndCurrentTasks()[position]
        holder.titleText.text = currentItem.name
        holder.descText.text = currentItem.description
        if(currentItem.places.isNotEmpty()){
            holder.locationText.text = currentItem.places[0].name
        }else {
            holder.locationText.visibility = View.GONE
            holder.locationIcon.visibility = View.GONE
        }
        val df = DateTimeFormatter.ofPattern(Constants.SHORT_DATETIME_FORMAT)
        //Zamienić potem na string.xml
        holder.deadlineText.text = df.format(currentItem.deadline)
        val durationText = "${currentItem.duration.inHours.toInt()}h ${currentItem.duration.inMinutes.toInt() % 60}m"
        holder.durationText.text = durationText

        if(!currentItem.doable) {
            holder.itemView.task_list_const_layout.setBackgroundResource(R.color.colorAccentBleak)
            holder.doableText.setText(R.string.not_doable_text)
        }
        else{
            holder.itemView.task_list_const_layout.setBackgroundResource(R.color.colorPurpleT)
            holder.doableText.setText(R.string.doable_text)
        }
        if(currentItem.deadline < LocalDateTime.now())
            holder.itemView.alpha = 0.7f
        else
            holder.itemView.alpha = 1f
        holder.itemView.setOnClickListener {
            onTaskClick!!.invoke(currentItem)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleText: TextView = itemView.findViewById(R.id.tl_card_title)
        var descText: TextView = itemView.findViewById(R.id.tl_description)
        var locationText: TextView = itemView.findViewById(R.id.tl_location_text)
        var deadlineText: TextView = itemView.findViewById(R.id.tl_deadline)
        var durationText: TextView = itemView.findViewById(R.id.tl_duration)
        var doableText: TextView = itemView.findViewById(R.id.doableTV)
        var locationIcon: ImageView = itemView.findViewById(R.id.tl_location_icon)
        //var background = itemId.
    }
}