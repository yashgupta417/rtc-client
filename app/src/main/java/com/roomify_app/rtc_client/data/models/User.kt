package com.roomify_app.rtc_client.data.models


data class User(
        var name: String?=null,
        var username: String?=null,
        var email: String?=null,
        var password: String?=null,
        var createdAt: Long?=null,
        var roomsCount: Int?=null,
        var image: String?=null,
)
