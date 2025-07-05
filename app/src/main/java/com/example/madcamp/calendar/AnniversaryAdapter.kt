package com.example.madcamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import android.annotation.SuppressLint

class AnniversaryAdapter(
    private var anniversaryList: List<AnniversaryDetails>,
    private val onCheckedStateChanged: (Int) -> Unit
) : RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {

        private var isManagementMode: Boolean = false
        private val checkedItems = mutableSetOf<AnniversaryDetails>()

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val icon: ImageView = view.findViewById(R.id.anniversary_icon)
            val nameText: TextView = view.findViewById(R.id.anniversary_name_text)
            val checkBox: CheckBox = view.findViewById(R.id.checkbox_anniversary_select)
            val ddayText: TextView = view.findViewById(R.id.anniversary_dday_text)
            val giftText: TextView = view.findViewById(R.id.anniversary_gift_text)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.calendar_anniversary_item, parent, false)
            return ViewHolder(view)
        }

        @SuppressLint("DiscouragedApi")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val details = anniversaryList.get(position)
            val person = details.person
            val anniversary = details.anniversary

            holder.nameText.text = "${person.name}님의 ${anniversary.name}"
            if (anniversary.gift.isEmpty()) {
                holder.giftText.text = "주고 싶은 선물을 아직 고르지 않았습니다!"
            } else {
                holder.giftText.text = "선물: ${anniversary.gift}"
            }
            holder.checkBox.visibility = if (isManagementMode) View.VISIBLE else View.INVISIBLE
            holder.checkBox.isChecked = checkedItems.contains(details)

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.add(details)
                } else {
                    checkedItems.remove(details)
                }
                onCheckedStateChanged(checkedItems.size)
            }
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
            if (person.representativeIcon.isNotEmpty()) {
                val context = holder.itemView.context
                val resourceId = context.resources.getIdentifier(person.representativeIcon, "drawable", context.packageName)

                if (resourceId != 0) {
                    holder.icon.setImageResource(resourceId)
                } else {
                    holder.icon.setImageResource(R.drawable.icon_heart)
                }
            } else {
                holder.icon.setImageResource(R.drawable.icon_heart)
            }
        }

        override fun getItemCount(): Int = anniversaryList.size

        // Fragment에서 기념일 관리 모드를 설정하는 함수
        fun setManagementMode(isEnabled: Boolean) {
            isManagementMode = isEnabled
            if (!isEnabled) {
                checkedItems.clear()
                onCheckedStateChanged(0)
            }
            notifyDataSetChanged()
        }

        // Fragment에서 체크된 아이템 목록을 가져가는 함수
        fun getCheckedItems(): Set<AnniversaryDetails> {
            return checkedItems
        }

        // 데이터 변경 후 목록을 갱신하는 함수
        fun updateData(newList: List<AnniversaryDetails>) {
            anniversaryList = newList
            notifyDataSetChanged()
        }
    }