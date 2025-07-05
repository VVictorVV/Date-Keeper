package com.example.madcamp.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp.R
import com.example.madcamp.databinding.FragmentPeopleBinding

class PeopleFragment : Fragment() {

    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)

        // + 버튼 클릭 시 PeopleInputFragment로 이동
        binding.btnAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_layout, PeopleInputFragment())
                .addToBackStack(null)
                .commit()
        }

        // 사람 목록 RecyclerView 설정
        val peopleList = PeopleManager.getPeople() // 여기에 현재 등록된 사람 목록이 있다고 가정
        val adapter = PeopleAdapter(peopleList) { person ->
            // 프로필 카드를 클릭했을 때 실행될 동작을 여기에 정의합니다.
            // 지금은 간단히 Toast 메시지를 띄워줍니다.
            Toast.makeText(requireContext(), "${person.name}님의 프로필입니다.", Toast.LENGTH_SHORT).show()

            // 추후에 상세 프로필 페이지로 이동하는 코드를 여기에 넣을 수 있습니다.
        }

        binding.rvPeopleList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPeopleList.adapter = adapter

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
