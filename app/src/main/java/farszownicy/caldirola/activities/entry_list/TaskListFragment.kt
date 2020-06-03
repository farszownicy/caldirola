package farszownicy.caldirola.activities.entry_list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.R
import farszownicy.caldirola.crud_activities.EditTaskActivity
import farszownicy.caldirola.models.data_classes.Task
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.TaskListAdapter
import kotlinx.android.synthetic.main.fragment_event_list.view.*
import kotlin.time.ExperimentalTime

class TaskListFragment : Fragment() {
    private val tasksAdapter = TaskListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @ExperimentalTime
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_event_list, container, false)

        val recyclerView = root.findViewById<RecyclerView>(R.id.el_recycler_view)
        tasksAdapter.onTaskClick = { task ->
            val intent = Intent(activity, EditTaskActivity::class.java)
            intent.putExtra("ID", task.id)
            requireActivity().startActivityForResult(intent, Constants.EDIT_TASK_CODE)
        }
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)


        root.past_entries_chBox.setOnCheckedChangeListener{
                _, isChecked ->
            tasksAdapter.showPastTasks = isChecked
            tasksAdapter.notifyDataSetChanged()}

        return root
    }

}