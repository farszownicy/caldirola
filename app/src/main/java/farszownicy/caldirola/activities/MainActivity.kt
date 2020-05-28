package farszownicy.caldirola.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.utils.memory.*
import kotlin.time.ExperimentalTime


class MainActivity : AppCompatActivity() {
    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        loadTasksFromMemory(this)
        loadEventsFromMemory(this)
        loadLocationsFromMemory(this)
    }


    @ExperimentalTime
    override fun onStop() {
        if(!PlanManager.memoryUpToDate){
            saveEventsToMemory(this)
            saveTasksToMemory(this)
            PlanManager.memoryUpToDate = true
        }
        super.onStop()
    }
}
