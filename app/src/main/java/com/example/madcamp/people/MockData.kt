package com.example.madcamp.people // 또는 다른 적절한 패키지

import android.content.ContentResolver
import android.content.Context
import androidx.annotation.DrawableRes
import com.example.madcamp.R
import com.example.madcamp.calendar.Anniversary
import com.example.madcamp.gallery.Gallery
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object MockData {
    private fun getUriForDrawable(context: Context, @DrawableRes resId: Int): String {
        val resources = context.resources
        return "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${resources.getResourcePackageName(resId)}/${resources.getResourceTypeName(resId)}/${resources.getResourceEntryName(resId)}"
    }

    fun getMockPeopleList(context: Context): List<Person> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        // --- 첫 번째 사람 데이터 ---
        val person1Anniversary1 = Anniversary(date = "2024-05-20", name = "제주도 여행", gift = "항공권", isYearly = false)
        val person1Anniversary2 = Anniversary(date = "2023-12-25", name = "크리스마스 파티", gift = "목도리", isYearly = true)

        val person1Memories = mutableListOf(
            Gallery(id = 101, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person1Anniversary1),
            Gallery(id = 102, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person1Anniversary1)
        )

        val person1 = Person(
            id = 1L,
            name = "김민준",
            nickname = "주니",
            representativeIcon = "icon_skull",
            phoneNumber = "123",
            anniversary = mutableListOf(person1Anniversary1, person1Anniversary2),
            memories = person1Memories,
            giftInfo = mutableListOf("항공권", "목도리")
        )

        return listOf(person1)
    }
}