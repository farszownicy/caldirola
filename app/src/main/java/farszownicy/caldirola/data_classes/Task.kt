package farszownicy.caldirola.data_classes

import java.util.*

data class Task(var id: String = "-1",
                var deadline: Date = Date(),
                var duration: Int = 0,
                var priority: Int = 0,
                var prerequisites: List<Task>,
                var divisible: Boolean = false,
                var places: List<Place> = listOf<Place>()
)
    : CalendarEntry()