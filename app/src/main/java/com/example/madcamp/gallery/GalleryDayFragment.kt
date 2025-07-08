package com.example.madcamp.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.madcamp.people.Person
import com.example.madcamp.R

class GalleryDayFragment : Fragment() {
    private var person: Person? = null

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
            // TODO: + 버튼 클릭 시 동작 구현
        }

        return view
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