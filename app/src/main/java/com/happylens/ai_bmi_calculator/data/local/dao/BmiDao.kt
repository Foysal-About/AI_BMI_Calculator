package com.happylens.ai_bmi_calculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.happylens.ai_bmi_calculator.data.local.entity.BmiEntity
import com.happylens.ai_bmi_calculator.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BmiDao {
    @Query("SELECT * FROM bmi_records WHERE profileName = :profileName ORDER BY date DESC")
    fun getAllRecords(profileName: String): Flow<List<BmiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: BmiEntity)

    @Query("DELETE FROM bmi_records WHERE id = :id")
    suspend fun deleteRecord(id: String)

    @Query("SELECT * FROM bmi_records WHERE profileName = :profileName ORDER BY date DESC LIMIT 1")
    suspend fun getLatestRecord(profileName: String): BmiEntity?

    @Query("SELECT COUNT(*) FROM bmi_records WHERE profileName = :profileName")
    fun getRecordCount(profileName: String): Flow<Int>

    @Query("SELECT * FROM chat_messages WHERE profileName = :profileName ORDER BY timestamp ASC")
    fun getAllChatMessages(profileName: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE profileName = :profileName")
    suspend fun clearChatHistory(profileName: String)
}
