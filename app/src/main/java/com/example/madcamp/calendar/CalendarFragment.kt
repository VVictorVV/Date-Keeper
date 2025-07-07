package com.example.madcamp.calendar

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.madcamp.people.PeopleManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp.AnniversaryAdapter
import com.example.madcamp.people.PeopleAdapter
import com.example.madcamp.people.Person
import com.example.madcamp.R
import com.example.madcamp.databinding.FragmentCalendarBinding
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import org.w3c.dom.Text
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Date
import com.example.madcamp.calendar.Anniversary

data class AnniversaryDetails(val person: Person, val anniversary: Anniversary)

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val anniversaryMap = mutableMapOf<LocalDate, MutableList<AnniversaryDetails>>()
    private var selectedDate: LocalDate? = null
    private var isAnniversaryManagementMode: Boolean = false
    private var anniversaryAdapter: AnniversaryAdapter? = null

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

        // 캘린더의 날짜 선택 시
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
            val peopleTitle = dialogView.findViewById<TextView>(R.id.title_select_person)
            val peopleSpinner = dialogView.findViewById<android.widget.Spinner>(R.id.spinner_people_selection)
            val anniversaryNameTitle = dialogView.findViewById<TextView>(R.id.title_anniversary_name)
            val anniversaryNameEdit = dialogView.findViewById<EditText>(R.id.edit_anniversary_name)
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
            val peopleAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item,peopleList.map { it.name })
            peopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            peopleSpinner.adapter = peopleAdapter

            peopleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedPerson = peopleList[position]

                    val giftList = selectedPerson?.giftInfo
                    val displayGiftList = if (giftList.isNullOrEmpty()) {
                        listOf("없음")
                    } else {
                        giftList
                    }
                    val giftAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, displayGiftList)
                    giftSpinner.adapter = giftAdapter

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    selectedPerson = null
                    giftSpinner.adapter = null
                }
            }

            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener{
                    val anniversaryName = anniversaryNameEdit.text.toString().trim()
                    val selectedGiftItem = giftSpinner.selectedItem?.toString()
                    val selectedGift = if (selectedGiftItem == "없음") {
                        ""
                    } else {
                        selectedGiftItem ?: ""
                    }

                    if (selectedPerson == null){
                        android.widget.Toast.makeText(requireContext(), "사람을 선택해주세요.", android.widget.Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    if (anniversaryName.isEmpty()) {
                        android.widget.Toast.makeText(requireContext(), "기념일 이름을 입력해주세요.", android.widget.Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val dateString = date.format(formatter)
                    val newAnniversary = Anniversary (
                        date = dateString,
                        name = anniversaryName,
                        gift = selectedGift
                    )
                    selectedPerson!!.anniversary.add(newAnniversary)

                    val details = AnniversaryDetails(selectedPerson!!, newAnniversary)
                    anniversaryMap.getOrPut(date) { mutableListOf() }.add(details)

                    binding.calendarView.notifyDateChanged(date)
                    updateUIBasedOnSelection()

                    dialog.dismiss()
                }
            }
            dialog.show()
        }

        binding.btnManageAnniversaries.setOnClickListener {
            // 1. 상태 변수를 확인하여 관리 모드에 진입하거나 액션을 수행합니다.
            if (!isAnniversaryManagementMode) {
                // 관리 모드가 아닐 때 -> 관리 모드로 진입
                isAnniversaryManagementMode = true
                anniversaryAdapter?.setManagementMode(true)
            } else {
                // 관리 모드일 때 -> 체크된 아이템이 있으면 삭제, 없으면 관리 모드 취소
                val checkedCount = anniversaryAdapter?.getCheckedItems()?.size ?: 0
                if (checkedCount > 0) {
                    deleteSelectedAnniversaries()
                } else {
                    // 체크된 아이템 없이 '기념일 관리' 버튼을 다시 누르면 관리 모드 취소
                    isAnniversaryManagementMode = false
                    anniversaryAdapter?.setManagementMode(false)
                }
            }
        }

        binding.btnPreviousMonth.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.minusMonths(1))
            }
        }

        binding.btnNextMonth.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.plusMonths(1))
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

    // 날짜 선택에 따라 UI를 업데이트하는 함수
    private fun updateUIBasedOnSelection() {
        val date = selectedDate

        isAnniversaryManagementMode = false
        anniversaryAdapter?.setManagementMode(false)
        binding.btnManageAnniversaries.text = getString(R.string.calendar_anniversary_management)
        if (date != null) {
            binding.registerAnniversaryButton.visibility = View.VISIBLE
            val anniversaryDetailsList = anniversaryMap[date]
            if (!anniversaryDetailsList.isNullOrEmpty()) {
                binding.anniversaryListHeader.visibility = View.VISIBLE

                binding.anniversaryListTitle.text = "${date.monthValue}월 ${date.dayOfMonth}일의 기념일"

                anniversaryAdapter = AnniversaryAdapter(anniversaryDetailsList) { checkedCount ->
                    binding.btnManageAnniversaries.text = if (checkedCount > 0) {
                        getString(R.string.calendar_delete_selected)
                    } else {
                        getString(R.string.calendar_anniversary_management)
                    }
                }

                binding.rvAnniversaryList.adapter = anniversaryAdapter
                binding.rvAnniversaryList.visibility = View.VISIBLE
            } else {
                binding.anniversaryListHeader.visibility = View.GONE
                binding.rvAnniversaryList.visibility = View.GONE
            }
        } else { // 날짜 선택이 해제 됨
            binding.registerAnniversaryButton.visibility = View.INVISIBLE
            binding.anniversaryListHeader.visibility = View.GONE
            binding.rvAnniversaryList.visibility = View.GONE
        }
    }

    // 선택된 기념일 삭제
    private fun deleteSelectedAnniversaries() {
        val itemsToDelete = anniversaryAdapter?.getCheckedItems() ?: return
        if (itemsToDelete.isEmpty()) return

        // PeopleManager에서 데이터 삭제
        itemsToDelete.forEach { details ->
            val person = PeopleManager.getPersonById(details.person.id)
            val anniversary = details.anniversary
            person?.anniversary?.remove(anniversary)
        }

        // 데이터 로드
        loadAnniversaries()

        // UI 갱신
        isAnniversaryManagementMode = false
        val updatedList = anniversaryMap[selectedDate] ?: emptyList()
        anniversaryAdapter?.updateData(updatedList)

        if (updatedList.isEmpty()) {
            binding.anniversaryListHeader.visibility = View.GONE
            binding.rvAnniversaryList.visibility = View.GONE
            binding.calendarView.notifyCalendarChanged()
        } else {
            binding.calendarView.notifyDateChanged(selectedDate!!)
        }

        // 버튼 텍스트 초기화
        binding.btnManageAnniversaries.text = getString(R.string.calendar_anniversary_management)
        anniversaryAdapter?.setManagementMode(false)
    }

    //모든 사람의 모든 기념일 불러오기
    private fun loadAnniversaries(){
        anniversaryMap.clear()
        val people = PeopleManager.getPeople()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        people.forEach { person ->
            person.anniversary.forEach { anniversary ->
                try {
                    val date = LocalDate.parse(anniversary.date, formatter)
                    val details = AnniversaryDetails(person, anniversary)
                    anniversaryMap.getOrPut(date) { mutableListOf() }.add(details)
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