package farszownicy.caldirola.crud_activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.Task
import kotlin.time.ExperimentalTime


class PrerequisitesFragment(tasks: List<Task>) : DialogFragment() {

    private lateinit var callback: PrerequisitiesDialogResult

    @ExperimentalTime
    private val prerequisitesListAdapter = PrerequisitesListAdapter(tasks)

    fun setCallback(callback: PrerequisitiesDialogResult){
        this.callback = callback
    }

    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_prerequisites, container, false)
        root.findViewById<Button>(R.id.pre_confirm_button).setOnClickListener{
            onConfirm()
        }
        val recyclerView = root.findViewById<RecyclerView>(R.id.pre_recycler_view)
        recyclerView.adapter = prerequisitesListAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return root
    }

    @ExperimentalTime
    private fun onConfirm(){
        callback.getChosenTasks(prerequisitesListAdapter.chosenTasks)
        this.dismiss()
    }
}
