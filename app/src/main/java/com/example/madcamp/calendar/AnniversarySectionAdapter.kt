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
    private val onSectionClick: (String) -> Unit
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

        // ✅ 어댑터 연결 (이거 없으면 사진이 안 보임)
        holder.photoRecyclerView.layoutManager = GridLayoutManager(holder.itemView.context, 3)
        holder.photoRecyclerView.adapter = GalleryPhotoAdapter(photos) {
            onSectionClick(title)
        }

        // ✅ 클릭 리스너 전달
        holder.sectionTitle.setOnClickListener {
            onSectionClick(title)
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
