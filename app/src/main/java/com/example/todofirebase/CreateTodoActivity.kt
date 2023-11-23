package com.example.todofirebase

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CreateTodoActivity : AppCompatActivity() {
    lateinit var etTitle : EditText
    lateinit var etDescription : EditText
    lateinit var btnSubmit : Button

    lateinit var labelHeader : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        btnSubmit = findViewById(R.id.btn_submit)
        labelHeader = findViewById(R.id.label_header)

        var editMode = false
        var id = intent.getStringExtra("id")
        if (!id.isNullOrBlank()) {
            editMode = true
            var title = intent.getStringExtra("title").toString()
            labelHeader.setText("Ubah Todo: $title")
            etTitle.setText(title)
            etDescription.setText(intent.getStringExtra("description").toString())

            btnSubmit.setText("Ubah")
        }


        btnSubmit.setOnClickListener {
            if (etTitle.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Harap isi judul todo terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (etDescription.text.isEmpty()) {
                Toast.makeText(
                    applicationContext,
                    "Harap isi deskripsi todo terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (editMode) {
                var todoModel = TodoModel(
                    Id = intent.getStringExtra("id").toString(),
                    Title = etTitle.text.toString(),
                    Description = etDescription.text.toString()
                )
                this.update(todoModel)
                return@setOnClickListener
            }

            var todoModel = TodoModel(
                Title = etTitle.text.toString(),
                Description = etDescription.text.toString()
            )
            this.create(todoModel)
        }
    }

    fun update(todoModel: TodoModel) {
        val db = Firebase.firestore
        db.collection("todo").document(todoModel.Id.toString()).set(todoModel)
            .addOnSuccessListener { documentReference ->
            Toast.makeText(
                applicationContext,
                "Berhasil merubah Todo!",
                Toast.LENGTH_SHORT
            ).show()

            finish()
        }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun create(todoModel: TodoModel) {
        val db = Firebase.firestore
        db.collection("todo")
            .add(todoModel)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(
                    applicationContext,
                    "Berhasil menambahkan Todo!",
                    Toast.LENGTH_SHORT
                ).show()

                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}