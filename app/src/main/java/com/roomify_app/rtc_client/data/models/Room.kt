package com.roomify_app.rtc_client.data.models

data class Room(
        var name: String?=null,
        var address: String?=null,
        var members: List<User>?=null,
        var owner: User?=null,
        var createdAt: Long?=null,
        var membersCount: Int?=null,
        var image: String?=null,
)
