package com.application.quizap

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import com.google.firebase.auth.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.view.*
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    //Creating member variables of FirebaseDatabase and DatabaseReference
    private lateinit var mFirebaseDatabase: DatabaseReference
    //Creating member variable for userId and emailAddress
    private lateinit var imageUrl: String
    private var uri: Uri?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        topBar.setNavigationOnClickListener { finish() }
        auth = FirebaseAuth.getInstance()
        mFirebaseDatabase=FirebaseDatabase.getInstance().getReference("users")
        var textPassword = findViewById<TextView>(R.id.textPasswordR)
        var textConfirmPassword = findViewById<TextView>(R.id.textConfirmPasswordR)
        var textEmail = findViewById<TextView>(R.id.textEmailR)
        profilePicture.setOnClickListener {
            var view=layoutInflater.inflate(R.layout.image_picker,null)
            MaterialAlertDialogBuilder(this).setTitle("Pick Profile Photo")
                .setView(view).show()
            var camera=view.findViewById<ImageButton>(R.id.menuCamera)
            var gallery=view.findViewById<ImageButton>(R.id.menuGallery)
            gallery.setOnClickListener {
                var intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 0)
            }
            camera.setOnClickListener {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    //permission was not enabled
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    //show popup to request permission
                    requestPermissions(permission, 11)
                }
                else{
                    //permission already granted
                    openCamera()
                }
            }
        }
        textPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                if (p0?.length!! < 6) {
                    fieldPasswordR.error = "* Password should be min 6 characters"
                } else {
                    fieldPasswordR.isErrorEnabled = false
                }
            }
        })
        textEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                when (isValidEmail(p0.toString())) {
                    false -> fieldEmailR.error = "* Invalid email address"
                    true -> fieldEmailR.isErrorEnabled = false
                }
            }
        })
        registerButton.setOnClickListener {
            if (textConfirmPassword.text.isEmpty() || textEmail.text.isEmpty() || textPassword.text.isEmpty()) {
                MaterialAlertDialogBuilder(this).setTitle("Fields with ' * ' cannot be empty")
                    .setPositiveButton("OK") { _, _ -> }.show()
            } else {
                if (textPassword.text.toString() == textConfirmPassword.text.toString()) {
                auth.createUserWithEmailAndPassword(textEmail.text.toString(), textPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            //upload image
                            uploadImageToFirebase()
                            //verification mail
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { task2 ->
                                    if (task2.isSuccessful) {
                                        MaterialAlertDialogBuilder(this).setTitle("A confirmation link has been sent to your email")
                                            .setMessage("Please click on confirm from mail received in your account.")
                                            .setPositiveButton("OK") { _, _ -> }
                                            .show()
                                    }
                                }
                            Snackbar.make(it, "User Added !!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Login") {
                                    finish()
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }.show()
                        }
                    }
            }else{
                    MaterialAlertDialogBuilder(this).setTitle("Password and Confirm Password should be same")
                        .setPositiveButton("OK") { _, _ -> }.show()
                }
            }
        }
        resetButton.setOnClickListener {
            recreate()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode) {
            11 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup was granted
                    openCamera()
                } else {
                    //permission from popup was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var intent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent,1)
    }

    private fun uploadImageToFirebase() {
        if (uri==null) return
        val ref=FirebaseStorage.getInstance().reference.child("images/"+UUID.randomUUID().toString())
            ref.putFile(uri!!)
                .addOnSuccessListener {
                    //firebase download location
                    ref.downloadUrl.addOnSuccessListener {it1->
                        imageUrl=it1.toString()
                        var myUser =
                            User(textNameR.text.toString(), textEmailR.text.toString(), imageUrl)
                        mFirebaseDatabase.child(auth.currentUser?.uid!!).setValue(myUser)
                        Log.d("Register uri","$uri")
                        Log.d("Register it","$it")
                    }
                }
                .addOnFailureListener {
                    imageUrl=""
                    var myUser =
                        User(textNameR.text.toString(), textEmailR.text.toString(), imageUrl)
                    mFirebaseDatabase.child(auth.currentUser?.uid!!).setValue(myUser)
                    Log.d("Register","image upload fail")
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==0 && resultCode==Activity.RESULT_OK && data != null){
            //when photo is selected->
            uri=data.data
            profile_Picture.setImageURI(uri)
            profilePicture.isVisible=false
        }
        if (requestCode==1 && resultCode==Activity.RESULT_OK){
            profile_Picture.setImageURI(uri)
            profilePicture.isVisible=false
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

}