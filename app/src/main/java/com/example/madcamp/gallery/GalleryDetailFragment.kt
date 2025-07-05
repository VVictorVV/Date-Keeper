package com.example.madcamp.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp.databinding.GalleryDetailBinding
import com.example.madcamp.people.Person

class GalleryDetailFragment : Fragment() {
    private var _binding: GalleryDetailBinding? = null
    private val binding get() = _binding!!

    private var person: Person? = null
    private lateinit var adapter: GalleryPagerAdapter  // ViewPager2용 어댑터

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val newGallery = Gallery(
                id = System.currentTimeMillis(),
                imageUri = uri.toString()
            )
            person?.memories?.add(newGallery)

            person?.memories?.toList()?.let { it1 -> adapter.submitList(it1) }
            binding.tvNoPhotos.visibility = View.GONE
            binding.viewPager.visibility = View.VISIBLE

            Toast.makeText(requireContext(), "이미지가 추가되었습니다", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        person = arguments?.getParcelable("person")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GalleryDetailBinding.inflate(inflater, container, false)

        binding.tvPersonName.text = "${person?.name} (${person?.nickname})"

        adapter = GalleryPagerAdapter()
        binding.viewPager.adapter = adapter

        if (person?.memories.isNullOrEmpty()) {
            binding.tvNoPhotos.visibility = View.VISIBLE
            binding.viewPager.visibility = View.GONE
        } else {
            binding.tvNoPhotos.visibility = View.GONE
            binding.viewPager.visibility = View.VISIBLE
            person?.memories?.toList()?.let { adapter.submitList(it) }

            // ViewPager 페이지 바뀔 때 화살표 보이기 설정
            binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateArrowVisibility(position)
                }
            })

            // 처음 페이지의 화살표 상태 설정
            updateArrowVisibility(0)
        }

        // 좌우 버튼 동작
        binding.btnLeft.setOnClickListener {
            val prev = binding.viewPager.currentItem - 1
            if (prev >= 0) binding.viewPager.currentItem = prev
        }

        binding.btnRight.setOnClickListener {
            val next = binding.viewPager.currentItem + 1
            if (next < adapter.itemCount) binding.viewPager.currentItem = next
        }

        // + 버튼 동작
        binding.btnAddPhoto.setOnClickListener {
            imagePicker.launch("image/*")
        }

        return binding.root
    }

    private fun updateArrowVisibility(position: Int) {
        val total = adapter.itemCount
        binding.btnLeft.visibility = if (position > 0) View.VISIBLE else View.GONE
        binding.btnRight.visibility = if (position < total - 1) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(person: Person): GalleryDetailFragment {
            val fragment = GalleryDetailFragment()
            val args = Bundle()
            args.putParcelable("person", person)
            fragment.arguments = args
            return fragment
        }
    }
}
