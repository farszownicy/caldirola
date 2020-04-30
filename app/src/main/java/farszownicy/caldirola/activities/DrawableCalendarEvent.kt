// Ta klasa jest z przykładu z githuba
package farszownicy.caldirola.activities
//import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent
//import com.github.tibolte.agendacalendarview.models.CalendarEvent
import com.example.agendacalendar.models.BaseCalendarEvent
import com.example.agendacalendar.models.CalendarEvent
import java.util.*


class DrawableCalendarEvent : BaseCalendarEvent {
    // endregion
// region Public methods
    var drawableId: Int

    // region Constructors
    constructor(
        id: Long,
        color: Int,
        title: String?,
        description: String?,
        location: String?,
        dateStart: Long,
        dateEnd: Long,
        allDay: Int,
        duration: String?,
        drawableId: Int
    ) : super(id, color, title, description, location, dateStart, dateEnd, allDay, duration) {
        this.drawableId = drawableId
    }

    constructor(
        title: String?,
        description: String?,
        location: String?,
        color: Int,
        startTime: Calendar?,
        endTime: Calendar?,
        allDay: Boolean,
        drawableId: Int
    ) : super(title, description, location, color, startTime, endTime, allDay) {
        this.drawableId = drawableId
    }

    constructor(calendarEvent: DrawableCalendarEvent) : super(calendarEvent) {
        drawableId = calendarEvent.drawableId
    }

    // endregion
// region Class - BaseCalendarEvent
    override fun copy(): CalendarEvent {
        return DrawableCalendarEvent(this)
    } // endregion
}