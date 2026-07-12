package com.happylens.ai_bmi_calculator.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.happylens.ai_bmi_calculator.domain.model.Gender
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_AGE = intPreferencesKey("user_age")
        val USER_GENDER = stringPreferencesKey("user_gender")
        val STARTING_WEIGHT = floatPreferencesKey("starting_weight")
        val TARGET_WEIGHT = floatPreferencesKey("target_weight")
        val LAST_HEIGHT = floatPreferencesKey("last_height")
        val LAST_WEIGHT = floatPreferencesKey("last_weight")
        val LAST_HEIGHT_UNIT = stringPreferencesKey("last_height_unit")
        val LAST_WEIGHT_UNIT = stringPreferencesKey("last_weight_unit")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[USER_NAME] ?: "",
            age = preferences[USER_AGE] ?: 25,
            gender = Gender.valueOf(preferences[USER_GENDER] ?: Gender.MALE.name),
            startingWeight = preferences[STARTING_WEIGHT] ?: 0f,
            targetWeight = preferences[TARGET_WEIGHT] ?: 0f,
            lastHeight = preferences[LAST_HEIGHT] ?: 170f,
            lastWeight = preferences[LAST_WEIGHT] ?: 68f,
            lastHeightUnit = preferences[LAST_HEIGHT_UNIT] ?: "cm",
            lastWeightUnit = preferences[LAST_WEIGHT_UNIT] ?: "kg"
        )
    }

    val targetWeight: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[TARGET_WEIGHT] ?: 65.0f
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED] ?: false
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = profile.name
            preferences[USER_AGE] = profile.age
            preferences[USER_GENDER] = profile.gender.name
            preferences[STARTING_WEIGHT] = profile.startingWeight
            preferences[TARGET_WEIGHT] = profile.targetWeight
            preferences[LAST_HEIGHT] = profile.lastHeight
            preferences[LAST_WEIGHT] = profile.lastWeight
            preferences[LAST_HEIGHT_UNIT] = profile.lastHeightUnit
            preferences[LAST_WEIGHT_UNIT] = profile.lastWeightUnit
        }
    }

    suspend fun saveTargetWeight(weight: Float) {
        context.dataStore.edit { preferences ->
            preferences[TARGET_WEIGHT] = weight
        }
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }
}
