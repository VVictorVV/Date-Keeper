package com.example.madcamp.gallery

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp.R


class GalleryPhotoAdapter(
    private val photos: List<Gallery>,
    private val onPhotoClick: () -> Unit
) : RecyclerView.Adapter<GalleryPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.photoImageView)

        init {
            itemView.setOnClickListener {
                onPhotoClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val gallery = photos[position]
        Glide.with(holder.itemView.context)
            .load(gallery.imageUri)
            .into(holder.imageView)
        Log.d("GalleryPhotoAdapter", "Image URI: ${gallery.imageUri}")

    }

    override fun getItemCount(): Int = photos.size
}

