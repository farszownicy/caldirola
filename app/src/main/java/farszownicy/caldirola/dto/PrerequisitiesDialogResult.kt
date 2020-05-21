package farszownicy.caldirola.dto

import farszownicy.caldirola.models.data_classes.Task

interface PrerequisitiesDialogResult {
    fun getChosenTasks(tasks : List<Task>)
}