package com.example.madcamp.calendar

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.madcamp.people.PeopleManager
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

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val anniversaryDates = mutableSetOf<LocalDate>()
    private var selectedDate: LocalDate? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)

        // 기념일 데이터 가져오기
        loadAnniversaries()

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
                        updateRegisterButtonVisibility()
                    }

                    textView.isSelected = selectedDate == day.date

                    if (anniversaryDates.contains(day.date)) {
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

        binding.registerAnniversaryButton.setOnClickListener {
            selectedDate?.let { date ->
                AlertDialog.Builder(requireContext())
                    .setTitle("${date.monthValue}월 ${date.dayOfMonth}일 기념일 등록")
                    .setMessage("여기에 기념일과 선물 정보를 입력하는 UI가 들어갑니다.")
                    .setPositiveButton("저장", null) // null은 버튼 클릭 시 창이 닫히지 않게 함
                    .setNegativeButton("취소", null)
                    .show()
            }
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

    private fun updateRegisterButtonVisibility() {
        if (selectedDate != null) {
            binding.registerAnniversaryButton.visibility = View.VISIBLE
        } else {
            binding.registerAnniversaryButton.visibility = View.INVISIBLE
        }
    }

    //모든 사람의 모든 기념일 불러오기
    private fun loadAnniversaries(){
        val people = PeopleManager.getPeople()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        people.forEach { person ->
            try {
                for (dateStr in person.anniversary) {
                    val date = LocalDate.parse(dateStr, formatter)
                    anniversaryDates.add(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
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