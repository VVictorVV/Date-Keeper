package com.example.madcamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.calendar.Anniversary
import com.example.madcamp.calendar.AnniversaryDetails
import com.example.madcamp.people.Person
import org.w3c.dom.Text
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AnniversaryAdapter(private val anniversaryList: List<AnniversaryDetails>) :
    RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.anniversary_icon)
            val nameText: TextView = view.findViewById(R.id.anniversary_name_text)
            val ddayText: TextView = view.findViewById(R.id.anniversary_dday_text)
            val giftText: TextView = view.findViewById(R.id.anniversary_gift_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_anniversary_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val details = anniversaryList.get(position)
            val person = details.person
            val anniversary = details.anniversary

            holder.nameText.text = "${person.name}님의 ${anniversary.name}"
            holder.giftText.text = "선물: ${anniversary.gift}"
            // D-day 계산
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val anniversaryDate = LocalDate.parse(anniversary.date, formatter)
                val currentDate = LocalDate.now()
                val daysUntil = ChronoUnit.DAYS.between(currentDate, anniversaryDate)

                holder.ddayText.text = when {
                    daysUntil > 0 -> "D-$daysUntil"
                    daysUntil < 0 -> ""
                    else -> "D-Day"
                }
            } catch (e: Exception) {
                holder.ddayText.text = ""
                e.printStackTrace()
            }

            // 아이콘 설정 로직 추가 가능
            // holder.icon.setImageResource(...)
        }

        override fun getItemCount(): Int = anniversaryList.size
    }