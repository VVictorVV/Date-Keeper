package com.example.madcamp.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp.R
import com.example.madcamp.databinding.FragmentGalleryBinding
import com.example.madcamp.people.PeopleManager

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val peopleList = PeopleManager.getPeople()

        val adapter = GalleryAdapter(peopleList) { person ->
            val fragment = GalleryDetailFragment.newInstance(person)  // 🔧 수정
            parentFragmentManager.beginTransaction()
                .replace(R.id.bottom_layout, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.rvMemoryList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvMemoryList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
