package farszownicy.caldirola.activities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.Logic.PlanManager
import farszownicy.caldirola.R
import farszownicy.caldirola.models.data_classes.User
import kotlinx.android.synthetic.main.fragment_add_user.*
import kotlin.time.ExperimentalTime

class AddUserFragment : Fragment() {

    companion object {
        const val FIRST_NAME_KEY = "name"
        const val SECOND_NAME_KEY = "surname"
        const val TAG = "debug"
    }
    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("users").document("RL68xbDoaO34qCnG1pti")

    @ExperimentalTime
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_add_user, container, false)

        root.findViewById<Button>(R.id.addButton).setOnClickListener {
            addUser()
        }

        root.findViewById<Button>(R.id.fetch_button).setOnClickListener{
            fetchUserData()
        }

//        root.findViewById<Button>(R.id.plan_button).setOnClickListener{
//            val intent = Intent(requireContext(), CalendarFragment::class.java)
//            startActivity(intent)
//        }
//
//        root.findViewById<Button>(R.id.agenda_btn).setOnClickListener(){
//            val intent = Intent(requireContext(), AgendaFragment::class.java)
//            val bundle = Bundle()
//            bundle.putInt(AgendaFragment.DAY_KEY, LocalDateTime.now().dayOfMonth)
//            bundle.putInt(AgendaFragment.MONTH_KEY, LocalDateTime.now().monthValue)
//            bundle.putInt(AgendaFragment.YEAR_KEY, LocalDateTime.now().year)
//            intent.putExtras(bundle)
//            startActivity(intent)
//        }
//
//        root.findViewById<Button>(R.id.addevent_button).setOnClickListener{
//            val intent = Intent(requireContext(), AddEventActivity::class.java)
//            startActivity(intent)
//        }
//
//        root.findViewById<Button>(R.id.addtask_button).setOnClickListener{
//            val intent = Intent(requireContext(), AddTaskActivity::class.java)
//            startActivity(intent)
//        }
//
//        root.findViewById<Button>(R.id.list_button).setOnClickListener{
//            val intent = Intent(requireContext(), EntryListActivity::class.java)
//            startActivity(intent)
//        }

        /**
         * Nasłuchiwanie zmian w  danym dokumencie.
         * activity = this oznacza, że nasłuchiwanie nie będzie działało jak Activity się zatrzyma
         */
        userDoc.addSnapshotListener(requireActivity()
        ) { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "Current data: ${snapshot.data}")
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
//        loadTasksFromMemory(this)
//        loadEventsFromMemory(this)

        return root
    }

    /**
     * Pobiera dane userów z serwera
     */
    private fun fetchUserData() {
        db.collection("users").get().addOnSuccessListener { result -> for (document in result) {
            val user = document.toObject(User::class.java)
            Log.d(TAG, user.toString())
            //Log.d(TAG, "${document.id} : ${document.data}")
        }
        }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    /**
     * Dodaje usera do Firestore'a
     */
    private fun addUser(){
        val userName = firstNameET.text.toString()
        val userSurname = secondNameET.text.toString()

        if(userName.isEmpty() || userSurname.isEmpty())
            return
        val userData = hashMapOf(
            FIRST_NAME_KEY to userName,
            SECOND_NAME_KEY to userSurname
        )

        db.collection("users").add(userData).addOnSuccessListener {
                documentReference -> Log.d(TAG, "user added with ID: ${documentReference.id}")  }
            .addOnFailureListener{e -> Log.w(TAG, "Error adding user", e)}
    }

}
