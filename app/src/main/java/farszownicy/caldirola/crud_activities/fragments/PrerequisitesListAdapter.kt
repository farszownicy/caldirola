package farszownicy.caldirola.crud_activities.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.Task
import farszownicy.caldirola.utils.Constants
import kotlinx.android.synthetic.main.task_list_card.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

class PrerequisitesListAdapter(val tasks : List<Task>) : RecyclerView.Adapter<PrerequisitesListAdapter.ViewHolder>() {

    public val chosenTasks = mutableListOf<Task>()

    @ExperimentalTime
    override fun getItemCount(): Int = tasks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).
                inflate(R.layout.task_list_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = tasks[position]

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

        holder.itemView.setOnClickListener {
            if(!chosenTasks.contains(currentItem)){
                chosenTasks.add(currentItem);
                holder.itemView.task_list_const_layout.setBackgroundResource(R.color.colorPurple)
            }else{
                chosenTasks.remove(currentItem)
                if(!currentItem.doable){
                    holder.itemView.task_list_const_layout.setBackgroundResource(R.color.colorAccentBleak)

                }else{
                    holder.itemView.task_list_const_layout.setBackgroundResource(R.color.colorPurpleT)
                }
            }
            //val intent = Intent(holder.itemView.context, Activity::class.java)
            //holder.itemView.context.startActivity(intent)
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