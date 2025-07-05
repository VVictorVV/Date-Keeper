package com.example.madcamp.gallery

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Gallery(
    val id: Long,
    val imageUri: String,
    var description: String = "",
    val timestamp: Long = System.currentTimeMillis()
) : Parcelable

