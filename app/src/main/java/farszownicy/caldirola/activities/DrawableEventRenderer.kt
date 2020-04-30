// Ta klasa jest z przykładu z githuba
package farszownicy.caldirola.activities

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.agendacalendar.render.EventRenderer
//import com.github.tibolte.agendacalendarview.render.EventRenderer

import farszownicy.caldirola.R

class DrawableEventRenderer : EventRenderer<DrawableCalendarEvent>() {
    // region Class - EventRenderer
    override fun render(view: View, event: DrawableCalendarEvent) {
        val imageView =
            view.findViewById<View>(R.id.view_agenda_event_image) as ImageView
        val txtTitle =
            view.findViewById<View>(R.id.view_agenda_event_title) as TextView
        val txtLocation =
            view.findViewById<View>(R.id.view_agenda_event_location) as TextView
        val descriptionContainer =
            view.findViewById<View>(R.id.view_agenda_event_description_container) as LinearLayout
        val locationContainer =
            view.findViewById<View>(farszownicy.caldirola.R.id.view_agenda_event_location_container) as LinearLayout
        descriptionContainer.visibility = View.VISIBLE
        imageView.setImageDrawable(view.context.resources.getDrawable(event.drawableId))
        txtTitle.setTextColor(view.resources.getColor(R.color.black))
        txtTitle.text = event.title
        txtLocation.text = event.location
        if (event.location.isNotEmpty()) {
            locationContainer.visibility = View.VISIBLE
            txtLocation.setText(event.getLocation())
        } else {
            locationContainer.visibility = View.GONE
        }
        if (event.getTitle().equals(view.resources.getString(R.string.agenda_event_no_events))) {
            txtTitle.setTextColor(view.resources.getColor(R.color.black))
        } else {
            txtTitle.setTextColor(view.resources.getColor(R.color.theme_text_icons))
        }
        descriptionContainer.setBackgroundColor(event.color)
        txtLocation.setTextColor(view.resources.getColor(R.color.theme_text_icons))
    }

    override fun getEventLayout(): Int {
        return R.layout.view_agenda_drawable_event
    }

    override fun getRenderType(): Class<DrawableCalendarEvent> {
        return DrawableCalendarEvent::class.java
    } // endregion
}