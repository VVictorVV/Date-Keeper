package com.example.madcamp // 본인의 패키지명으로 변경하세요

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.people.Person // Person 모델 클래스 import
import com.example.madcamp.calendar.AnniversaryDetails

class AnniversaryAdapter(private val anniversaryList: List<AnniversaryDetails>) :
    RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.anniversary_icon)
            val text: TextView = view.findViewById(R.id.anniversary_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_anniversary_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val details = anniversaryList[position]
            holder.text.text = "${details.person.name}님의 ${details.anniversary.name}"
            // 아이콘 설정 로직 추가 가능
            // holder.icon.setImageResource(...)
        }

        override fun getItemCount(): Int = anniversaryList.size
    }