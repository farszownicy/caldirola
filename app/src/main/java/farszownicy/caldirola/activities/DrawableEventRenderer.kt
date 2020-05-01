// Ta klasa jest z przykładu z githuba
package farszownicy.caldirola.activities

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.agendacalendar.models.BaseCalendarEvent
import com.example.agendacalendar.render.EventRenderer

import farszownicy.caldirola.R

class DrawableEventRenderer : EventRenderer<BaseCalendarEvent>() {
    // region Class - EventRenderer
    override fun render(view: View, event: BaseCalendarEvent) {
        val nameView = view.findViewById<View>(R.id.event_name_tv) as TextView
        nameView.text = event.name
        val locationView = view.findViewById<View>(R.id.location_tv) as TextView
        locationView.text = event.location
//        val imageView =
//            view.findViewById<View>(R.id.view_agenda_event_image) as ImageView
//        val txtTitle =
//            view.findViewById<View>(R.id.view_agenda_event_title) as TextView
//        val txtLocation =
//            view.findViewById<View>(R.id.view_agenda_event_location) as TextView
//        val descriptionContainer =
//            view.findViewById<View>(R.id.view_agenda_event_description_container) as LinearLayout
//        val locationContainer =
//            view.findViewById<View>(farszownicy.caldirola.R.id.view_agenda_event_location_container) as LinearLayout
//        descriptionContainer.visibility = View.VISIBLE
////        imageView.setImageDrawable(view.context.resources.getDrawable(event.drawableId))
//        txtTitle.setTextColor(view.resources.getColor(R.color.black))
//        txtTitle.text = event.name
//        txtLocation.text = event.location
//        if (event.location.isNotEmpty()) {
//            locationContainer.visibility = View.VISIBLE
//            txtLocation.setText(event.getLocation())
//        } else {
//            locationContainer.visibility = View.GONE
//        }
//        if (event.getName().equals(view.resources.getString(R.string.agenda_event_no_events))) {
//            txtTitle.setTextColor(view.resources.getColor(R.color.black))
//        } else {
//            txtTitle.setTextColor(view.resources.getColor(R.color.theme_text_icons))
//        }
//        descriptionContainer.setBackgroundColor(event.color)
//        txtLocation.setTextColor(view.resources.getColor(R.color.theme_text_icons))
    }

    override fun getEventLayout(): Int {
        return R.layout.view_agenda_drawable_event
    }

    override fun getRenderType(): Class<BaseCalendarEvent> {
        return BaseCalendarEvent::class.java
    } // endregion
}