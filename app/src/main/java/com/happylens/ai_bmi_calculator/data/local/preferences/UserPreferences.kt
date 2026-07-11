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
        val TARGET_WEIGHT = floatPreferencesKey("target_weight")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            name = preferences[USER_NAME] ?: "",
            age = preferences[USER_AGE] ?: 25,
            gender = Gender.valueOf(preferences[USER_GENDER] ?: Gender.MALE.name),
            targetWeight = preferences[TARGET_WEIGHT] ?: 65.0f
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
            preferences[TARGET_WEIGHT] = profile.targetWeight
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
