package com.example.androidlab2

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.lang.IndexOutOfBoundsException


class ShoppingListAdapter(
    private val context: Context,
    private val shoppingList: MutableList<ShoppingItem>,
    private val db: AppDatabase,
    val showEmptyListMessage: (Boolean) -> Unit
)
    : RecyclerView.Adapter<ShoppingListAdapter.ShoppingItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.shopping_item, parent, false)
        return ShoppingItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ShoppingItemViewHolder, position: Int) {
        val item = shoppingList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return shoppingList.size
    }

    inner class ShoppingItemViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
        private val itemName: TextView = itemView.findViewById<View>(R.id.item_name) as TextView
        private val checkbox: CheckBox = itemView.findViewById<View>(R.id.checkbox) as CheckBox

        fun bind(item: ShoppingItem) {
            itemName.text = (item.name)
            checkbox.isChecked = item.checked
            if (item.checked) {
                itemName.paintFlags = itemName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                itemName.paintFlags = itemName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            checkbox.setOnClickListener {
                item.checked = checkbox.isChecked
                db.itemDao().update(item)
                notifyItemChanged(shoppingList.indexOf(item))
            }
            itemView.setOnClickListener {
                val cardDialogBinding =
                    LayoutInflater.from(context).inflate(R.layout.opened_card, null)
                val cardDialog = Dialog(context)
                cardDialog.setContentView(cardDialogBinding)
                cardDialog.setCancelable(true)
                cardDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                cardDialog.show()
                val itemNameEditText: TextInputEditText = cardDialog.findViewById(R.id.item_name_edit_text)
                itemNameEditText.setText(item.name)
                val itemNoteEditText: TextInputEditText = cardDialog.findViewById(R.id.item_note_edit_text)
                itemNoteEditText.setText(item.note)

                val buttonSubmit: Button = cardDialog.findViewById(R.id.add_button)
                buttonSubmit.setOnClickListener {
                    val itemNameLayout: TextInputLayout = cardDialog.findViewById(R.id.item_name_layout)
                    if (itemNameEditText.text.toString().isEmpty()) {
                        itemNameLayout.error = "Please enter item name"
                    } else {
                        item.name = itemNameEditText.text.toString()
                        item.note = itemNoteEditText.text.toString()
                        db.itemDao().update(item)
                        notifyItemChanged(shoppingList.indexOf(item))
                        cardDialog.dismiss()
                    }
                }
            }
            val buttonRemove: Button = itemView.findViewById(R.id.button_remove)
            buttonRemove.setOnClickListener {
                val index = shoppingList.indexOf(item)
                db.itemDao().delete(item)
                shoppingList.removeAt(index)
                notifyItemRemoved(index)
                if (shoppingList.isEmpty()) {
                    showEmptyListMessage(true)
                }
            }
        }
    }

    fun addNewItem(item: ShoppingItem) {
        shoppingList.add(item)
        item.uid = newId()
        db.itemDao().insertAll(item)
        notifyItemInserted(shoppingList.size - 1)
    }

    private fun newId(): Int {
        var newId = 0
        for (item in shoppingList) {
            if (item.uid > newId) {
                newId = item.uid
            }
        }
        if (newId + 1 == Int.MAX_VALUE) {
            for (i in (0..Int.MAX_VALUE)) {
                if (!shoppingList.any { it.uid == i }) {
                    return i
                }
            }
            throw IndexOutOfBoundsException("No more ids available")
        }
        return newId + 1
    }
}