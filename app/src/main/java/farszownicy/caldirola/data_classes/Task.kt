package farszownicy.caldirola.data_classes

import java.time.LocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.minutes

class Task @ExperimentalTime constructor(id: String = "-1",
                                         name: String,
                                         description: String,
                                         var deadline: LocalDateTime, //= Calendar.getInstance(),
                                         var duration: kotlin.time.Duration = 0.minutes,
                                         var priority: Int = 0,
                                         var prerequisites: List<Task> = mutableListOf(),
                                         var divisible: Boolean = false,
                                         var places: List<Place> = mutableListOf()//,
                                         //val timeSlices: MutableList<Pair<Calendar, Calendar>> = mutableListOf()
) : CalendarEntry(id, name, description) {

//    init{
//        if(!divisible)
//            for(i in 1..3)
//                timeSlices.add(Pair(Calendar.getInstance(), Calendar.getInstance()))
//    }


}