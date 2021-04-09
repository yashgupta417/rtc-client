package com.roomify_app.rtc_client.data.models

data class AgoraUser(
        var user:User?=null,
        var uid:Integer?=null,
        var me:Boolean?=null,
        var audioEnabled:Boolean?=null,
        var videoEnabled:Boolean?=null,
)
