package com.example.madcamp.people

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madcamp.databinding.PeopleInputBinding

class PeopleInputFragment : Fragment(){

    private var _binding: PeopleInputBinding? = null
    private val binding get() = _binding!!
    private var existingPerson: Person? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 여기에서 arguments로 전달받은 Person을 받아옴
        existingPerson = arguments?.getSerializable("person") as? Person
    }

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
                // 아직 등록되지 않은 선물 입력창이 있는지 확인
                val hasUnregisteredGift = (0 until binding.giftInput.childCount).any { i ->
                    val child = binding.giftInput.getChildAt(i) as? ViewGroup
                    child?.let {
                        (0 until it.childCount).any { j -> it.getChildAt(j) is EditText }
                    } ?: false
                }

                if (hasUnregisteredGift) {
                    Toast.makeText(requireContext(), "선물 항목은 '등록' 버튼을 눌러 추가해 주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 선호하는 선물 리스트
                val giftList = mutableListOf<String>()

                for (i in 0 until binding.giftInput.childCount) {
                    val row = binding.giftInput.getChildAt(i) as ViewGroup
                    for (j in 0 until row.childCount) {
                        val view = row.getChildAt(j)
                        // ✅ bullet이 아닌 "등록된 선물 TextView"만 추출
                        if (view is TextView && view.tag == "giftText") {
                            giftList.add(view.text.toString())
                        }
                    }
                }

                if (existingPerson != null) {
                    // 수정 모드: 기존 person 수정$
                    existingPerson?.apply {
                        name = this@PeopleInputFragment.binding.editName.text.toString().trim()
                        nickname = this@PeopleInputFragment.binding.editNickname.text.toString().trim()
                        phoneNumber = this@PeopleInputFragment.binding.editPhone.text.toString().trim()
                        giftInfo = giftList
                    }

                    Toast.makeText(requireContext(), "사람 정보가 수정되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    // 추가 모드
                    val newPerson = Person(
                        id = PeopleManager.generateId(),
                        name = name,
                        nickname = nickname,
                        representativeIcon = "",
                        phoneNumber = phone,
                        anniversary = mutableListOf(),
                        giftInfo = giftList,
                        memories = mutableListOf()
                    )
                    PeopleManager.addPerson(newPerson)
                    Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                }

                val allPeople = PeopleManager.getPeople()
                Log.d("PeopleDebug", "현재 PeopleManager 목록: $allPeople")

                Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()

                // 입력칸 초기화
                binding.editName.text.clear()
                binding.editPhone.text.clear()
                binding.editNickname.text.clear()
                binding.giftInput.removeAllViews()
                binding.giftInput.visibility = View.GONE

                parentFragmentManager.popBackStack()  // 이전 화면으로 돌아가기
            } else {
                Toast.makeText(requireContext(), "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addGift.setOnClickListener {
            val hasInputRow = (0 until binding.giftInput.childCount).any { i ->
                val child = binding.giftInput.getChildAt(i) as? ViewGroup
                child?.let {
                    (0 until it.childCount).any { j -> it.getChildAt(j) is EditText }
                } ?: false
            }

            if (hasInputRow) {
                // 입력창만 찾아서 제거
                val inputRow = (0 until binding.giftInput.childCount).firstOrNull { i ->
                    val child = binding.giftInput.getChildAt(i) as? ViewGroup
                    child?.let {
                        (0 until it.childCount).any { j -> it.getChildAt(j) is EditText }
                    } ?: false
                }
                inputRow?.let {
                    binding.giftInput.removeViewAt(inputRow)
                }

                // 버튼 복원
                binding.addGift.text = "+"
            } else {
                addGiftInputRow()
            }
        }

        val existingPerson = arguments?.getSerializable("person") as? Person
        existingPerson?.let {
            binding.editName.setText(it.name)
            binding.editPhone.setText(it.phoneNumber)
            binding.editNickname.setText(it.nickname)

            it.giftInfo.forEach { gift ->
                addGiftDisplayRow(gift)
            }
        }

    }

    private fun addGiftInputRow() {
        // 이미 입력창이 있으면 또 만들지 않음
        val alreadyHasInput = (0 until binding.giftInput.childCount).any { i ->
            val child = binding.giftInput.getChildAt(i) as? ViewGroup
            child?.let {
                (0 until it.childCount).any { j -> it.getChildAt(j) is EditText }
            } ?: false
        }

        if (alreadyHasInput) return

        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        val bulletTextView = TextView(requireContext()).apply {
            text = "●"
            textSize = 12f
            setPadding(0, 0, 16, 0)
        }

        val giftEditText = EditText(requireContext()).apply {
            hint = "선물 입력"
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            setSingleLine(true)
            imeOptions = EditorInfo.IME_ACTION_DONE
            inputType = InputType.TYPE_CLASS_TEXT
        }

        val registerButton = Button(requireContext()).apply {
            text = "등록"
        }

        registerButton.setOnClickListener {
            val giftText = giftEditText.text.toString().trim()
            if (giftText.isNotEmpty()) {
                val giftTextView = TextView(requireContext()).apply {
                    text = giftText
                    textSize = 16f
                    layoutParams = giftEditText.layoutParams
                    tag = "giftText"
                }

                val deleteButton = Button(requireContext()).apply {
                    text = "삭제"
                }

                deleteButton.setOnClickListener {
                    binding.giftInput.removeView(rowLayout)

                    // 입력창이 모두 사라졌을 경우 → 숨기기 (선택)
                    if (binding.giftInput.childCount == 0) {
                        binding.giftInput.visibility = View.GONE
                    }
                }

                rowLayout.removeView(giftEditText)
                rowLayout.removeView(registerButton)
                rowLayout.addView(giftTextView)
                rowLayout.addView(deleteButton)

                // 모든 row를 확인해보고 EditText가 하나도 없으면 + 버튼으로 바꿈
                val hasInputRow = (0 until binding.giftInput.childCount).any { i ->
                    val child = binding.giftInput.getChildAt(i) as? ViewGroup
                    child?.let {
                        (0 until it.childCount).any { j -> it.getChildAt(j) is EditText }
                    } ?: false
                }
                if (!hasInputRow) {
                    binding.addGift.text = "+"
                }
            }
        }

        rowLayout.addView(bulletTextView)
        rowLayout.addView(giftEditText)
        rowLayout.addView(registerButton)

        binding.giftInput.addView(rowLayout)
        binding.addGift.text = "✕"
        binding.giftInput.visibility = View.VISIBLE
    }

    private fun addGiftDisplayRow(gift: String) {
        val rowLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        val bulletTextView = TextView(requireContext()).apply {
            text = "●"
            textSize = 12f
            setPadding(0, 0, 16, 0)
        }

        val giftTextView = TextView(requireContext()).apply {
            text = gift
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            tag = "giftText" // 나중에 저장할 때 giftText만 추출하기 위한 tag
        }

        val deleteButton = Button(requireContext()).apply {
            text = "삭제"
            setOnClickListener {
                binding.giftInput.removeView(rowLayout)

                // 모두 제거되었을 때 giftInput 숨기기 + + 버튼 복원
                if (binding.giftInput.childCount == 0) {
                    binding.giftInput.visibility = View.GONE
                    binding.addGift.text = "+"
                }
            }
        }

        rowLayout.addView(bulletTextView)
        rowLayout.addView(giftTextView)
        rowLayout.addView(deleteButton)

        binding.giftInput.addView(rowLayout)
        binding.giftInput.visibility = View.VISIBLE
        binding.addGift.text = "+"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(person: Person): PeopleInputFragment {
            val fragment = PeopleInputFragment()
            val args = Bundle()
            args.putSerializable("person", person)
            fragment.arguments = args
            return fragment
        }
    }

}