package kz.iitu.umutpa.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kz.iitu.umutpa.databinding.ActivityUserBinding

class UserActivity:AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private lateinit var adapter : UsersAdapter
    private val userList = mutableListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        adapter = UsersAdapter()
        val mDatabaseReference = FirebaseDatabase.getInstance().reference
        mDatabaseReference.child("Users").get().addOnSuccessListener {
                dataSnapshot ->
            if (dataSnapshot.exists()) {

                for (userSnapshot in dataSnapshot.children) {
                    val userMap = userSnapshot.value as Map<String, Any>
                    val user = User(
                        uid = userSnapshot.key ?: "",
                        imageUrl = userMap["imageUrl"] as String? ?: "",
                        name = userMap["name"] as String? ?: "",
                        email = userMap["email"] as String? ?: "",
                        role = userMap["role"] as String? ?: "",
                        dob = userMap["dob"] as String? ?: ""
                    )
                    if (user.role != "Doctor"){
                        userList.add(user)
                    }
                }

                // Сортировка списка по имени пользователя
                val sortedUserList = userList.sortedBy { it.name }
                // Передача отсортированного списка в адаптер
                Log.d("userDataModel", sortedUserList.toString())
                adapter.submitList(sortedUserList)
                binding.recyclerViewUsers.adapter = adapter
            } else {
                Log.d("userDataModel", "No such document")
            }
        }.addOnFailureListener {
            Log.d("userDataModel", "get failed with ", it)
        }
        adapter.setRcOnItemClick(object : RcOnItemClick {
            override fun onItemClick(uid: String) {
                // Переход на экран с информацией о пользователе
                val intent = Intent(this@UserActivity, DirectionActivity::class.java)
                intent.putExtra("uid", uid)
                startActivity(intent)
            }
        })
    }
}