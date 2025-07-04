package com.example.madcamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class IconAdapter(
    private val iconList: List<Int>,
    var onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {
    class IconViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconImageView: ImageView = view.findViewById(R.id.icon_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.icon_item, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val iconResId = iconList[position]
        holder.iconImageView.setImageResource(iconResId)
        holder.itemView.setOnClickListener {
            onIconSelected(iconResId)
        }
    }

    override fun getItemCount(): Int {
        return iconList.size
    }
}