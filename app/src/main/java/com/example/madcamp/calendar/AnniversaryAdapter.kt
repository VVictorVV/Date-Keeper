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
import android.graphics.Color
import android.text.BoringLayout
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import java.util.Date

class AnniversaryAdapter(
    private var anniversaryList: List<AnniversaryDetails>,
    private val isCalendarMode: Boolean,
    private val onCheckedStateChanged: (Int) -> Unit
) : RecyclerView.Adapter<AnniversaryAdapter.ViewHolder>() {
        private val checkedItems = mutableSetOf<AnniversaryDetails>()
        private var showDday: Boolean = true
        private var isDetailViewMode: Boolean = false

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

            // 주고 싶은 선물 출력
            if (anniversary.gift.isEmpty()) {
                holder.giftText.text = "선택한 선물이 없습니다!"
            } else {
                holder.giftText.text = "선물: ${anniversary.gift}"
            }
            holder.checkBox.visibility = if (isDetailViewMode) View.INVISIBLE else View.VISIBLE
            holder.checkBox.visibility = if (isCalendarMode) View.VISIBLE else View.INVISIBLE
            holder.checkBox.isChecked = checkedItems.contains(details)

            holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    checkedItems.add(details)
                } else {
                    checkedItems.remove(details)
                }
                onCheckedStateChanged(checkedItems.size)
            }

            // 프로필 상세보기에서의 출력
            if (isDetailViewMode) {
                holder.icon.visibility = View.INVISIBLE
                holder.icon.layoutParams.width = 0
                holder.icon.requestLayout()
                holder.nameText.text = "${anniversary.name}"
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val displayFormatter = DateTimeFormatter.ofPattern("M월 d일")
                    val anniversaryDate = LocalDate.parse(anniversary.date, formatter)
                    holder.ddayText.text = anniversaryDate.format(displayFormatter)
                } catch (e: Exception) {
                    holder.ddayText.text = ""
                    e.printStackTrace()
                }
            } else { // 캘린더 및 홈 탭에서의 출력
                val personName = person.name
                val anniversaryName = anniversary.name
                val fullText = "${personName}님의 ${anniversaryName}"
                val spannable = SpannableStringBuilder(fullText)
                val nameColor = Color.parseColor("#7C4DFF")
                val nameStartIndex = 0
                val nameEndIndex = personName.length
                spannable.setSpan(
                    ForegroundColorSpan(nameColor),
                    nameStartIndex,
                    nameEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                val anniversaryColor = Color.parseColor("#6A1B9A")
                val anniversaryStartIndex = fullText.indexOf(anniversaryName)
                val anniversaryEndIndex = anniversaryStartIndex + anniversaryName.length
                spannable.setSpan(
                    ForegroundColorSpan(anniversaryColor),
                    anniversaryStartIndex,
                    anniversaryEndIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                holder.nameText.text = spannable

                // D-day 계산
                try {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val anniversaryDate = LocalDate.parse(anniversary.date, formatter)

                    // 홈 탭에서 D-day vs 날짜 보여주기 선택
                    if (showDday) {
                        val currentDate = LocalDate.now()
                        val daysUntil = ChronoUnit.DAYS.between(currentDate, anniversaryDate)

                        holder.ddayText.text = when {
                            daysUntil > 0 -> "D-$daysUntil"
                            daysUntil < 0 -> ""
                            else -> "D-Day"
                        }
                    } else {
                        val displayFormatter = DateTimeFormatter.ofPattern("M월 d일")
                        holder.ddayText.text = anniversaryDate.format(displayFormatter)
                    }

                } catch (e: Exception) {
                    holder.ddayText.text = ""
                    e.printStackTrace()
                }

                // 아이콘 설정 로직 추가 가능
                if (person.representativeIcon.isNotEmpty()) {
                    holder.icon.visibility = View.VISIBLE
                    holder.icon.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
                    holder.icon.requestLayout()
                    val context = holder.itemView.context
                    val resourceId = context.resources.getIdentifier(
                        person.representativeIcon,
                        "drawable",
                        context.packageName
                    )

                    if (resourceId != 0) {
                        holder.icon.setImageResource(resourceId)
                    } else {
                        holder.icon.setImageResource(R.drawable.icon_heart)
                    }
                } else {
                    holder.icon.setImageResource(R.drawable.icon_heart)
                }
            }
        }

        override fun getItemCount(): Int = anniversaryList.size

    fun setDisplayMode(showDdayMode: Boolean) {
        showDday = showDdayMode
        notifyDataSetChanged()
    }

    fun setDetailViewMode(isDetailMode: Boolean) {
        this.isDetailViewMode = isDetailMode
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