package farszownicy.caldirola.utils

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import org.w3c.dom.Text
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

class TaskListAdapter() : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    @ExperimentalTime
    override fun getItemCount(): Int = PlanManager.mTasks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_list_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = PlanManager.mTasks[position]
        holder.titleText.text = currentItem.name
        holder.descText.text = currentItem.description
        if(currentItem.places.isNotEmpty()){
            holder.locationText.text = currentItem.places[0].name
        }
        val df = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT)
        //Zamienić potem na string.xml
        holder.deadlineText.text = "${df.format(currentItem.deadline)}"
        holder.durationText.text = currentItem.duration.toString()

        holder.itemView.setOnClickListener {
            //val intent = Intent(holder.itemView.context, Activity::class.java)
            //holder.itemView.context.startActivity(intent)
        }
    }

    @ExperimentalTime
    fun removeTask(position: Int){
        PlanManager.mTasks.removeAt(position)
        this.notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleText: TextView = itemView.findViewById(R.id.tl_card_title)
        var descText: TextView = itemView.findViewById(R.id.tl_description)
        var locationText: TextView = itemView.findViewById(R.id.tl_location_text)
        var deadlineText: TextView = itemView.findViewById(R.id.tl_deadline)
        var durationText: TextView = itemView.findViewById(R.id.tl_duration)
    }
}