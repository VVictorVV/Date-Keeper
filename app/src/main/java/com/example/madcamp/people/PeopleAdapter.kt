package com.example.madcamp.people

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.R

class PeopleAdapter(
    private val peopleList: List<Person>,
    private val onItemClick: (Person) -> Unit
) :
    RecyclerView.Adapter<PeopleAdapter.PersonViewHolder>() {

    // 각 카드 뷰의 구성요소를 보관하는 클래스
    class PersonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImage: ImageView = view.findViewById(R.id.card_profile_image)
        val profileName: TextView = view.findViewById(R.id.card_profile_name)
        val profilePhoneNumber: TextView = view.findViewById(R.id.card_profile_phone_number)
        val iconGift: ImageView = view.findViewById(R.id.icon_gift)
        val iconAnniversary: ImageView = view.findViewById(R.id.icon_anniversary)
        val iconMemory: ImageView = view.findViewById(R.id.icon_memory)
    }

    // ViewHolder를 생성하는 함수 (person_card_item.xml 레이아웃을 inflate)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.people_person_card_item, parent, false)
        return PersonViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    @SuppressLint("DiscouragedApi")
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = peopleList[position]
        val showName = "${person.name} (${person.nickname})"

        holder.profileName.text = showName
        holder.profilePhoneNumber.text = person.phoneNumber

        // 선물 여부 아이콘 설정
        holder.iconGift.setImageResource(
            if (person.giftInfo.isNotEmpty()) R.drawable.people_gift_color
            else R.drawable.people_gift
        )

        // 기념일 여부 아이콘 설정
        holder.iconAnniversary.setImageResource(
            if (person.anniversary.isNotEmpty()) R.drawable.people_celebration_color
            else R.drawable.people_celebration
        )

        if (person.representativeIcon.isNotEmpty()) {
            val context = holder.itemView.context
            val resourceId = context.resources.getIdentifier(person.representativeIcon, "drawable", context.packageName)

            if (resourceId != 0) {
                holder.profileImage.setImageResource(resourceId)
            } else {
                holder.profileImage.setImageResource(R.drawable.icon_heart)
            }
        } else {
            holder.profileImage.setImageResource(R.drawable.icon_heart)
        }

        // 추억 여부 아이콘 설정
        holder.iconMemory.setImageResource(
            if (person.memories.isNullOrEmpty()) R.drawable.people_gallery_color
            else R.drawable.people_gallery
        )

        // 프로필 클릭 리스너 생성
        holder.itemView.setOnClickListener {
            onItemClick(person)
        }
    }

    // 전체 아이템 개수 반환
    override fun getItemCount(): Int {
        return peopleList.size
    }
}