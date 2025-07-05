package com.example.madcamp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.madcamp.databinding.ActivityMainBinding
import android.util.Log
import com.example.madcamp.calendar.CalendarFragment
import com.example.madcamp.gallery.GalleryFragment
import com.example.madcamp.home.HomeFragment
import com.example.madcamp.people.PeopleFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "홈"
        supportFragmentManager.beginTransaction().replace(R.id.bottom_layout, HomeFragment()).commit()

        navigation()

        // enableEdgeToEdge()
    }

    private fun navigation() {
        val bottomNavigationView = binding.bottomNavigation
        // bottomNavigationView.selectedItemId = R.id.menu_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    replaceFragment(HomeFragment())
                    supportActionBar?.title = "홈" // 타이틀 변경
                    true
                }
                R.id.menu_calendar -> {
                    replaceFragment(CalendarFragment())
                    supportActionBar?.title = "캘린더" // 타이틀 변경
                    true
                }
                R.id.menu_people -> {
                    replaceFragment(PeopleFragment())
                    supportActionBar?.title = "연락처" // 타이틀 변경
                    true
                }
                R.id.menu_gallery -> {
                    replaceFragment(GalleryFragment())
                    supportActionBar?.title = "갤러리" // 타이틀 변경
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.bottom_layout, fragment).commit()
    }
}