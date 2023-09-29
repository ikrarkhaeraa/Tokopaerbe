package com.example.tokopaerbe.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificationList")
class NotificationsEntity(

    @field:ColumnInfo(name = "notifId")
    @PrimaryKey(autoGenerate = true)
    val notifId: Int = 0,

    @field:ColumnInfo(name = "notifType")
    val notifType: String,

    @field:ColumnInfo("notifTitle")
    val notifTitle: String,

    @field:ColumnInfo("notifBody")
    val notifBody: String,

    @field:ColumnInfo("notifDate")
    val notifDate: String,

    @field:ColumnInfo("notifTime")
    val notifTime: String,

    @field:ColumnInfo("notifImage")
    val notifImage: String,

    @field:ColumnInfo("isChecked")
    var isChecked: Boolean = false,

)
