package com.happylens.ai_bmi_calculator.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.happylens.ai_bmi_calculator.data.repository.BmiRepository
import com.happylens.ai_bmi_calculator.domain.model.UserProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ProfileWithCount(
    val profile: UserProfile,
    val entries: Int
)

class ProfileViewModel : ViewModel() {

    val currentProfileName: StateFlow<String> = BmiRepository.currentProfileName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Guest User")

    @OptIn(ExperimentalCoroutinesApi::class)
    val profiles: StateFlow<List<ProfileWithCount>> = BmiRepository.allProfiles
        .flatMapLatest { profileList ->
            if (profileList.isEmpty()) return@flatMapLatest flowOf(emptyList<ProfileWithCount>())
            val flows = profileList.map { profile ->
                BmiRepository.getRecordCount(profile.name).map { count ->
                    ProfileWithCount(profile, count)
                }
            }
            combine(flows) { it.toList() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectProfile(name: String) {
        BmiRepository.updateProfileName(name)
    }

    fun createProfile(name: String, isTracked: Boolean) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newProfile = UserProfile(name = name, isTracked = isTracked)
            BmiRepository.updateUserProfile(newProfile)
            BmiRepository.updateProfileName(name)
        }
    }

    fun deleteProfile(profile: UserProfile) {
        viewModelScope.launch {
            BmiRepository.deleteProfile(profile)
            // If deleting current profile, switch to first available
            val current = currentProfileName.value
            if (profile.name == current) {
                val others = profiles.value.map { it.profile }.filter { it.name != current }
                if (others.isNotEmpty()) {
                    selectProfile(others.first().name)
                }
            }
        }
    }
}
