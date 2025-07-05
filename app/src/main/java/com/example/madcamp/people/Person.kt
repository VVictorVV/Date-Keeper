package com.example.madcamp.people

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

//이름, 별명, 대표 아이콘, 연락처, 기념일, 선물 정보, 추억 사진
@Parcelize
data class Person(
    val id: Long,
    var name: String,
    var nickname: String,
    var representativeIcon: String,
    var phoneNumber: String,
    var anniversary: MutableList<String>,
    var giftInfo: MutableList<String>,
    var memories: MutableList<Long>
) : Serializable, Parcelable
