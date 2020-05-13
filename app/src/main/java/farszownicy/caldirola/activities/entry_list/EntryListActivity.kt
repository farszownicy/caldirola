package farszownicy.caldirola.activities.entry_list

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import farszownicy.caldirola.R
import farszownicy.caldirola.activities.entry_list.SectionsPagerAdapter
import farszownicy.caldirola.utils.memory.saveEventsToMemory
import farszownicy.caldirola.utils.memory.saveTasksToMemory
import kotlin.time.ExperimentalTime

class EntryListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_list)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    @ExperimentalTime
    override fun onStop() {
        saveTasksToMemory(this)
        saveEventsToMemory(this)
        super.onStop()
    }
}