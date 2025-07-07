package com.example.madcamp.people

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.R

class GiftAdapter(private val giftList: List<String>) : RecyclerView.Adapter<GiftAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val giftNameText: TextView = view.findViewById(R.id.gift_name_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.people_gift_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gift = giftList[position]
        holder.giftNameText.text = gift
    }

    override fun getItemCount(): Int = giftList.size
}