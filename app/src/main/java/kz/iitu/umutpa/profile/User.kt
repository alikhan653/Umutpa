package kz.iitu.umutpa.profile

data class User(
    val uid: String,
    val imageUrl: String,
    val name: String,
    val email: String,
    val role: String,
    val dob: String
)