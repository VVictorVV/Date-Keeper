package com.example.madcamp.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.gallery.Gallery
import com.example.madcamp.gallery.GalleryPhotoAdapter
import com.example.madcamp.R
import com.example.madcamp.gallery.GalleryDetailFragment
import com.example.madcamp.people.Person

class AnniversarySectionAdapter(
    private val groupedData: Map<String, List<Gallery>>,
    private val person: Person,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<AnniversarySectionAdapter.SectionViewHolder>() {

    private var sectionTitles = groupedData.keys.toList()

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sectionTitle: TextView = itemView.findViewById(R.id.tvSectionTitle)
        val photoRecyclerView: RecyclerView = itemView.findViewById(R.id.rvPhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anniversary_section, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val title = sectionTitles[position]
        val photos = groupedData[title] ?: emptyList()

        holder.sectionTitle.text = title
        holder.photoRecyclerView.layoutManager =
            GridLayoutManager(holder.itemView.context, 3) // 3개씩 한 줄에
        holder.sectionTitle.setOnClickListener {
            val sectionTitle = sectionTitles[position]
            val fragment = GalleryDetailFragment.newInstance(person, sectionTitle)

            fragmentManager.beginTransaction()
                .replace(R.id.bottom_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

    }

    override fun getItemCount(): Int = sectionTitles.size

    fun updateData(newGroupedData: Map<String, List<Gallery>>) {
        (groupedData as MutableMap).clear()
        (groupedData as MutableMap).putAll(newGroupedData)
        sectionTitles = newGroupedData.keys.toList()
        notifyDataSetChanged()
    }

}
