package com.example.rtc_client.data.models


data class User(
        val name: String?=null,
        val username: String?=null,
        val email: String?=null,
        val password: String?=null,
        val createdAt: Long?=null,
        val roomsCount: Int?=null,
)
