package com.example.madcamp.gallery

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
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

        // 2. 섹션 어댑터 연결
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvAnniversarySections)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        sectionAdapter = AnniversarySectionAdapter(grouped, person!!, parentFragmentManager)
        recyclerView.adapter = sectionAdapter

        return view
    }

    private fun showAddMemoryDialog() {
        val personAnniversaries = person?.anniversary ?: emptyList()

        val anniversaryDisplayList: List<String>
        if (personAnniversaries.isEmpty()) {
            anniversaryDisplayList = listOf("해당 없음")
        } else {
            anniversaryDisplayList = personAnniversaries.map { "${it.name} (${it.date})" }
        }

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.gallery_dialog_add_memory, null)
        dialogImageView = dialogView.findViewById(R.id.image_preview)
        val anniversarySpinner = dialogView.findViewById<Spinner>(R.id.spinner_anniversary_selection)

        dialogImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, anniversaryDisplayList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        anniversarySpinner.adapter = adapter

        AlertDialog.Builder(requireContext())
            .setTitle("${person?.name}과의 추억 추가")
            .setView(dialogView)
            .setPositiveButton("저장") {_,_ ->
                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedAnniversary = if (personAnniversaries.isEmpty()) {
                    null
                } else {
                    val selectedPosition = anniversarySpinner.selectedItemPosition
                    personAnniversaries[selectedPosition]
                }

                val imageUriToSave = selectedImageUri.toString()

                val newMemory = Gallery(
                    id = System.currentTimeMillis(),
                    imageUri = imageUriToSave,
                    anniversary = selectedAnniversary
                )

                person?.memories?.add(newMemory)

                val updatedGrouped = person?.memories?.groupBy { it.anniversary?.name ?: "해당 없음" } ?: emptyMap()
                sectionAdapter.updateData(updatedGrouped)
            }
            .setNegativeButton("취소",null)
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