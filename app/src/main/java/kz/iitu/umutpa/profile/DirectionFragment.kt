package kz.iitu.umutpa.profile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import kz.iitu.umutpa.R
import kz.iitu.umutpa.databinding.FragmentDirectionBinding
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar


class DirectionFragment : Fragment() {
    private lateinit var binding: FragmentDirectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDirectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    private lateinit var mStorage: FirebaseStorage

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mStorage = FirebaseStorage.getInstance()
        mAuth = FirebaseAuth.getInstance()
        senderID = mAuth.getCurrentUser().getUid()
        rootRef = FirebaseDatabase.getInstance().reference
        receiverID = "tWhWuj9BwKXZp7Cmi3w9fzrhKmC3"

        appointImage = binding.directionImage
        appointName = binding.directionGoal
        appointTitle = binding.directionDoctor
        appointAddress = binding.directionAddress
        appointWork = binding.directionComment
        appointDate = binding.appointDate
        appointTime = binding.appointTime

        appointImage.setOnClickListener {
            val gallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, 104)
        }

        appointDate.setOnClickListener {
            hideKeyboard(appointName)
            val dialogInterface: DialogInterface = object : DialogInterface {
                override fun cancel() {
                }

                override fun dismiss() {
                }
            }
            val datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                DatePickerDialog(requireContext())
            } else {
                TODO("VERSION.SDK_INT < N")
            }
            datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
                appointDate.setText(String.format("%d/%d/%d", year, month, dayOfMonth))
            }
            datePickerDialog.onClick(dialogInterface, 1)
            datePickerDialog.show()
        }

        appointTime.setOnClickListener {
            val dialogInterface: DialogInterface = object : DialogInterface {
                override fun cancel() {
                }

                override fun dismiss() {
                }
            }

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { view, hourOfDay, minute ->
                    appointTime.setText(
                        String.format(
                            "%d:%d",
                            hourOfDay,
                            minute
                        )
                    )
                }, 0, 0, false
            )
            timePickerDialog.onClick(dialogInterface, 1)
            timePickerDialog.show()
        }
        // Click on add button
        binding.buttonDirectionSend.setOnClickListener {
            if (
                checkErrors(
                    imageSelected3,
                    appointName,
                    appointAddress,
                    appointDate,
                    appointTime
                )
            ) {
                uploadImage(
                    appointImage,
                    "appointments",
                    appointName,
                    appointTitle,
                    appointWork,
                    appointAddress,
                    appointTime,
                    appointDate
                )

            } else {
                Toast.makeText(requireContext(), "Error!", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun hideKeyboard(editText: TextInputEditText?) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText!!.windowToken, 0)
    }
    private fun checkErrors(
        isSelected: Boolean,
        name: TextInputEditText,
        number: TextInputEditText,
        more: TextInputEditText,
        more2: TextInputEditText
    ): Boolean {
        if (!isSelected) {
            Toast.makeText(requireContext(), "Select Image first", Toast.LENGTH_LONG).show()
            return false
        }
        if (name.text.toString().trim().isEmpty()) {
            name.error = "Required!"
            return false
        }
        if (number.text.toString().trim().isEmpty()) {
            name.error = "Required!"
            return false
        }
        if (name.text.toString().trim().isEmpty()) {
            name.error = "Required!"
            return false
        }
        if (more.text.toString().trim().isEmpty()) {
            more.error = "Required!"
            return false
        }
        if (more2.text.toString().trim().isEmpty()) {
            more2.error = "Required!"
            return false
        }
        return true
    }


    private lateinit var mAuth: FirebaseAuth
    private lateinit var senderID: String
    private lateinit var receiverID: String
    private lateinit var rootRef: DatabaseReference

    private fun sendMessageToUser(directionImage:String,directionGoal:String,directionDoctor:String,directionAddress:String,directionComments:String,directionDate:String,directionTime:String) {

            val messageSenderRef: String =
                ("messages" + "/" + senderID).toString() + "/" + receiverID
            val messageReceiverRef: String =
                ("messages" + "/" + receiverID).toString() + "/" + senderID

            val messagePushId: String =
                rootRef.child("messages").child(senderID).child(receiverID).push().getKey()
                    .toString()

            val recieverMessageTextBody = createMessageBody(directionImage,directionGoal,directionDoctor,directionAddress,directionComments,directionDate,directionTime)
            val senderMessageTextBody = createMessageBody(directionImage,directionGoal,directionDoctor,directionAddress,directionComments,directionDate,directionTime)

            val messageBodyDetails: MutableMap<String, Any> = HashMap()
            messageBodyDetails["$messageSenderRef/$messagePushId"] = senderMessageTextBody
            messageBodyDetails["$messageReceiverRef/$messagePushId"] = recieverMessageTextBody

            sendMessageToDatabase(messageBodyDetails)

    }

    private fun createMessageBody(directionImageUrl:String,directionGoal:String,directionDoctor:String,directionAddress:String,directionComments:String,directionDate:String,directionTime:String): Map<String, Any> {
        val messageTextBody: MutableMap<String, Any> = HashMap()
        messageTextBody["imageUrl"] = directionImageUrl
        messageTextBody["goal"] = directionGoal
        messageTextBody["doctor"] = directionDoctor
        messageTextBody["address"] = directionAddress
        messageTextBody["comments"] = directionComments
        messageTextBody["date"] = directionDate
        messageTextBody["time"] = directionTime
        return messageTextBody
    }

    private fun sendMessageToDatabase(messageBodyDetails: Map<String, Any>) {
        rootRef.updateChildren(messageBodyDetails).addOnCompleteListener { task ->
            if (task.isSuccessful()) {
                Log.d("sendMessageToDatabase", "sendMessageToDatabase:success")
            } else {
                val message: String = task.exception?.message.toString()
                Toast.makeText(requireContext(), "Error: $message", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private var imageUri: Uri? = null
    private lateinit var appointImage: ShapeableImageView
    private lateinit var appointName: TextInputEditText
    private lateinit var appointTitle: TextInputEditText
    private lateinit var appointAddress: TextInputEditText
    private lateinit var appointDate: TextInputEditText
    private lateinit var appointTime: TextInputEditText
    private lateinit var appointWork: TextInputEditText
    private var imageSelected3: Boolean = false


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 104) {
            imageUri = data?.data
            appointImage.setImageURI(imageUri)
            imageSelected3 = true
        }

    }

    private fun uploadImage(
        imageView: ShapeableImageView,
        toFolder: String,
        name: TextInputEditText,
        title: TextInputEditText,
        work: TextInputEditText,
        address: TextInputEditText,
        timeEdit: TextInputEditText,
        dateEdit: TextInputEditText
    ) {
        imageView.setDrawingCacheEnabled(true)
        imageView.buildDrawingCache()
        val bitmap: Bitmap = imageView.getDrawingCache(true)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        imageView.setDrawingCacheEnabled(false)
        val bytes = byteArrayOutputStream.toByteArray()
        val user: FirebaseUser? = mAuth.currentUser
        //saving in mentioned dir
        val path = user?.uid + "/$toFolder" + "/${name.text.toString()}.png"
        val reference: StorageReference = mStorage.getReference(path)
        val metadata = StorageMetadata.Builder()
            .setCustomMetadata("text", "Profile pic of ${user?.displayName}").build()

        reference.putBytes(bytes, metadata).addOnSuccessListener { taskSnapshot ->
            //getting image url for showing user's profile pic with Glide
            reference.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl: String = uri.toString()

                //saving user's data
                //navigating to different screen
                sendMessageToUser(
                    imageUrl,
                    name.text.toString(),
                    title.text.toString(),
                    address.text.toString(),
                    work.text.toString(),
                    timeEdit.text.toString(),
                    dateEdit.text.toString()
                )
            }
        }
    }
}