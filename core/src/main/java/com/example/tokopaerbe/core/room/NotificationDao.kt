package com.example.tokopaerbe.core.room

import androidx.lifecycle.LiveData
import androidx.room.Query

@androidx.room.Dao
interface NotificationDao {

    @Query("UPDATE notificationList SET isChecked = :isChecked WHERE notifId = :id ")
    fun notifIsChecked(id: Int, isChecked: Boolean)

    @Query("SELECT * FROM notificationList WHERE isChecked = :isChecked")
    fun getUnreadNotifications(isChecked: Boolean): LiveData<List<NotificationsEntity>?>

    @Query(
        "INSERT INTO notificationList (notifId," +
                "notifType," +
            "notifTitle, " +
            "notifBody, " +
            "notifDate, " +
            "notifTime, " +
            "notifImage, " +
            "isChecked) values (:notifId, :notifType, :notifTitle, :notifBody, :notifDate, :notifTime, :notifImage, :isChecked)"
    )
    suspend fun addNotifications(
        notifId: Int,
        notifType: String,
        notifTitle: String,
        notifBody: String,
        notifDate: String,
        notifTime: String,
        notifImage: String,
        isChecked: Boolean
    )

    @Query("SELECT * FROM notificationList")
    fun getNotifications(): LiveData<List<NotificationsEntity>?>

    @Query("DELETE FROM notificationList")
    fun deleteAllNotif()
}
