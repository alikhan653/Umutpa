package kz.iitu.umutpa.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kz.iitu.umutpa.R
import kz.iitu.umutpa.dashboard.Attempt_Quiz_Section.Create_Test.CreateTestActivity
import kz.iitu.umutpa.dashboard.Attempt_Quiz_Section.TestsActivity
import kz.iitu.umutpa.dashboard.appointment.AppointmentActivity
import kz.iitu.umutpa.dashboard.identify.IdentifyActivity
import kz.iitu.umutpa.dashboard.locations.LocationActivity
import kz.iitu.umutpa.dashboard.medicine.MedicineActivity
import kz.iitu.umutpa.dashboard.result.ResultActivity
import kz.iitu.umutpa.dashboard.support.SupportActivity
import kz.iitu.umutpa.dashboard.todo.TodoActivity
import kz.iitu.umutpa.models.UserDataModel
import kz.iitu.umutpa.profile.UserActivity


class DashbaordFragment : Fragment() {

    private var user = FirebaseAuth.getInstance().currentUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    // rootView
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_dashbaord, container, false)

        //IdentifyPeople :: 1st Card
        rootView.findViewById<CardView>(R.id.card_identify).setOnClickListener {
            startActivity(Intent(activity, IdentifyActivity::class.java))
        }

        //ImportantLocations :: 2nd Card
        rootView.findViewById<CardView>(R.id.card_location).setOnClickListener {
            startActivity(Intent(activity, LocationActivity::class.java))
        }

        //ToDos :: 3rd Card
        rootView.findViewById<CardView>(R.id.card_todo).setOnClickListener {
            startActivity(Intent(activity, UserActivity::class.java))
        }

        //AttemptTest :: 4th Card
        rootView.findViewById<CardView>(R.id.card_training).setOnClickListener {
            startActivity(Intent(activity, TestsActivity::class.java))
        }
        //Medicines :: 5th Card
        rootView.findViewById<CardView>(R.id.card_medicine).setOnClickListener {
            startActivity(Intent(activity, MedicineActivity::class.java))
        }

        //AppointmentView :: 6th Card
        rootView.findViewById<CardView>(R.id.card_appointment).setOnClickListener {
            startActivity(Intent(activity, AppointmentActivity::class.java))
        }

        //SupportAndAddData :: 7th Card
        rootView.findViewById<CardView>(R.id.card_support).setOnClickListener {
            startActivity(Intent(activity, SupportActivity::class.java))
        }

        //making only one admin
        if (user?.email.equals("alihan65321@gmail.com")) {
            rootView.findViewById<ExtendedFloatingActionButton>(R.id.addTest).visibility =
                View.VISIBLE
        }
        //Creating Test
        rootView.findViewById<ExtendedFloatingActionButton>(R.id.addTest).setOnClickListener {
            startActivity(Intent(activity, CreateTestActivity::class.java))
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        readUserData()

    }


    //firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference

    private fun readUserData() {
        mAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        if (user != null) {
            mDatabaseReference.child("Users").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserDataModel::class.java)!!
                        if (userData.role !="Doctor"){
                            rootView.findViewById<CardView>(R.id.card_todo).visibility = View.GONE
                            rootView.findViewById<CardView>(R.id.card_support).visibility = View.GONE
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

}