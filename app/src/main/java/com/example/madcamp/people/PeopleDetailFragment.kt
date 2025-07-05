package com.example.madcamp.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.madcamp.R
import com.example.madcamp.databinding.PeopleDetailBinding

class PeopleDetailFragment : Fragment(){
    private var _binding: PeopleDetailBinding? = null
    private val binding get() = _binding!!
    private var person: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        person = arguments?.getParcelable("person")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PeopleDetailBinding.inflate(inflater, container, false)

        person?.let {
            binding.textName.text = "이름: ${it.name}"
            binding.textNickname.text = "별명: ${it.nickname}"
            binding.textPhone.text = "전화번호: ${it.phoneNumber}"
            binding.textGifts.text = "선물 목록: ${it.giftInfo.joinToString(", ")}"
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEdit.setOnClickListener {
            person?.let {
                val fragment = PeopleInputFragment.newInstance(it)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_layout, fragment)  // 현재 프래그먼트를 입력 폼으로 바꿈
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}