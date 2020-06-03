package farszownicy.caldirola.activities.entry_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.fragment.app.Fragment
import farszownicy.caldirola.R
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlin.time.ExperimentalTime

class EntryListFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_entry_list, container, false)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireContext(), childFragmentManager)
        val viewPager: ViewPager = root.findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = root.findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        return root
    }

    @ExperimentalTime
    override fun onStop() {
        saveTasksToMemory(requireContext())
        saveEventsToMemory(requireContext())
        super.onStop()
    }
}