package com.example.madcamp.people

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp.AnniversaryAdapter
import com.example.madcamp.R
import com.example.madcamp.calendar.AnniversaryDetails
import com.example.madcamp.databinding.PeopleDetailBinding
import com.example.madcamp.gallery.GalleryDetailFragment

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
            binding.textName.text = "${it.name} (${it.nickname})"
            binding.textPhone.text = "전화번호: ${it.phoneNumber}"
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        person?.let { person ->
            binding.textName.text = "${person.name} (${person.nickname})"
            binding.textPhone.text = "${person.phoneNumber}"
            if (person.giftInfo.isNotEmpty()) {
                val giftAdapter = GiftAdapter(person.giftInfo)
                binding.rvGift.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = giftAdapter
                }
            }

            if (person.anniversary.isNotEmpty()) {
                val anniversaryDetails = person.anniversary.map { AnniversaryDetails(person, it) }
                val anniversaryAdapter = AnniversaryAdapter(anniversaryDetails, isCalendarMode = false) {}

                anniversaryAdapter.setDetailViewMode(true)

                binding.rvPersonAnniversaries.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = anniversaryAdapter
                }
            }

            // 수정 버튼
            binding.btnEdit.setOnClickListener {
                val editFragment = PeopleInputFragment.newInstance(person)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_layout, editFragment)
                    .addToBackStack(null)
                    .commit()
            }

            // 삭제 버튼 처리
            binding.btnDelete.setOnClickListener {
                PeopleManager.removePerson(person)
                Toast.makeText(requireContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()  // 목록으로 돌아가기
            }

            binding.btnViewMemory.setOnClickListener {
                val fragment = GalleryDetailFragment.newInstance(person)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.bottom_layout, fragment)
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