package com.example.androidlab2

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.IndexOutOfBoundsException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "shopping-list"
        ).allowMainThreadQueries().build()
        val shoppingList: ArrayList<ShoppingItem> = db.itemDao().getAll() as ArrayList<ShoppingItem>
        if (shoppingList.isEmpty()) {
            setEmptyListMessage(true)
        } else {
            setEmptyListMessage(false)
        }

        val recyclerView: androidx.recyclerview.widget.RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        val adapter = ShoppingListAdapter(this, shoppingList, db) { setEmptyListMessage(it) }
        recyclerView.adapter = adapter
        val buttonAdd: FloatingActionButton = findViewById(R.id.button_add_item)
        buttonAdd.setOnClickListener {
            val cardDialogBinding = layoutInflater.inflate(R.layout.opened_card, null)
            val cardDialog = Dialog(this)
            cardDialog.setContentView(cardDialogBinding)
            cardDialog.setCancelable(true)
            cardDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            cardDialog.show()

            val titleTextView: TextView = cardDialog.findViewById(R.id.title)
            val itemNameLayout: TextInputLayout = cardDialog.findViewById(R.id.item_name_layout)
            val itemNoteLayout: TextInputLayout = cardDialog.findViewById(R.id.note_layout)

            itemNameLayout.setHint(R.string.enter_item_name)
            itemNoteLayout.setHint(R.string.enter_note)
            titleTextView.setText(R.string.new_shopping_item)

            val buttonSubmit: Button = cardDialog.findViewById(R.id.add_button)
            buttonSubmit.setOnClickListener {
                val itemNameEditText: TextInputEditText = cardDialog.findViewById(R.id.item_name_edit_text)
                val itemNoteEditText: TextInputEditText = cardDialog.findViewById(R.id.item_note_edit_text)

                if (itemNameEditText.text.toString().isEmpty()) {
                    itemNameLayout.error = "Please enter item name"
                } else {
                    try {
                        adapter.addNewItem(
                            ShoppingItem(
                                0,
                                (itemNameEditText.text.toString()),
                                (itemNoteEditText.text.toString())
                            )
                        )
                        setEmptyListMessage(false)
                        cardDialog.dismiss()
                    } catch (e: IndexOutOfBoundsException) {
                        Toast.makeText(this, "Shopping list reached maximum capacity", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    private fun setEmptyListMessage(show: Boolean) {
        val emptyList: TextView = findViewById(R.id.empty_list_text)
        if (show) {
            emptyList.visibility = TextView.VISIBLE
        } else {
            emptyList.visibility = TextView.GONE
        }
    }
}