package farszownicy.caldirola.agendacalendar.render;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import farszownicy.caldirola.R;
import farszownicy.caldirola.models.BaseCalendarEntry;

/**
 * Class helping to inflate our default layout in the AgendaAdapter
 */
public class EventRenderer extends EntryRenderer<BaseCalendarEntry> {

    // region class - EventRenderer

    @Override
    public void render(@NonNull View view, @NonNull BaseCalendarEntry event) {
        TextView nameView =  view.findViewById(R.id.event_name_tv);
        TextView location =  view.findViewById(R.id.location_tv);
        ImageView locationIV =  view.findViewById(R.id.location_iv);
        nameView.setText(event.getName());
        location.setText(event.getLocation());
        if(event.getName().equals(BaseCalendarEntry.NO_EVENTS)) {
            ConstraintLayout background = view.findViewById(R.id.item_event_content);
            background.setBackground(null);
            nameView.setTextSize(16);
            locationIV.setVisibility(View.INVISIBLE);
        }
        if(event.getLocation().length() == 0){
            locationIV.setVisibility(View.INVISIBLE);
        }

//        TextView locationView = (TextView) view.findViewById(R.id.location_tv);
//        locationView.setText(event.getLocation());
//        TextView txtTitle = (TextView) view.findViewById(R.id.view_agenda_event_title);
//        TextView txtLocation = (TextView) view.findViewById(R.id.view_agenda_event_location);
//        LinearLayout descriptionContainer = (LinearLayout) view.findViewById(R.id.view_agenda_event_description_container);
//        LinearLayout locationContainer = (LinearLayout) view.findViewById(R.id.view_agenda_event_location_container);
//
//        descriptionContainer.setVisibility(View.VISIBLE);
//        txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));
//
//        txtTitle.setText(event.getName());
//        txtLocation.setText(event.getLocation());
//        if (event.getLocation().length() > 0) {
//            locationContainer.setVisibility(View.VISIBLE);
//            txtLocation.setText(event.getLocation());
//        } else {
//            locationContainer.setVisibility(View.GONE);
//        }
//
//        if (event.getName().equals(view.getResources().getString(R.string.agenda_event_no_events))) {
//            txtTitle.setTextColor(view.getResources().getColor(android.R.color.black));
//        } else {
//            txtTitle.setTextColor(view.getResources().getColor(R.color.theme_text_icons));
//        }
//        descriptionContainer.setBackgroundColor(event.getColor());
//        txtLocation.setTextColor(view.getResources().getColor(R.color.theme_text_icons));
    }

    @Override
    public int getEventLayout() {
        return R.layout.view_agenda_event;
    }

    // endregion
}
