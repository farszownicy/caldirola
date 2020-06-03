package farszownicy.caldirola.crud_activities.fragments

import farszownicy.caldirola.models.data_classes.Task

interface PrerequisitiesDialogResult {
    fun getChosenTasks(tasks : List<Task>)
}