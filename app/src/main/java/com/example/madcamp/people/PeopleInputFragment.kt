package com.example.madcamp.people

import android.annotation.SuppressLint
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
import android.app.AlertDialog
import android.content.Context
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.GridView
import androidx.core.widget.doAfterTextChanged
import com.example.madcamp.R

class PeopleInputFragment : Fragment(){
    private var _binding: PeopleInputBinding? = null
    private val binding get() = _binding!!
    private var existingPerson: Person? = null
    private var selectedIconName: String? = null

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
            val personName = binding.editName.text.toString().trim()
            val phone1 = binding.editPhone1.text.toString().trim()
            val phone2 = binding.editPhone2.text.toString().trim()
            val phone3 = binding.editPhone3.text.toString().trim()
            val fullPhoneNumber = "$phone1-$phone2-$phone3"
            val nickName = binding.editNickname.text.toString().trim()

            if (personName.isNotEmpty() && phone1.isNotEmpty() && phone2.isNotEmpty() && phone3.isNotEmpty() && nickName.isNotEmpty()) {
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
                        // bullet이 아닌 "등록된 선물 TextView"만 추출
                        if (view is TextView && view.tag == "giftText") {
                            giftList.add(view.text.toString())
                        }
                    }
                }

                if (existingPerson != null) {
                    // 수정 모드: 기존 person 수정
                    existingPerson?.apply {
                        name = this@PeopleInputFragment.binding.editName.text.toString().trim()
                        nickname = this@PeopleInputFragment.binding.editNickname.text.toString().trim()
                        val phone1edit = this@PeopleInputFragment.binding.editPhone1.text.toString().trim()
                        val phone2edit = this@PeopleInputFragment.binding.editPhone2.text.toString().trim()
                        val phone3edit = this@PeopleInputFragment.binding.editPhone3.text.toString().trim()
                        phoneNumber = "$phone1edit-$phone2edit-$phone3edit"
                        giftInfo = giftList
                        representativeIcon = selectedIconName ?: ""
                    }

                    Toast.makeText(requireContext(), "사람 정보가 수정되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    // 추가 모드
                    val newPerson = Person(
                        id = PeopleManager.generateId(),
                        name = personName,
                        nickname = nickName,
                        representativeIcon = selectedIconName ?: "",
                        phoneNumber = fullPhoneNumber,
                        anniversary = mutableListOf(),
                        giftInfo = giftList,
                        memories = mutableListOf()
                    )
                    PeopleManager.addPerson(newPerson)
                    Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }

                val allPeople = PeopleManager.getPeople()
                Log.d("PeopleDebug", "현재 PeopleManager 목록: $allPeople")

                Toast.makeText(requireContext(), "사람이 추가되었습니다!", Toast.LENGTH_SHORT).show()

                // 입력칸 초기화
                binding.editName.text.clear()
                binding.editPhone1.text.clear()
                binding.editPhone2.text.clear()
                binding.editPhone3.text.clear()
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

    @SuppressLint("DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageProfile.setOnClickListener {
            showIconSelectionDialog()
        }

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

        setupPhoneNumberAutoMoving()

        val existingPerson = arguments?.getSerializable("person") as? Person
        existingPerson?.let {
            binding.editName.setText(it.name)
            val phoneParts = it.phoneNumber.split("-")
            if (phoneParts.size == 3) {
                binding.editPhone1.setText(phoneParts[0])
                binding.editPhone2.setText(phoneParts[1])
                binding.editPhone3.setText(phoneParts[2])
            } else {
                binding.editPhone1.setText(it.phoneNumber)
            }
            binding.editNickname.setText(it.nickname)

            if (it.representativeIcon.isNotEmpty()) {
                selectedIconName = it.representativeIcon
                val resourceId = resources.getIdentifier(selectedIconName, "drawable", requireContext().packageName)
                if (resourceId != 0) {
                    binding.imageProfile.setImageResource(resourceId)
                }
            }

            it.giftInfo.forEach { gift ->
                addGiftDisplayRow(gift)
            }
        }
    }

    private fun setupPhoneNumberAutoMoving() {
        binding.editPhone1.doAfterTextChanged { text ->
            if (text?.length == 3) {
                binding.editPhone2.requestFocus()
            }
        }
        binding.editPhone2.doAfterTextChanged { text ->
            if (text?.length == 4) {
                binding.editPhone3.requestFocus()
            }
        }
    }

    private fun getIconList(): List<Pair<String, Int>> {
        val iconList = mutableListOf<Pair<String, Int>>()

        for (field in R.drawable::class.java.fields) {
            if (field.name.startsWith("icon_")) {
                val resourceId = field.getInt(null)
                iconList.add(Pair(field.name, resourceId))
            }
        }

        return iconList
    }

    private fun showIconSelectionDialog() {
        val iconList = getIconList()
        if (iconList.isEmpty()) {
            Toast.makeText(requireContext(), "선택할 수 있는 아이콘이 없습니다. (res/drawable에 'icon_'으로 시작하는 파일을 추가하세요)", Toast.LENGTH_LONG).show()
            return
        }

        val gridView = GridView(requireContext()).apply {
            numColumns = 4
            adapter = IconAdapter(requireContext(), iconList)
            gravity = Gravity.CENTER
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("아이콘 선택")
            .setView(gridView)
            .setNegativeButton("취소",null)
            .create()

        gridView.setOnItemClickListener { _, _, position, _ ->
            val (name, id) = iconList[position]
            selectedIconName = name
            binding.imageProfile.setImageResource(id)
            dialog.dismiss()
        }

        dialog.show()
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
                    handleGiftDeletion(rowLayout, giftText)
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

        giftEditText.requestFocus()

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(giftEditText, InputMethodManager.SHOW_IMPLICIT)
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
                handleGiftDeletion(rowLayout, gift)
            }
        }

        rowLayout.addView(bulletTextView)
        rowLayout.addView(giftTextView)
        rowLayout.addView(deleteButton)

        binding.giftInput.addView(rowLayout)
        binding.giftInput.visibility = View.VISIBLE
        binding.addGift.text = "+"
    }

    private fun handleGiftDeletion(rowLayout: ViewGroup, giftText: String) {
        val person = existingPerson ?: return

        val associatedAnniversaries = person.anniversary.filter { it.gift == giftText }

        if (associatedAnniversaries.isNotEmpty()) {
            AlertDialog.Builder(requireContext())
                .setTitle("선물 삭제")
                .setMessage("선택한 선물과 연관된 기념일이 존재합니다. 정말로 삭제하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    performCascadingDelete(giftText)
                    binding.giftInput.removeView(rowLayout)
                    checkIfGiftListIsEmpty()
                }
                .setNegativeButton("아니오", null)
                .show()
        } else {
            performCascadingDelete(giftText)
            binding.giftInput.removeView(rowLayout)
            checkIfGiftListIsEmpty()
        }
    }

    private fun performCascadingDelete(giftText: String) {
        existingPerson?.apply {
            giftInfo.remove(giftText)
            anniversary.forEach { ann ->
                if (ann.gift == giftText) {
                    ann.gift = ""
                }
            }
        }
    }

    private fun checkIfGiftListIsEmpty() {
        if (binding.giftInput.childCount == 0) {
            binding.giftInput.visibility = View.GONE
            binding.addGift.text = "+"
        }
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