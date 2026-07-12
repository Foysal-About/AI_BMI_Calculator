package com.happylens.ai_bmi_calculator.data.repository

import android.content.Context
import com.happylens.ai_bmi_calculator.data.local.database.AppDatabase
import com.happylens.ai_bmi_calculator.data.local.entity.toDomain
import com.happylens.ai_bmi_calculator.data.local.entity.toEntity
import com.happylens.ai_bmi_calculator.data.local.preferences.UserPreferences
import com.happylens.ai_bmi_calculator.domain.model.BmiRecord
import com.happylens.ai_bmi_calculator.domain.model.ChatMessage
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BmiRepository {
    private lateinit var database: AppDatabase
    private lateinit var preferences: UserPreferences
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun initialize(context: Context) {
        val appContext = context.applicationContext
        database = AppDatabase.getDatabase(appContext)
        preferences = UserPreferences(appContext)
        
        // Ensure default Guest profile exists in the database
        scope.launch {
            if (database.profileDao().getProfileByName("Guest User") == null) {
                database.profileDao().insertProfile(UserProfile(name = "Guest User", isTracked = false).toEntity())
            }
        }
    }

    val allProfiles: Flow<List<UserProfile>> by lazy {
        database.profileDao().getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun deleteProfile(profile: UserProfile) {
        scope.launch {
            database.profileDao().deleteProfile(profile.toEntity())
            // Also cleanup their records and messages
            // (In a real app, you might want a foreign key with cascade delete)
            // But here we can do it manually for now if needed.
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val records: Flow<List<BmiRecord>> by lazy {
        currentProfileName.flatMapLatest { name ->
            database.bmiDao().getAllRecords(name).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    fun addRecord(record: BmiRecord) {
        scope.launch {
            database.bmiDao().insertRecord(record.toEntity())
        }
    }

    fun deleteRecord(recordId: String) {
        scope.launch {
            database.bmiDao().deleteRecord(recordId)
        }
    }

    suspend fun getLatestRecord(): BmiRecord? = withContext(Dispatchers.IO) {
        val name = currentProfileName.first()
        database.bmiDao().getLatestRecord(name)?.toDomain()
    }

    fun getRecordCount(profileName: String): Flow<Int> {
        return database.bmiDao().getRecordCount(profileName)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val chatMessages: Flow<List<ChatMessage>> by lazy {
        currentProfileName.flatMapLatest { name ->
            database.bmiDao().getAllChatMessages(name).map { entities ->
                entities.map { it.toDomain() }
            }
        }
    }

    fun addChatMessage(message: ChatMessage) {
        scope.launch {
            database.bmiDao().insertChatMessage(message.toEntity())
        }
    }

    fun clearChatHistory() {
        scope.launch {
            val name = currentProfileName.first()
            database.bmiDao().clearChatHistory(name)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val userProfile: Flow<UserProfile> by lazy {
        currentProfileName.flatMapLatest { name ->
            database.profileDao().getAllProfiles().map { profiles ->
                profiles.find { it.name == name }?.toDomain() ?: UserProfile(name = name)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val targetWeight: Flow<Float> by lazy {
        userProfile.map { it.targetWeight }
    }

    fun updateTargetWeight(weight: Float) {
        scope.launch {
            val profile = userProfile.first()
            updateUserProfile(profile.copy(targetWeight = weight))
        }
    }

    val currentProfileName: Flow<String> by lazy {
        preferences.userProfile.map { 
            if (it.name.isBlank()) "Guest User" else it.name 
        }.onStart { emit("Guest User") }
    }

    val isOnboardingCompleted: Flow<Boolean> by lazy {
        preferences.isOnboardingCompleted
    }

    fun setOnboardingCompleted(completed: Boolean) {
        scope.launch {
            preferences.saveOnboardingCompleted(completed)
        }
    }

    fun updateUserProfile(profile: UserProfile) {
        scope.launch {
            database.profileDao().insertProfile(profile.toEntity())
            // Also update current profile in preferences
            preferences.saveUserProfile(profile)
        }
    }

    fun updateProfileName(name: String) {
        scope.launch {
            val profile = database.profileDao().getProfileByName(name)?.toDomain() 
                ?: UserProfile(name = name)
            preferences.saveUserProfile(profile)
        }
    }
}
