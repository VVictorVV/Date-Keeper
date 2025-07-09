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

        // --- 엄마 ---
        val person1Anniversary1 = Anniversary(date = "2025-06-20", name = "결혼 기념일", gift = "제주도 여행 티켓", isYearly = true)
        val person1Anniversary2 = Anniversary(date = "2025-07-25", name = "생신", gift = "생크림 케이크", isYearly = true)

        val person1Memories = mutableListOf(
            Gallery(id = 101, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person1Anniversary1),
            Gallery(id = 102, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person1Anniversary2),
            Gallery(id = 103, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = null),
            Gallery(id = 104, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = null)
        )

        val person1 = Person(
            id = 1L,
            name = "어머니",
            nickname = "엄마",
            representativeIcon = "icon_smile",
            phoneNumber = "010-9201-3551",
            anniversary = mutableListOf(person1Anniversary1, person1Anniversary2),
            memories = person1Memories,
            giftInfo = mutableListOf("제주도 여행 티켓", "생크림 케이크", "조리 도구")
        )

        // --- 친구 (넙죽이) ---
        val person2Anniversary1 = Anniversary(date = "2025-07-06", name = "생일", gift = "치킨 기프티콘", isYearly = true)
        val person2Anniversary2 = Anniversary(date = "2025-06-12", name = "도쿄 여행", gift = "", isYearly = false)
        val person2Anniversary3 = Anniversary(date = "2025-05-15", name = "선생님 찾아뵙기", gift = "", isYearly = false)

        val person2Memories = mutableListOf(
            Gallery(id = 101, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person2Anniversary2),
            Gallery(id = 102, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person2Anniversary2),
            Gallery(id = 103, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person2Anniversary3)
        )

        val person2 = Person(
            id = 2L,
            name = "넙죽이",
            nickname = "넙치",
            representativeIcon = "icon_skull",
            phoneNumber = "010-9999-9999",
            anniversary = mutableListOf(person2Anniversary1, person2Anniversary2, person2Anniversary3),
            memories = person2Memories,
            giftInfo = mutableListOf("치킨 기프티콘","커피 기프티콘","오리 인형")
        )

        // --- 선생님 ---
        val person3Anniversary1 = Anniversary(date = "2025-07-06", name = "생신", gift = "만년필", isYearly = true)
        val person3Anniversary2 = Anniversary(date = "2025-05-15", name = "선생님 찾아뵙기", gift = "", isYearly = false)

        val person3Memories = mutableListOf(
            Gallery(id = 101, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person3Anniversary1),
            Gallery(id = 102, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person3Anniversary2)
        )

        val person3 = Person(
            id = 3L,
            name = "선생님",
            nickname = "쌤",
            representativeIcon = "icon_sunny",
            phoneNumber = "010-8282-2828",
            anniversary = mutableListOf(person3Anniversary1, person3Anniversary2),
            memories = person3Memories,
            giftInfo = mutableListOf("만년필")
        )

        // --- 연인 ---
        val person4Anniversary1 = Anniversary(date = "2025-05-16", name = "생일", gift = "향수", isYearly = true)
        val person4Anniversary2 = Anniversary(date = "2021-08-12", name = "4주년", gift = "커플링", isYearly = true)

        val person4Memories = mutableListOf(
            Gallery(id = 101, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person4Anniversary1),
            Gallery(id = 102, imageUri = getUriForDrawable(context, R.drawable.dogtest), anniversary = person4Anniversary2)
        )

        val person4 = Person(
            id = 4L,
            name = "안보현",
            nickname = "자기",
            representativeIcon = "icon_heart",
            phoneNumber = "010-1234-5678",
            anniversary = mutableListOf(person4Anniversary1, person4Anniversary2),
            memories = person4Memories,
            giftInfo = mutableListOf("향수","커플링","시계")
        )

        return listOf(person1, person2, person3, person4)
    }
}