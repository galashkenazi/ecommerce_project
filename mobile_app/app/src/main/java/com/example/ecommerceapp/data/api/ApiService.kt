package com.example.ecommerceapp.data.api

import com.example.ecommerceapp.data.api.client.NetworkClient
import com.example.ecommerceapp.data.api.models.*
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import java.math.BigDecimal

class ApiService(private var token: String? = null) {
    private val client = NetworkClient.createHttpClient { token }

    companion object {
        // This is currently points to a locally running server. To find the correct IP for the
        // server, make sure your mobile app is connected to the same wifi as your computer is
        // running on. Then, running "ifconfig | grep 192" on mac and linnux, or use ipconfig
        // instead for windows. Add the matching IP here (leave the 5013 port as is).
        private const val BASE_URL = "http://192.168.223.100:5013"
    }

    // Auth endpoints
    suspend fun register(request: RegisterRequest): TokenResponse =
        client.post("$BASE_URL/auth/register") { setBody(request) }.body()

    suspend fun login(request: LoginRequest): TokenResponse =
        client.post("$BASE_URL/auth/login") { setBody(request) }.body()

    suspend fun logout() =
        client.post("$BASE_URL/auth/logout")

    suspend fun getCurrentUser(): UserModel =
        client.get("$BASE_URL/auth/me").body()

    // Business endpoints
    suspend fun getAllBusinesses(): List<BusinessDetailsWithRewards> =
        client.get("$BASE_URL/businesses").body()

    suspend fun upsertBusinessDetails(details: BusinessDetails) =
        client.put("$BASE_URL/businesses") { setBody(details) }

    suspend fun getMyBusinessDetails(): BusinessDetailsWithRewards =
        client.get("$BASE_URL/businesses/me").body()

    // Rewards endpoints
    suspend fun createReward(reward: CreateRewardRequest) =
        client.post("$BASE_URL/businesses/rewards") { setBody(reward) }

    suspend fun deleteReward(rewardId: String) =
        client.delete("$BASE_URL/businesses/rewards/$rewardId")

    suspend fun updateReward(rewardId: String, reward: UpdateRewardRequest) =
        client.put("$BASE_URL/businesses/rewards/$rewardId") { setBody(reward) }

    // Enrollment endpoints
    suspend fun getUserEnrollments(): List<UserEnrollment> =
        client.get("$BASE_URL/enrollments/me").body()

    suspend fun enrollToBusiness(businessId: String) =
        client.post("$BASE_URL/enrollments/businesses/$businessId")

    suspend fun cancelEnrollment(businessId: String) =
        client.delete("$BASE_URL/enrollments/businesses/$businessId")

    suspend fun addPoints(userId: String, points: BigDecimal) =
        client.post("$BASE_URL/enrollments/add_points") {
            setBody(AddPointsRequest(userId, points))
        }.body<AddPointsResponse>()

    suspend fun redeemReward(userId: String, rewardId: String) =
        client.post("$BASE_URL/enrollments/redeem_reward") {
            setBody(RedeemRewardRequest(userId, rewardId))
        }.body<RedeemRewardResponse>()

    fun updateToken(newToken: String?) {
        token = newToken
    }
}