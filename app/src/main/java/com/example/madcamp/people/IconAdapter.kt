package com.example.madcamp.people

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat

class IconAdapter (
    private val context: Context,
    private val iconList: List<Pair<String, Int>>
) : BaseAdapter() {
    override fun getCount(): Int = iconList.size
    override fun getItem(position: Int): Any = iconList[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(150, 150)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        val (_, iconId) = iconList[position]
        imageView.setImageDrawable(ContextCompat.getDrawable(context, iconId))
        return imageView
    }
}