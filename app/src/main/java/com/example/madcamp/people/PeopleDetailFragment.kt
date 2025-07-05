package com.example.madcamp.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        person?.let { person ->
            binding.textName.text = "이름: ${person.name}"
            binding.textNickname.text = "별명: ${person.nickname}"
            binding.textPhone.text = "전화번호: ${person.phoneNumber}"
            binding.textGifts.text = "선호하는 선물: ${person.giftInfo.joinToString(", ")}"

            // 수정 버튼
            binding.btnEdit.setOnClickListener {
                val editFragment = PeopleInputFragment.newInstance(person)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_layout, editFragment)
                    .addToBackStack(null)
                    .commit()
            }

            // 🔥 삭제 버튼 처리
            binding.btnDelete.setOnClickListener {
                PeopleManager.removePerson(person)
                Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()  // 목록으로 돌아가기
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}