package farszownicy.caldirola.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime


class EventListAdapter() : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    var showPastEvents = false;

    @ExperimentalTime
    override fun getItemCount(): Int = if (showPastEvents) PlanManager.mEvents.size
                                        else PlanManager.getFutureEvents().size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_list_card, parent, false)
        return ViewHolder(itemView)
    }

    @ExperimentalTime
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = if (showPastEvents) PlanManager.mEvents[position]
                                else PlanManager.getFutureEvents()[position]
        holder.titleText.text = currentItem.name
        if(currentItem.Location != null){
            holder.LocationText.text = currentItem.Location!!.name
        } else {
            holder.LocationText.visibility = View.GONE
            holder.LocationIcon.visibility = View.GONE
        }
        holder.descText.text = currentItem.description
        val df = DateTimeFormatter.ofPattern(Constants.SHORT_DATETIME_FORMAT)
        //Zamienić potem na string.xml
        holder.dateText.text = "${df.format(currentItem.startTime)} - ${df.format(currentItem.endTime)}"

        if(currentItem.endTime < LocalDateTime.now())
            holder.itemView.alpha = 0.7f
        else
            holder.itemView.alpha = 1f

        holder.itemView.setOnClickListener {
            //val intent = Intent(holder.itemView.context, Activity::class.java)
            //holder.itemView.context.startActivity(intent)
        }
    }

    @ExperimentalTime
    fun removeEvent(position: Int){
        PlanManager.mEvents.removeAt(position)
        this.notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.el_card_title)
        val descText: TextView = itemView.findViewById(R.id.el_description)
        val LocationText: TextView = itemView.findViewById(R.id.el_location_text)
        val dateText: TextView = itemView.findViewById(R.id.el_date)
        var LocationIcon: ImageView = itemView.findViewById(R.id.el_location_icon)
    }
}