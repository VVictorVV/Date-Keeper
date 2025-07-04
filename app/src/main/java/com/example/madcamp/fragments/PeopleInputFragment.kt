package com.example.madcamp.fragments

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
import com.example.madcamp.PeopleManager
import com.example.madcamp.R
import com.example.madcamp.data.Person
import com.example.madcamp.databinding.PeopleInputBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale

class PeopleInputFragment : Fragment(){

    private var _binding: PeopleInputBinding? = null
    private val binding get() = _binding!!
    private var isGiftInputVisible = false

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
                // 선호하는 선물 리스트
                val giftList = mutableListOf<String>()

                if (giftList.isEmpty()) {
                    Toast.makeText(requireContext(), "선물 항목은 등록 버튼을 누르거나 입력 후 등록해주세요.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

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

                val newPerson = Person(
                    id = PeopleManager.generateId(),
                    name = name,
                    nickname = nickname,
                    representativeIcon = "", // 아직 없으니 빈 문자열
                    phoneNumber = phone,
                    anniversary = listOf(),
                    giftInfo = giftList,
                    memories = listOf()
                )
                PeopleManager.addPerson(newPerson)

                val allPeople = PeopleManager.getPeople()
                Log.d("PeopleDebug", "현재 PeopleManager 목록: $allPeople")

                Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()

                // 입력칸 초기화
                binding.editName.text.clear()
                binding.editPhone.text.clear()
                binding.editNickname.text.clear()
                binding.giftInput.removeAllViews()
                binding.giftInput.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "모든 칸을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addGift.setOnClickListener {
            if (binding.giftInput.childCount == 0) {
                addGiftInputRow()
            } else {
                // 현재 입력 중인 선물 입력창 포함해서 전부 제거
                binding.giftInput.removeAllViews()
                binding.giftInput.visibility = View.GONE
                binding.addGift.text = "+"
            }
        }
    }

    private fun addGiftInputRow() {
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
            }
        }

        rowLayout.addView(bulletTextView)
        rowLayout.addView(giftEditText)
        rowLayout.addView(registerButton)

        binding.giftInput.addView(rowLayout)
        binding.addGift.text = "✕"
        binding.giftInput.visibility = View.VISIBLE
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}