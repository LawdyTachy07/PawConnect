package com.example.pawconnect.UserManagement.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

@Serializable
private data class AuthResponse(
    @SerialName("access_token") val accessToken: String? = null,
    val user: AuthUser? = null,
    val msg: String? = null,
    @SerialName("error_description") val errorDescription: String? = null
)

@Serializable
private data class AuthUser(
    val id: String = "",
    val email: String = ""
)

class AuthRepository {
    private val client = SupabaseClient.httpClient
    private val baseUrl = SupabaseClient.SUPABASE_URL
    private val apiKey = SupabaseClient.SUPABASE_KEY

    companion object {
        var currentAccessToken: String? = null
        var currentUserId: String? = null
    }

    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        role: String,
        phoneNumber: String?,
        organizationName: String?
    ): Result<UserProfile> {
        return try {
            val payload = buildJsonObject {
                put("email", email)
                put("password", password)
                put("data", buildJsonObject {
                    put("full_name", fullName)
                    put("role", role)
                    if (!phoneNumber.isNullOrBlank()) put("phone_number", phoneNumber)
                    if (!organizationName.isNullOrBlank()) put("organization_name", organizationName)
                })
            }

            val response = client.post("$baseUrl/auth/v1/signup") {
                setBody(payload)
            }
            val rawText = response.bodyAsText()
            val authRes = SupabaseClient.jsonConfig.decodeFromString<AuthResponse>(rawText)

            if (!response.status.isSuccess() || authRes.user == null) {
                return Result.failure(Exception(authRes.errorDescription ?: authRes.msg ?: "Registration failed"))
            }

            currentAccessToken = authRes.accessToken
            currentUserId = authRes.user.id

            // 如果开启了邮箱验证，accessToken 可能为空，直接返回基础资料或提示登录
            if (authRes.accessToken.isNullOrBlank()) {
                return Result.failure(Exception("Registration succeeded! Please check your email or log in."))
            }

            fetchProfile(authRes.user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val payload = buildJsonObject {
                put("email", email)
                put("password", password)
            }

            val response = client.post("$baseUrl/auth/v1/token?grant_type=password") {
                setBody(payload)
            }
            val rawText = response.bodyAsText()
            val authRes = SupabaseClient.jsonConfig.decodeFromString<AuthResponse>(rawText)

            if (!response.status.isSuccess() || authRes.user == null || authRes.accessToken.isNullOrBlank()) {
                return Result.failure(Exception(authRes.errorDescription ?: authRes.msg ?: "Invalid email or password"))
            }

            currentAccessToken = authRes.accessToken
            currentUserId = authRes.user.id

            val profileResult = fetchProfile(authRes.user.id)
            val profile = profileResult.getOrNull()
            if (profile != null && profile.isDeactivated) {
                logout()
                return Result.failure(Exception("This account has been deactivated."))
            }
            profileResult
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchProfile(userId: String): Result<UserProfile> {
        return try {
            val token = currentAccessToken ?: apiKey
            val response = client.get("$baseUrl/rest/v1/profiles?id=eq.$userId&select=*") {
                header("Authorization", "Bearer $token")
            }
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("Failed to fetch profile: ${response.status}"))
            }
            val profiles: List<UserProfile> = response.body()
            val profile = profiles.firstOrNull()
                ?: return Result.failure(Exception("User profile not found."))
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfile(
        userId: String,
        fullName: String,
        phoneNumber: String?,
        address: String?,
        organizationName: String?
    ): Result<UserProfile> {
        return try {
            val token = currentAccessToken ?: return Result.failure(Exception("Not authenticated"))
            val payload = buildJsonObject {
                put("full_name", fullName)
                put("phone_number", phoneNumber)
                put("address", address)
                put("organization_name", organizationName)
            }

            val response = client.patch("$baseUrl/rest/v1/profiles?id=eq.$userId") {
                header("Authorization", "Bearer $token")
                header("Prefer", "return=representation")
                setBody(payload)
            }
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("Update failed: ${response.bodyAsText()}"))
            }
            fetchProfile(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deactivateAccount(userId: String): Result<Unit> {
        return try {
            val token = currentAccessToken ?: return Result.failure(Exception("Not authenticated"))
            val payload = buildJsonObject {
                put("is_deactivated", true)
            }

            val response = client.patch("$baseUrl/rest/v1/profiles?id=eq.$userId") {
                header("Authorization", "Bearer $token")
                setBody(payload)
            }
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("Failed to deactivate account"))
            }
            logout()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        try {
            val token = currentAccessToken
            if (!token.isNullOrBlank()) {
                client.post("$baseUrl/auth/v1/logout") {
                    header("Authorization", "Bearer $token")
                }
            }
        } catch (_: Exception) {
        } finally {
            currentAccessToken = null
            currentUserId = null
        }
    }

    fun getCurrentUserId(): String? = currentUserId
}