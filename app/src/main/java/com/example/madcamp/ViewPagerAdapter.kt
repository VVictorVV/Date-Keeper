import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.madcamp.calendar.CalendarFragment
import com.example.madcamp.gallery.GalleryFragment
import com.example.madcamp.home.HomeFragment
import com.example.madcamp.people.PeopleFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4 // 탭 개수

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> CalendarFragment()
            2 -> PeopleFragment()
            3 -> GalleryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}