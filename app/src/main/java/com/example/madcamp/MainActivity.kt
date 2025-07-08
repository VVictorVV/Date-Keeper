package com.example.madcamp

import ViewPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp.R
import com.example.madcamp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "홈"

        // 1. ViewPager2에 어댑터 설정
        binding.viewPager.adapter = ViewPagerAdapter(this)
        binding.viewPager.isUserInputEnabled = false

        // 3. BottomNavigationView 아이템 선택 시 ViewPager2 페이지 변경
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> binding.viewPager.currentItem = 0
                R.id.menu_calendar -> binding.viewPager.currentItem = 1
                R.id.menu_people -> binding.viewPager.currentItem = 2
                R.id.menu_gallery -> binding.viewPager.currentItem = 3
            }
            true
        }

        // 4. ViewPager2 페이지 변경 시 BottomNavigationView 아이템 선택 상태 변경 (선택 사항)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigation.menu.getItem(position).isChecked = true

                // ▼▼▼ 페이지에 따라 툴바 제목 변경 ▼▼▼
                supportActionBar?.title = when (position) {
                    0 -> "홈"
                    1 -> "캘린더"
                    2 -> "사람들"
                    3 -> "갤러리"
                    else -> getString(R.string.app_name) // 기본 앱 이름
                }
                // ▲▲▲ 여기까지 추가 ▲▲▲
            }
        })
    }
}