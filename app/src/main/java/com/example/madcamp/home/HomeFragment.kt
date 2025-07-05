package com.example.madcamp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.madcamp.databinding.FragmentHomeBinding // 자동으로 생성된 바인딩 클래스

class HomeFragment : Fragment() {

    // View Binding 인스턴스를 저장할 변수
    private var _binding: FragmentHomeBinding? = null
    // _binding을 null 체크 없이 편하게 사용하기 위한 getter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // XML 레이아웃을 인플레이트하고 바인딩 객체를 초기화
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        // 생성된 뷰를 반환
        return binding.root
    }

    // View가 파괴될 때 바인딩 객체를 정리하여 메모리 누수 방지
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}