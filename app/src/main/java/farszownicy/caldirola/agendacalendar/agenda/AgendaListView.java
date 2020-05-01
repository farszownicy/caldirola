package farszownicy.caldirola.agendacalendar.agenda;

import farszownicy.caldirola.agendacalendar.CalendarManager;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

import farszownicy.caldirola.models.BaseCalendarEvent;
import farszownicy.caldirola.utils.DateHelper;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * StickyListHeadersListView to scroll chronologically through events.
 */
public class AgendaListView extends StickyListHeadersListView {

    // region Constructors

    public AgendaListView(Context context) {
        super(context);
    }

    public AgendaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AgendaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // endregion

    // region Public methods

    public void scrollToCurrentDate(Calendar today) {
        List<BaseCalendarEvent> events = CalendarManager.getInstance().getEvents();

        int toIndex = 0;
        for (int i = 0; i < events.size(); i++) {
            Log.d("DEBUG", events.get(i).getName());
            if (DateHelper.sameDate(today, events.get(i).getInstanceDay())) {
                toIndex = i;
                break;
            }
        }

        final int finalToIndex = toIndex;
        post(()->setSelection(finalToIndex));
    }

    // endregion
}
