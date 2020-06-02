package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.IllegalInterval

class IllegalIntervalDto(var dayOfWeek: Int,
                         var startTime: String,
                         var endTime: String,
                         var title: String) {
    constructor(interval: IllegalInterval):this(
        interval.dayOfWeek?.value ?: 0,
        interval.startTime.toString(),
        interval.endTime.toString(),
        interval.title )
}