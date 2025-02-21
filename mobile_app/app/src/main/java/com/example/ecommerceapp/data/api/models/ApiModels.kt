package com.example.ecommerceapp.data.api.models

import com.example.ecommerceapp.data.api.serializers.BigDecimalSerializer
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val emailAddress: String,
    val isBusinessOwner: Boolean = false
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "bearer"
)

@Serializable
data class UserModel(
    val id: String,
    val username: String,
    val emailAddress: String,
    val isBusinessOwner: Boolean
)

@Serializable
data class BusinessDetails(
    val id: String,
    val businessName: String,
    val description: String? = null,
    val emailAddress: String,
    val address: String? = null,
    val phoneNumber: String? = null
)

@Serializable
data class Reward(
    val id: String,
    val businessId: String,
    val name: String,
    val description: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val requiredPoints: BigDecimal,
    val usageCount: Int,
    val validFromTimestamp: String? = null,
    val validUntilTimestamp: String? = null
)

@Serializable
data class BusinessDetailsWithRewards(
    val details: BusinessDetails,
    val rewards: List<Reward>,
    val similarBusinesses: List<BusinessDetails> = emptyList()
)

@Serializable
data class CreateRewardRequest(
    val name: String,
    val description: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val requiredPoints: BigDecimal,
    val validFromTimestamp: String? = null,
    val validUntilTimestamp: String? = null
)

@Serializable
data class UpdateRewardRequest(
    val name: String,
    val description: String? = null,
    @Serializable(with = BigDecimalSerializer::class)
    val requiredPoints: BigDecimal,
    val validFromTimestamp: String? = null,
    val validUntilTimestamp: String? = null
)

@Serializable
data class Enrollment(
    val id: String,
    val userId: String,
    val businessId: String,
    @Serializable(with = BigDecimalSerializer::class)
    val points: BigDecimal
)

@Serializable
data class UserEnrollment(
    val business: BusinessDetails,
    val enrollment: Enrollment
)

@Serializable
data class AddPointsRequest(
    val userId: String,
    @Serializable(with = BigDecimalSerializer::class)
    val points: BigDecimal
)

@Serializable
data class AddPointsResponse(
    val userId: String,
    @Serializable(with = BigDecimalSerializer::class)
    val newPointsBalance: BigDecimal
)

@Serializable
data class RedeemRewardRequest(
    val userId: String,
    val rewardId: String
)

@Serializable
data class RedeemRewardResponse(
    val success: Boolean,
    @Serializable(with = BigDecimalSerializer::class)
    val newPointsBalance: BigDecimal
)