package com.example.rtc_client.data.models

data class Message(
        var text:String?=null,
        var sender:User?=null,
        var timestamp:Long?=null,
//        var to:Room?=null,
)
