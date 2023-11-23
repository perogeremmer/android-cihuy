package com.example.todofirebase

import android.app.AlertDialog
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    lateinit var listTodo : ListView
    lateinit var btnCreateTodo : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listTodo = findViewById(R.id.list_todo)
        btnCreateTodo = findViewById(R.id.btn_create_todo)

        this.getData()

        btnCreateTodo.setOnClickListener {
            val intent = Intent(this, CreateTodoActivity::class.java)
            startActivity(intent)
        }

        listTodo.setOnItemClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as TodoModel

            val intent = Intent(this, CreateTodoActivity::class.java)
            intent.putExtra("id", item.Id.toString())
            intent.putExtra("title", item.Title.toString())
            intent.putExtra("description", item.Description.toString())
            startActivity(intent)
        }

        listTodo.setOnItemLongClickListener { adapterView, view, position, id ->
            val item = adapterView.getItemAtPosition(position) as TodoModel

            var title = item.Title.toString()

            val builder = AlertDialog.Builder(this)
            builder.setMessage("Apakah kamu ingin menghapus todo: $title?")
                .setCancelable(false)
                .setPositiveButton("Hapus") { dialog, id ->
                    deleteItem(item.Id.toString(), title)
                    getData()
                }
                .setNegativeButton("Batal") { dialog, id ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()

            return@setOnItemLongClickListener true
        }
    }

    fun deleteItem(id: String, title: String) {
        val db = Firebase.firestore
        db.collection("todo").document(id).delete()
            .addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "Berhasil menghapus todo: $title",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    applicationContext,
                    "Gagal menghapus todo: $title.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    override fun onResume() {
        super.onResume()
        this.getData()
    }

    fun getData() {
        val db = Firebase.firestore
        db.collection("todo")
            .get()
            .addOnSuccessListener { result ->
                val Items = ArrayList<TodoModel>()

                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    Items.add(
                        TodoModel(
                            document.id.toString(),
                            document.data.get("title").toString(),
                            document.data.get("description").toString()
                        )
                    )
                }

                val adapter = TodoAdapter(this, R.layout.todo_item, Items)
                listTodo.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
}