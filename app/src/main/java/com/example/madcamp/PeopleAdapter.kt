package com.example.madcamp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.data.Person
import com.example.madcamp.databinding.PersonListBinding

class PeopleAdapter(private val people: List<Person>) :
    RecyclerView.Adapter<PeopleAdapter.PersonViewHolder>() {

    inner class PersonViewHolder(private val binding: PersonListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(person: Person) {
            binding.plName.text = person.name
            binding.plPhone.text = person.phoneNumber
            binding.plNickname.text = person.nickname
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        val binding = PersonListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PersonViewHolder(binding)
    }

    override fun getItemCount(): Int = people.size

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        holder.bind(people[position])
    }
}