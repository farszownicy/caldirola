package farszownicy.caldirola.activities.preferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : AppCompatActivity() {

    private val preferencesAdapter = PreferencesListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferences)

        pref_recycler_view.adapter = preferencesAdapter
        pref_recycler_view.layoutManager = LinearLayoutManager(this)

        pref_add_button.setOnClickListener {
            PlanManager.addEmptyPref()
            preferencesAdapter.notifyItemInserted(PlanManager.mPreferences.size-1)
        }
    }
}
