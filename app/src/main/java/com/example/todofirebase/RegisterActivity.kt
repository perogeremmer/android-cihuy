package com.example.todofirebase

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RegisterActivity : AppCompatActivity() {

    lateinit var btnRegister : Button
    lateinit var etEmail : EditText
    lateinit var etName : EditText
    lateinit var etPassword : EditText
    lateinit var etPasswordConfirmation : EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister = findViewById(R.id.btn_register)
        etEmail = findViewById(R.id.et_register_email)
        etName = findViewById(R.id.et_register_name)
        etPassword = findViewById(R.id.et_register_password)
        etPasswordConfirmation = findViewById(R.id.et_register_password_confirmation)

        btnRegister.setOnClickListener {
            var userModel = UserModel(
                Email = etEmail.text.toString(),
                Name = etName.text.toString(),
                Password = PasswordHelper.md5(etPassword.text.toString())
            )

            // cara 1
            checkUser(etEmail.text.toString()) {
                if (it) {
                    Toast.makeText(
                        applicationContext,
                        "Akun dengan email ${etEmail.text.toString()} sudah terdaftar!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    this.registerUser(userModel)
                }
            }

            // cara 2
            CoroutineScope(Dispatchers.Main).launch {
                val isRegistered = checkUser(etEmail.text.toString())
                if (isRegistered) {
                    Toast.makeText(
                        applicationContext,
                        "Akun dengan email ${etEmail.text.toString()} sudah terdaftar!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    this@RegisterActivity.registerUser(userModel)
                }
            }
        }


    }

    fun checkUser(email: String, checkResult: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // doc exist
                    checkResult.invoke(true)
                } else {
                    // doc doesn't exist
                    checkResult.invoke(false)
                }
            }
            .addOnFailureListener { exception ->
                // fail
                checkResult.invoke(false)
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    suspend fun checkUser(email: String): Boolean {
        return suspendCoroutine { continuation ->
            val db = Firebase.firestore
            db.collection("users").whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // doc exist
                        continuation.resume(true)
                    } else {
                        // doc doesn't exist
                        continuation.resume(false)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resume(false)
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    fun registerUser(userModel: UserModel) {
        val db = Firebase.firestore
        db.collection("users")
            .add(userModel)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    applicationContext,
                    "Berhasil melakukan registrasi!",
                    Toast.LENGTH_SHORT
                ).show()

//                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}