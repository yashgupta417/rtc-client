package com.example.rtc_client.data.models

data class Message(
        var localId:String?=null,
        var text:String?=null,
        var sender:User?=null,
        var timestamp:Long?=null,
//        var to:Room?=null,
        var isSent:Boolean?=true,
)
