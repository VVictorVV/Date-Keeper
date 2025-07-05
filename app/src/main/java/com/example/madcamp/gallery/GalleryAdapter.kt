package com.example.madcamp.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.R
import com.example.madcamp.people.Person

class GalleryAdapter(
    private val peopleList: List<Person>,
    private val onItemClick: (Person) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.MemoryViewHolder>() {

    class MemoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById(R.id.memory_card_name)
        val photoCountText: TextView = view.findViewById(R.id.memory_card_photo_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_person_card_item, parent, false)
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val person = peopleList[position]
        holder.textName.text = "${person.name} (${person.nickname})"
        holder.photoCountText.text = "사진 ${person.memories.size}장"

        holder.itemView.setOnClickListener {
            onItemClick(person)
        }
    }

    override fun getItemCount(): Int = peopleList.size
}