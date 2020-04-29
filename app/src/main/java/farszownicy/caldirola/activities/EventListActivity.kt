package farszownicy.caldirola.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.utils.EventListAdapter
import kotlinx.android.synthetic.main.activity_event_list.*

class EventListActivity : AppCompatActivity() {

    private val eventsAdapter = EventListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        el_recycler_view.adapter = eventsAdapter
        el_recycler_view.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(getLeftSwipeCallback())
        itemTouchHelper.attachToRecyclerView(el_recycler_view)
    }

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
                val position = viewHolder.adapterPosition
                PlanManager.mAllInsertedEntries.removeAt(position)
                eventsAdapter.notifyItemRemoved(position)
            }
        }
    }
}
