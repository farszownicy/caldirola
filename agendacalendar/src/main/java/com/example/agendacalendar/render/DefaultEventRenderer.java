package com.example.agendacalendar.render;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.agendacalendar.R;

import com.example.agendacalendar.models.BaseCalendarEvent;

/**
 * Class helping to inflate our default layout in the AgendaAdapter
 */
public class DefaultEventRenderer extends EventRenderer<BaseCalendarEvent> {

    // region class - EventRenderer

    @Override
    public void render(@NonNull View view, @NonNull BaseCalendarEvent event) {
        TextView nameView = (TextView) view.findViewById(R.id.event_name_tv);
        nameView.setText(event.getName());
        TextView locationView = (TextView) view.findViewById(R.id.location_tv);
        locationView.setText(event.getLocation());
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
