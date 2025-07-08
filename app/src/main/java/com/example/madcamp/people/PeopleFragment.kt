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
                .replace(R.id.fragment_container_details, PeopleInputFragment())
                .addToBackStack(null)
                .commit()
        }

        val peopleList = PeopleManager.getPeople()

        // RecyclerView 어댑터 설정
        val adapter = PeopleAdapter(peopleList) { selectedPerson ->
            val fragment = PeopleDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("person", selectedPerson)
                }
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_details, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvPeopleList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPeopleList.adapter = adapter

        // 등록된 사람이 없을 때 안내 문구 보여주기
        if (peopleList.isEmpty()) {
            binding.rvPeopleList.visibility = View.GONE
            binding.tvEmptyPeople.visibility = View.VISIBLE
        } else {
            binding.rvPeopleList.visibility = View.VISIBLE
            binding.tvEmptyPeople.visibility = View.GONE
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
