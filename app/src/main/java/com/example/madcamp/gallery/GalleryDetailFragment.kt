package com.example.madcamp.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp.databinding.GalleryDetailBinding
import com.example.madcamp.people.Person

class GalleryDetailFragment : Fragment(){
    private var _binding: GalleryDetailBinding? = null
    private val binding get() = _binding!!

    private var person: Person? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val gallery = Gallery(
                id = System.currentTimeMillis(),
                imageUri = it.toString()
            )
            person?.memories?.add(gallery)
            Toast.makeText(requireContext(), "이미지가 추가되었습니다", Toast.LENGTH_SHORT).show()
            // 필요 시 RecyclerView 갱신 코드 추가
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

        if (person?.memories.isNullOrEmpty()) {
            binding.tvNoPhotos.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.tvNoPhotos.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE

            // TODO: GalleryItemAdapter 같은 adapter를 따로 만들어야 함
            // 예시로만 둠
            // binding.recyclerView.adapter = GalleryItemAdapter(person!!.memories)
        }

        binding.btnAddPhoto.setOnClickListener {
            // TODO: 이미지 선택 → URI 저장 → person.memories에 추가
        }

        return binding.root
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