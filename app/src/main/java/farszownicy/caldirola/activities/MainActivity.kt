package farszownicy.caldirola.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import farszownicy.caldirola.R
import farszownicy.caldirola.data_classes.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val NAME_KEY = "name"
        const val SURN_KEY = "surname"
        const val TAG = "debug"
    }
    private val db = FirebaseFirestore.getInstance()
    private val userDoc = db.collection("users").document("RL68xbDoaO34qCnG1pti")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addButton.setOnClickListener {
            addUser()
        }
        fetch_button.setOnClickListener{
            fetchUserData()
        }

        plan_button.setOnClickListener{
            val intent = Intent(this, PlanActivity::class.java)
            startActivity(intent)
        }

        addevent_button.setOnClickListener{
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        /**
         * Nasłuchiwanie zmian w  danym dokumencie.
         * activity = this oznacza, że nasłuchiwanie nie będzie działało jak Activity się zatrzyma
         */
        userDoc.addSnapshotListener(this
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
        val userName = nameET.text.toString()
        val userSurname = surnameET.text.toString()

        if(userName.isEmpty() || userSurname.isEmpty())
            return
        val userData = hashMapOf(
            NAME_KEY to userName,
            SURN_KEY to userSurname
        )

        db.collection("users").add(userData).addOnSuccessListener {
                documentReference -> Log.d(TAG, "user added with ID: ${documentReference.id}")  }
            .addOnFailureListener{e -> Log.w(TAG, "Error adding user", e)}
    }
}
