package com.example.madcamp.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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
        val profileImage: ImageView = view.findViewById(R.id.card_profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_person_card_item, parent, false)
        return MemoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemoryViewHolder, position: Int) {
        val person = peopleList[position]
        holder.textName.text = "${person.nickname}"
        holder.photoCountText.text = "${person.memories.size}"

        holder.itemView.setOnClickListener {
            onItemClick(person)
        }

        if (person.representativeIcon.isNotEmpty()) {
            val context = holder.itemView.context
            val resourceId = context.resources.getIdentifier(person.representativeIcon, "drawable", context.packageName)

            if (resourceId != 0) {
                holder.profileImage.setImageResource(resourceId)
            } else {
                holder.profileImage.setImageResource(R.drawable.icon_heart)
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.icon_heart)
        }
    }

    override fun getItemCount(): Int = peopleList.size
}