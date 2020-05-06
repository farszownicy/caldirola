package farszownicy.caldirola.agendacalendar.agenda;
import farszownicy.caldirola.agendacalendar.render.EventRenderer;
import farszownicy.caldirola.agendacalendar.render.EntryRenderer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import farszownicy.caldirola.agendacalendar.render.TaskRenderer;
import farszownicy.caldirola.models.BaseCalendarEntry;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Adapter for the agenda, implements StickyListHeadersAdapter.
 * Days as sections and CalendarEvents as list items.
 */
public class AgendaAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private List<BaseCalendarEntry> mEvents = new ArrayList<>();
    private List<EntryRenderer<?>> mRenderers = new ArrayList<>();
    private int mCurrentDayColor;

    // region Constructor

    public AgendaAdapter(int currentDayTextColor) {
        this.mCurrentDayColor = currentDayTextColor;
    }

    // endregion

    // region Public methods

    public void updateEvents(List<BaseCalendarEntry> events) {
        this.mEvents.clear();
        this.mEvents.addAll(events);
        notifyDataSetChanged();
    }

    // endregion

    // region Interface - StickyListHeadersAdapter

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        AgendaHeaderView agendaHeaderView = (AgendaHeaderView) convertView;
        if (agendaHeaderView == null) {
            agendaHeaderView = AgendaHeaderView.inflate(parent);
        }
        agendaHeaderView.setDay(getItem(position).getInstanceDay(), mCurrentDayColor);
        return agendaHeaderView;
    }

    @Override
    public long getHeaderId(int position) {
        return mEvents.get(position).getInstanceDay().getTimeInMillis();
    }

    // endregion

    // region Class - BaseAdapter

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public BaseCalendarEntry getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EntryRenderer entryRenderer = new EventRenderer();
        final BaseCalendarEntry entry = getItem(position);

        // Search for the correct event renderer
        for (EntryRenderer renderer : mRenderers) {
            if(entry.getClass().isAssignableFrom(renderer.getRenderType())) {
                if(renderer instanceof TaskRenderer && entry.getTaskSliceReference() != null) {
                    entryRenderer = renderer;
                    break;
                }
                else if(renderer instanceof  EventRenderer && entry.getEventReference() != null){
                    entryRenderer = renderer;
                    break;
                }
            }
        }
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(entryRenderer.getEventLayout(), parent, false);
        entryRenderer.render(convertView, entry);
        return convertView;
    }

    public void addEventRenderer(@NonNull final EntryRenderer<?> renderer) {
        mRenderers.add(renderer);
    }

    // endregion
}
