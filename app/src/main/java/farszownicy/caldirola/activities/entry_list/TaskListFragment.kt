package farszownicy.caldirola.activities.entry_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.R
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
        recyclerView.adapter = tasksAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        root.past_entries_chBox.setOnCheckedChangeListener{
                _, isChecked ->
            tasksAdapter.showPastTasks = isChecked
            tasksAdapter.notifyDataSetChanged()}

        val itemTouchHelper = ItemTouchHelper(getLeftSwipeCallback())
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return root
    }

    @ExperimentalTime
    private fun getLeftSwipeCallback() : ItemTouchHelper.SimpleCallback{
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                tasksAdapter.removeTask(viewHolder.adapterPosition)
            }
        }
    }

}