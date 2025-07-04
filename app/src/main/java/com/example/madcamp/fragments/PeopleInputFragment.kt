package com.example.madcamp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madcamp.PeopleManager
import com.example.madcamp.R
import com.example.madcamp.data.Person
import com.example.madcamp.databinding.PeopleInputBinding

class PeopleInputFragment : Fragment(){

    private var _binding: PeopleInputBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PeopleInputBinding.inflate(inflater, container, false)

        binding.btnSave.setOnClickListener {
            val name = binding.editName.text.toString().trim()
            val phone = binding.editPhone.text.toString().trim()
            val nickname = binding.editNickname.text.toString().trim()

            if (name.isNotEmpty() && phone.isNotEmpty() && nickname.isNotEmpty()) {
                val newPerson = Person(
                    id = PeopleManager.generateId(),
                    name = name,
                    nickname = nickname,
                    representativeIcon = "", // 아직 없으니 빈 문자열
                    phoneNumber = phone,
                    anniversary = listOf(),
                    giftInfo = listOf(),
                    memories = listOf()
                )
                PeopleManager.addPerson(newPerson)

                val allPeople = PeopleManager.getPeople()
                Log.d("PeopleDebug", "현재 PeopleManager 목록: $allPeople")

                Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()

                // 필요하면 입력칸 초기화
                binding.editName.text.clear()
                binding.editPhone.text.clear()
                binding.editNickname.text.clear()
            } else {
                Toast.makeText(requireContext(), "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}