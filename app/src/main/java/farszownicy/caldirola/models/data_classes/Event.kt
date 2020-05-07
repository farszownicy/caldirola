package farszownicy.caldirola.models.data_classes

import farszownicy.caldirola.models.DayItem
import farszownicy.caldirola.models.WeekItem
import java.time.LocalDateTime
import java.util.*

class Event (
    var id: String = "-1",
    var name: String = "",
    var description: String = "",
    startTime: LocalDateTime  = LocalDateTime.now(),
    endTime: LocalDateTime = LocalDateTime.now(),
    var Location: Place? = null
                 ) : AgendaDrawableEntry(startTime, endTime)
{
//    fun copy(): Event {
//        return Event(this)
//    }

//    constructor(other:Event) : this(
//        other.id,
//        other.name,
//        other.description,
//        other.startTime,
//        other.endTime,
//        other.Location)

//    constructor(nam: String) : this() {
//        name = nam
//    }

//    public BaseCalendarEvent(Calendar day, String title) {
//    this.mPlaceHolder = true;
//    this.mTitle = title;
//    this.mLocation = "";
//    setInstanceDay(day);
//}

//    var weekReference: WeekItem? = null
//    var dayReference: DayItem? = null
//    var instanceDay: Calendar? = null
}