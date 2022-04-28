package com.example.whereami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val contactsList : ArrayList<Contact>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,
        parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = contactsList[position]
        holder.name_TextView.text = currentItem.name
        holder.number_TextView.text = currentItem.number
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val item_ImageView : ImageView = itemView.findViewById(R.id.item_image)
        val name_TextView : TextView = itemView.findViewById(R.id.item_name)
        val number_TextView : TextView = itemView.findViewById(R.id.item_number)

    }
}