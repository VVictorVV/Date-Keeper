package com.example.madcamp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp.IconAdapter
import com.example.madcamp.R
import com.example.madcamp.databinding.FragmentPeopleBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Locale

class PeopleFragment : Fragment() {
    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addAnniversaryButton.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("기념일 선택")
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDateInMillis ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = sdf.format(selectedDateInMillis)

                binding.anniversaryTextView.text = formattedDate
            }

            datePicker.show(childFragmentManager, "datePicker")
        }

        binding.peopleImageView.setOnClickListener {
            showIconPickerDialog()
        }
    }

    private fun showIconPickerDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.icon_dialog_picker, null)
        val iconRecyclerView = dialogView.findViewById<RecyclerView>(R.id.icon_recycler_view)

        val icons = listOf(
            R.drawable.dogtest
        )

        val adapter = IconAdapter(icons) { selectedIconResId ->
            binding.peopleImageView.setImageResource(selectedIconResId)
        }

        iconRecyclerView.adapter = adapter
        iconRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle("아이콘 선택")
            .setView(dialogView)
            .setNegativeButton("취소", null)
            .create()

        adapter.onIconSelected = { selectedIconResId ->
            binding.peopleImageView.setImageResource(selectedIconResId)
            dialog.dismiss()
        }

        dialog.show()
    }

    // View가 파괴될 때 바인딩 객체를 정리하여 메모리 누수 방지
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}