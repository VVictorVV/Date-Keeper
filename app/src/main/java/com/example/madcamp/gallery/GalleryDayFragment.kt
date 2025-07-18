package com.example.madcamp.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.people.Person
import com.example.madcamp.R
import com.example.madcamp.calendar.AnniversarySectionAdapter
import java.io.File
import android.widget.TextView

class GalleryDayFragment : Fragment() {
    private var person: Person? = null

    private var selectedImageUri: Uri? = null
    private lateinit var dialogImageView: ImageView

    private lateinit var sectionAdapter: AnniversarySectionAdapter

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                dialogImageView.setImageURI(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        person = arguments?.getParcelable("person")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.gallery_day, container, false)

        val btnAddPhoto = view.findViewById<ImageButton>(R.id.btnAddDayPhoto)
        btnAddPhoto.setOnClickListener {
            showAddMemoryDialog()
        }

        val galleryList = person?.memories ?: emptyList()

        // 1. 기념일별로 그룹화
        val grouped: Map<String, List<Gallery>> = galleryList.groupBy {
            it.anniversary?.name ?: "해당 없음"
        }

        // 별명 매핑
        val nickname = person?.nickname ?: "이름 없음"
        val fullText = "${nickname}과의 추억"

        val builder = SpannableStringBuilder(fullText)
        val nicknameStart = 0
        val nicknameEnd = nickname.length

        // 원하는 색으로 설정 (예: 보라색)
        val nicknameColor = ContextCompat.getColor(requireContext(), R.color.representation_icon_color) // 또는 원하는 색 리소스

        builder.setSpan(
            ForegroundColorSpan(nicknameColor),
            nicknameStart,
            nicknameEnd,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val nicknameTextView = view.findViewById<TextView>(R.id.tvPersonNickname)
        nicknameTextView.text = builder


        // 2. 섹션 어댑터 연결
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAnniversarySections)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        sectionAdapter = AnniversarySectionAdapter(grouped) { selectedAnniversaryName ->
            val fragment = GalleryDetailFragment.newInstance(person!!, selectedAnniversaryName)
            parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = sectionAdapter

        return view
    }

    private fun showAddMemoryDialog() {
        val personAnniversaries = person?.anniversary ?: emptyList()

        // "해당 없음"을 마지막에 추가
        val anniversaryDisplayList: List<String> =
            personAnniversaries.map { "${it.name} (${it.date})" } + "해당 없음"

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.gallery_dialog_add_memory, null)
        dialogImageView = dialogView.findViewById(R.id.image_preview)
        val anniversarySpinner = dialogView.findViewById<Spinner>(R.id.spinner_anniversary_selection)

        dialogImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, anniversaryDisplayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        anniversarySpinner.adapter = adapter

        AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setTitle("${person?.name}과의 추억 추가")
            .setView(dialogView)
            .setPositiveButton("저장") { _, _ ->
                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedPosition = anniversarySpinner.selectedItemPosition
                val selectedAnniversary = if (selectedPosition == personAnniversaries.size) {
                    null  // 마지막 항목이면 "해당 없음"
                } else {
                    personAnniversaries[selectedPosition]
                }

                val newMemory = Gallery(
                    id = System.currentTimeMillis(),
                    imageUri = selectedImageUri.toString(),
                    anniversary = selectedAnniversary
                )

                person?.memories?.add(newMemory)

                val updatedGrouped = person?.memories?.groupBy {
                    it.anniversary?.name ?: "해당 없음"
                } ?: emptyMap()
                sectionAdapter.updateData(updatedGrouped)
            }
            .setNegativeButton("취소", null)
            .show()
    }


    companion object {
        fun newInstance(person: Person): GalleryDayFragment {
            val fragment = GalleryDayFragment()
            val args = Bundle()
            args.putParcelable("person", person)
            fragment.arguments = args
            return fragment
        }
    }

}