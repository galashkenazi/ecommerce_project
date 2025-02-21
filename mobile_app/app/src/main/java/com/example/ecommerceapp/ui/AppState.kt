package com.example.ecommerceapp.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.ecommerceapp.data.api.ApiService
import com.example.ecommerceapp.data.api.models.BusinessDetails
import com.example.ecommerceapp.data.api.models.BusinessDetailsWithRewards
import com.example.ecommerceapp.data.api.models.CreateRewardRequest
import com.example.ecommerceapp.data.api.models.LoginRequest
import com.example.ecommerceapp.data.api.models.RegisterRequest
import com.example.ecommerceapp.data.api.models.UpdateRewardRequest
import com.example.ecommerceapp.data.api.models.UserEnrollment
import com.example.ecommerceapp.data.api.models.UserModel
import com.example.ecommerceapp.data.local.TokenManager
import com.example.ecommerceapp.util.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

open class AppState(context: Context) : ViewModel() {
    private val tokenManager = TokenManager(context)
    private val apiService = ApiService()

    // Auth state
    var isLoggedIn by mutableStateOf(false)
        private set

    var isBusinessOwner by mutableStateOf(false)
        private set

    // User details
    private val _currentUser = MutableStateFlow<Resource<UserModel>>(Resource.Loading())
    val currentUser = _currentUser.asStateFlow()

    // Business listing
    private val _businesses = MutableStateFlow<Resource<List<BusinessDetailsWithRewards>>>(Resource.Loading())
    val businesses = _businesses.asStateFlow()

    // Business owner's details
    private val _myBusiness = MutableStateFlow<Resource<BusinessDetailsWithRewards?>>(Resource.Loading())
    val myBusiness = _myBusiness.asStateFlow()

    // User enrollments
    private val _enrollments = MutableStateFlow<Resource<List<UserEnrollment>>>(Resource.Loading())
    val enrollments = _enrollments.asStateFlow()


    init {
        viewModelScope.launch {
            tokenManager.token.collect { token ->
                apiService.updateToken(token)
                isLoggedIn = !token.isNullOrBlank()
                if (isLoggedIn) {
                    loadUserDetails()
                    loadBusinesses()
                    loadEnrollments()
                    if (isBusinessOwner) {
                        loadMyBusiness()
                    }
                }
            }
        }
    }

    private fun loadUserDetails() {
        viewModelScope.launch {
            try {
                _currentUser.value = Resource.Loading()
                val userDetails = apiService.getCurrentUser()
                isBusinessOwner = userDetails.isBusinessOwner
                _currentUser.value = Resource.Success(userDetails)
            } catch (e: Exception) {
                _currentUser.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Auth functions
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(username, password))
                tokenManager.saveToken(response.accessToken)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun register(username: String, password: String, email: String, isBusinessOwner: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("AppState",
                    "Registering user: $username, email: $email, isBusinessOwner: $isBusinessOwner")
                val response = apiService.register(RegisterRequest(
                    username = username,
                    password = password,
                    emailAddress = email,
                    isBusinessOwner = isBusinessOwner
                ))
                tokenManager.saveToken(response.accessToken)
            } catch (e: Exception) {
                Log.e("AppState", "Failed registering user: $e")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                apiService.logout()
                tokenManager.clearToken()
                _businesses.value = Resource.Loading()
                _enrollments.value = Resource.Loading()
                _currentUser.value = Resource.Loading()
                isBusinessOwner = false
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Business listing functions
    private fun loadBusinesses() {
        viewModelScope.launch {
            try {
                _businesses.value = Resource.Loading()
                val businesses = apiService.getAllBusinesses()
                _businesses.value = Resource.Success(businesses)
            } catch (e: Exception) {
                _businesses.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun loadMyBusiness() {
        viewModelScope.launch {
            try {
                _myBusiness.value = Resource.Loading()
                val businessDetails = apiService.getMyBusinessDetails()
                _myBusiness.value = Resource.Success(businessDetails)
            } catch (e: Exception) {
                if (e.message?.contains("404") == true) {
                    _myBusiness.value = Resource.Success(null) // No business exists yet
                } else {
                    _myBusiness.value = Resource.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun upsertBusinessDetails(details: BusinessDetails) {
        viewModelScope.launch {
            try {
                apiService.upsertBusinessDetails(details)
                loadMyBusiness() // Refresh business details after update
                loadBusinesses() // Also refresh the businesses list
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Rewards functions
    fun createReward(reward: CreateRewardRequest) {
        viewModelScope.launch {
            try {
                apiService.createReward(reward)
                loadMyBusiness()
                loadBusinesses()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteReward(rewardId: String) {
        viewModelScope.launch {
            try {
                apiService.deleteReward(rewardId)
                loadMyBusiness()
                loadBusinesses()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateReward(rewardId: String, reward: UpdateRewardRequest) {
        viewModelScope.launch {
            try {
                apiService.updateReward(rewardId, reward)
                loadMyBusiness()
                loadBusinesses()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Enrollment functions
    private fun loadEnrollments() {
        viewModelScope.launch {
            try {
                _enrollments.value = Resource.Loading()
                val enrollments = apiService.getUserEnrollments()
                _enrollments.value = Resource.Success(enrollments)
            } catch (e: Exception) {
                _enrollments.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun enrollToBusiness(businessId: String) {
        viewModelScope.launch {
            try {
                apiService.enrollToBusiness(businessId)
                loadEnrollments()
                loadBusinesses() // Refresh to update enrollment status
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun cancelEnrollment(businessId: String) {
        viewModelScope.launch {
            try {
                apiService.cancelEnrollment(businessId)
                loadEnrollments()
                loadBusinesses() // Refresh to update enrollment status
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun addPoints(userId: String, points: BigDecimal) {
        viewModelScope.launch {
            try {
                apiService.addPoints(userId, points)
                loadEnrollments() // Refresh points after update
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun redeemReward(userId: String, rewardId: String) {
        viewModelScope.launch {
            try {
                apiService.redeemReward(userId, rewardId)
                loadEnrollments() // Refresh points after redemption
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    private fun resetAllStates() {
        _currentUser.value = Resource.Loading()
        _businesses.value = Resource.Loading()
        _myBusiness.value = Resource.Loading()
        _enrollments.value = Resource.Loading()
        isBusinessOwner = false
    }
}