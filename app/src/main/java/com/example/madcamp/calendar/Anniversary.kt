package com.example.madcamp.calendar

import kotlinx.android.parcel.Parcelize
import android.os.Parcelable
import java.time.LocalDate

@Parcelize
data class Anniversary(
    val date: String, //yyyy-MM-dd
    val name: String,
    var gift: String,
    val isYearly: Boolean
) : Parcelable