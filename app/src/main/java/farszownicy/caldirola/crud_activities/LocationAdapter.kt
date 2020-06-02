package farszownicy.caldirola.crud_activities

import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.Place
import kotlinx.android.synthetic.main.location_item.view.*

class LocationAdapter(private val locationList: ArrayList<Place>) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    public class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName = itemView.locationName
        val locationDeleteBtn = itemView.locationSign
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false)
        return LocationViewHolder(itemView)
    }

    override fun getItemCount() = locationList.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val currentLocation = locationList[position]
        holder.locationName.text = currentLocation.name
        holder.locationDeleteBtn.setImageResource((R.drawable.pin))
    }

     fun removeLocation(index: Int)
    {
        locationList.removeAt(index)
        this.notifyItemRemoved(index)
    }

    fun addItem(place: Place)
    {
        locationList.add( place)
        this.notifyItemInserted(locationList.size - 1)
    }

    fun getItems(): ArrayList<Place> = locationList

}