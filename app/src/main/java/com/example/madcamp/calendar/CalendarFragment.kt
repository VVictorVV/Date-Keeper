package com.example.madcamp.calendar

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.madcamp.people.PeopleManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp.AnniversaryAdapter
import com.example.madcamp.AnniversaryInfo
import com.example.madcamp.people.PeopleAdapter
import com.example.madcamp.people.Person
import com.example.madcamp.R
import com.example.madcamp.databinding.FragmentCalendarBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val anniversaryMap = mutableMapOf<LocalDate, MutableList<Person>>()
    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        // 기념일 데이터 가져오기
        loadAnniversaries()

        val anniversaryRecyclerView = binding.rvAnniversaryList
        anniversaryRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 달력 설정
        val calendarView = binding.calendarView
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        val firstDayOfWeek = DayOfWeek.SUNDAY

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE

                    container.view.setOnClickListener {
                        if (selectedDate == day.date) {
                            selectedDate = null
                            calendarView.notifyDayChanged(day)
                        } else {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            calendarView.notifyDateChanged(day.date)
                            oldDate?.let { calendarView.notifyDateChanged(it) }
                        }
                        updateUIBasedOnSelection()
                    }

                    textView.isSelected = selectedDate == day.date

                    if (anniversaryMap.containsKey(day.date)) {
                        dotView.visibility = View.VISIBLE
                    } else {
                        dotView.visibility = View.INVISIBLE
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                    dotView.visibility = View.INVISIBLE
                }
            }
        }

        // 기념일 등록 버튼 클릭 시
        binding.registerAnniversaryButton.setOnClickListener {
            val date = selectedDate ?: return@setOnClickListener

            // 다이얼로그 View, UI 세팅
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.calendar_dialog_add_anniversary, null)
            val peopleRecyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_people_selection)
            val giftTitle = dialogView.findViewById<TextView>(R.id.title_select_gift)
            val giftSpinner = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_gifts_selection)

            var selectedPerson: Person? = null

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("${date.monthValue}월 ${date.dayOfMonth}일 기념일 등록")
                .setView(dialogView)
                .setPositiveButton("저장") { _, _ ->
                }
                .setNegativeButton("취소", null)
                .create()

            val peopleList = PeopleManager.getPeople()
            val peopleAdapter = PeopleAdapter(peopleList) { person ->
                selectedPerson = person

                peopleRecyclerView.visibility = View.GONE
                giftTitle.visibility = View.VISIBLE
                giftSpinner.visibility = View.VISIBLE

                val giftAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, person.giftInfo)
                giftSpinner.adapter = giftAdapter
            }

            peopleRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
            peopleRecyclerView.adapter = peopleAdapter

            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener{
                    if (selectedPerson == null){
                        android.widget.Toast.makeText(requireContext(), "사람을 선택해주세요.", android.widget.Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val dateString = date.format(formatter)

                    if (!selectedPerson!!.anniversary.contains(dateString)) {
                        selectedPerson!!.anniversary.add(dateString)
                        android.widget.Toast.makeText(requireContext(), "기념일이 등록되었습니다.", android.widget.Toast.LENGTH_SHORT).show()
                    }

                    anniversaryMap.getOrPut(date) { mutableListOf() }.add(selectedPerson!!)
                    binding.calendarView.notifyDateChanged(date)
                    updateUIBasedOnSelection()

                    selectedDate = null
                    updateUIBasedOnSelection()

                    dialog.dismiss()
                }
            }

            dialog.show()
        }

        // 월 이동 시 상단 텍스트를 업데이트
        calendarView.monthScrollListener = { calendarMonth ->
            val formatter = DateTimeFormatter.ofPattern("yyyy년 M월")
            binding.calendarMonthText.text = calendarMonth.yearMonth.format(formatter)
        }

        calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        return binding.root
    }

    // 날짜 선택에 따라 UI를 업데이트하는 함수
    private fun updateUIBasedOnSelection() {
        val date = selectedDate
        if (date != null) {
            binding.registerAnniversaryButton.visibility = View.VISIBLE

            val peopleWithAnniversary = anniversaryMap[date]
            if (!peopleWithAnniversary.isNullOrEmpty()) {
                val anniversaryInfoList = peopleWithAnniversary.map {
                    AnniversaryInfo(it, "기념일") // 기념일 파트를 나중에 구체적인 내용으로 바꿔야 함.
                }
                binding.rvAnniversaryList.adapter = AnniversaryAdapter(anniversaryInfoList)
                binding.anniversaryListTitle.visibility = View.VISIBLE
                binding.rvAnniversaryList.visibility = View.VISIBLE
            } else {
                binding.anniversaryListTitle.visibility = View.GONE
                binding.rvAnniversaryList.visibility = View.GONE
            }
        } else { // 날짜 선택이 해제 됨
            binding.registerAnniversaryButton.visibility = View.INVISIBLE
            binding.anniversaryListTitle.visibility = View.GONE
            binding.rvAnniversaryList.visibility = View.GONE
        }
    }

    private fun updateRegisterButtonVisibility() {
        if (selectedDate != null) {
            binding.registerAnniversaryButton.visibility = View.VISIBLE
        } else {
            binding.registerAnniversaryButton.visibility = View.INVISIBLE
        }
    }

    //모든 사람의 모든 기념일 불러오기
    private fun loadAnniversaries(){
        anniversaryMap.clear()
        val people = PeopleManager.getPeople()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        people.forEach { person ->
            person.anniversary.forEach { dateStr ->
                try {
                    val date = LocalDate.parse(dateStr, formatter)
                    anniversaryMap.getOrPut(date) { mutableListOf() }.add(person)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendar_day_text)
    val dotView = view.findViewById<View>(R.id.calendar_day_dot)
    lateinit var day: CalendarDay // 날짜 정보 저장
}