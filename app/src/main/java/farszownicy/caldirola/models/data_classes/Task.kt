package farszownicy.caldirola.models.data_classes

import farszownicy.caldirola.utils.Constants.PRIORITY_LOW
import java.time.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class Task @ExperimentalTime constructor(id: String = "-1",
                                         name: String,
                                         description: String,
                                         var deadline: LocalDateTime,
                                         var duration: kotlin.time.Duration = 0.minutes,
                                         var priority: String = PRIORITY_LOW,
                                         var prerequisites: List<Task> = mutableListOf(),
                                         var divisible: Boolean = false,
                                         var minSliceSize: Int = 0,
                                         var places: List<Place> = mutableListOf(),
                                         var doable: Boolean = true
                                         //val timeSlices: MutableList<Pair<Calendar, Calendar>> = mutableListOf()
) : CalendarEntry(id, name, description) {

//    init{
//        if(!divisible)
//            for(i in 1..3)
//                timeSlices.add(Pair(Calendar.getInstance(), Calendar.getInstance()))
//    }


}