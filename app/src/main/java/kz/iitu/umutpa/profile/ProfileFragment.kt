package kz.iitu.umutpa.profile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kz.iitu.umutpa.R
import kz.iitu.umutpa.SigninActivity
import kz.iitu.umutpa.databinding.FragmentProfileBinding
import kz.iitu.umutpa.models.UserDataModel


class ProfileFragment : Fragment() {
    private lateinit var logoutButton: LinearLayout
    private lateinit var profileImage: ImageView
    private lateinit var profileName: TextView
    private lateinit var profileEmail: TextView
    private lateinit var profileDateOfBirth: TextView

    //firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mStorage: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enabling separate option menu
        setHasOptionsMenu(true)

        mAuth = FirebaseAuth.getInstance()
        mDatabaseReference = FirebaseDatabase.getInstance().reference

    }

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentProfileBinding.inflate(inflater, container, false)
        val rootView = binding.root
        // Inflate the layout for this fragment


        profileImage = rootView.findViewById(R.id.profile_image)
        profileName = rootView.findViewById(R.id.tvName)
        profileEmail = rootView.findViewById(R.id.tvEmail)
        profileDateOfBirth = rootView.findViewById(R.id.tvDate)
        logoutButton = rootView.findViewById(R.id.btnExit)
        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.finish()
        }
        readUserData()
        return rootView
    }


    private fun showImage(profileImageUrl: String) {
        val user = mAuth.currentUser
        val path = user?.uid + "/profile.png"
        mStorage = FirebaseStorage.getInstance().getReference(path)

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()

        activity?.let {
            Glide.with(it)
                .load(profileImageUrl)
                .apply(options)
                .into(profileImage)
        }
    }

    private fun readUserData() {
        val user: FirebaseUser? = mAuth.currentUser
        mDatabaseReference = FirebaseDatabase.getInstance().reference
        if (user != null) {
            mDatabaseReference.child("Users").child(user.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userData = snapshot.getValue(UserDataModel::class.java)!!
//                        fullName.setText(userData.name)
//                        dob.setText(userData.dob)
//                        email.setText(userData.email)
                        profileName.text =
                            userData.name
                        profileEmail.text =
                            userData.email
                        profileDateOfBirth.text =
                            userData.dob

                        val profileImageUrl = userData.imageUrl
                        showImage(profileImageUrl)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_menu -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(activity, SigninActivity::class.java))
                activity?.finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}