package com.example.madcamp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.AnniversaryAdapter
import com.example.madcamp.R
import com.example.madcamp.calendar.AnniversaryDetails
import com.example.madcamp.databinding.FragmentHomeBinding // 자동으로 생성된 바인딩 클래스
import com.example.madcamp.people.PeopleManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var anniversaryAdapter: AnniversaryAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        recyclerView = binding.rvUpcomingAnniversaries
        setupRecyclerView()
        loadAndSortAnniversaries()

        binding.checkboxShowDday.setOnCheckedChangeListener { _, isChecked ->
            anniversaryAdapter.setDisplayMode(isChecked)
        }

        return binding.root
    }

    // 기존 AnniversaryAdapter 재사용
    private fun setupRecyclerView() {
        anniversaryAdapter = AnniversaryAdapter(emptyList(), isCalendarMode = false) {}
        recyclerView.adapter = anniversaryAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    // 데이터 가져오기
    private fun loadAndSortAnniversaries() {
        val allAnniversaries = loadAllAnniversaryDetails()

        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val sortedList = allAnniversaries
            .mapNotNull { details ->
                try {
                    val anniversaryDate = LocalDate.parse(details.anniversary.date, formatter)
                    val daysUntil = ChronoUnit.DAYS.between(today, anniversaryDate)
                    if (daysUntil >= 0) Pair(details, daysUntil) else null
                } catch (e: Exception) {
                    null
                }
            }
            .sortedBy {it.second}
            .map {it.first}

        if (sortedList.isEmpty()) {
            binding.rvUpcomingAnniversaries.visibility = View.GONE
            binding.homeHeaderLayout.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
        } else {
            binding.rvUpcomingAnniversaries.visibility = View.VISIBLE
            binding.homeHeaderLayout.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            anniversaryAdapter.updateData(sortedList)
        }
    }

    private fun loadAllAnniversaryDetails(): List<AnniversaryDetails> {
        val allAnniversaryDetails = mutableListOf<AnniversaryDetails>()
        val people = PeopleManager.getPeople()

        people.forEach { person ->
            person.anniversary.forEach { anniversary ->
                val details = AnniversaryDetails(person, anniversary)
                allAnniversaryDetails.add(details)
            }
        }

        return allAnniversaryDetails
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}