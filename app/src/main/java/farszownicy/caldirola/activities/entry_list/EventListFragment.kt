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
import farszownicy.caldirola.crud_activities.EditEventActivity
import farszownicy.caldirola.crud_activities.EditTaskActivity
import farszownicy.caldirola.utils.Constants
import farszownicy.caldirola.utils.EventListAdapter
import kotlinx.android.synthetic.main.fragment_event_list.view.*
import kotlin.time.ExperimentalTime

class EventListFragment : Fragment() {

    private val eventsAdapter = EventListAdapter()

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

        eventsAdapter.onEventClick = { event ->
            val intent = Intent(activity, EditEventActivity::class.java)
            intent.putExtra("ID", event.id)
            requireActivity().startActivityForResult(intent, Constants.EDIT_EVENT_CODE)
        }

        recyclerView.adapter = eventsAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        root.past_entries_chBox.setOnCheckedChangeListener{
                _, isChecked ->
            eventsAdapter.showPastEvents = isChecked
            eventsAdapter.notifyDataSetChanged()}

        return root
    }
}