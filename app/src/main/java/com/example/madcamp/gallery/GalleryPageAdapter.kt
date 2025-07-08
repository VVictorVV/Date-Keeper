package com.example.madcamp.gallery

import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp.R
import com.example.madcamp.people.Person

class GalleryPagerAdapter(
    private val person: Person,
    private val onDelete: (Gallery) -> Unit  // 삭제 콜백 받기
) : RecyclerView.Adapter<GalleryPagerAdapter.GalleryViewHolder>() {

    private val items = mutableListOf<Gallery>()

    fun submitList(newList: List<Gallery>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    inner class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.gallery_image_view)
        val descriptionInput: EditText = view.findViewById(R.id.gallery_description_input)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)  // 삭제 버튼
        val profileImage: ImageView = view.findViewById(R.id.card_profile_image)
        val tvNickname: TextView = view.findViewById(R.id.tvNickname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_image, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = items[position]

        // 이미지 로딩
        Glide.with(holder.itemView)
            .load(item.imageUri)
            .into(holder.imageView)

        // 설명 표시
        holder.descriptionInput.setText(item.description)

        // 변경 감지
        holder.descriptionInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                item.description = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 삭제 버튼 클릭 시 처리
        holder.btnDelete.setOnClickListener {
            onDelete(item)
        }

        val context = holder.itemView.context
        val iconName = person.representativeIcon
        val iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName)
        if (iconResId != 0) {
            holder.profileImage.setImageResource(iconResId)
        } else {
            holder.profileImage.setImageResource(R.drawable.icon_heart)
        }
        holder.tvNickname.text = person.nickname
    }

    override fun getItemCount(): Int = items.size
}


