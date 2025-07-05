package com.example.madcamp.people

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
        val profileGiftInfo: TextView = view.findViewById(R.id.card_gift_info)
    }

    // ViewHolder를 생성하는 함수 (person_card_item.xml 레이아웃을 inflate)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.people_person_card_item, parent, false)
        return PersonViewHolder(view)
    }

    // ViewHolder에 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val person = peopleList[position]
        val showName = "${person.name} (${person.nickname})"

        holder.profileName.text = showName
        holder.profilePhoneNumber.text = person.phoneNumber
        holder.profileGiftInfo.text =
            if (person.giftInfo.isNotEmpty()) "선물 있음 🎁" else "선물 없음"
        // person 객체에 아이콘 정보가 있다면 설정 (예: person.iconResId)
        // holder.profileImage.setImageResource(person.iconResId)

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