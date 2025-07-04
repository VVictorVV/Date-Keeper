package com.example.madcamp.data

import java.io.Serializable

//이름, 별명, 대표 아이콘, 연락처, 기념일, 선물 정보, 추억 사진
data class Person(
    val name: String,
    val nickname: String,
    val representativeIcon: String,
    val phoneNumber: String,
    val anniversary: List<String>,
    val giftInfo: List<String>,
    val memories: List<Long>
) : Serializable
