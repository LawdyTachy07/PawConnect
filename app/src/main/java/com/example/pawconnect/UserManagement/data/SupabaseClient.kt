package com.example.pawconnect.UserManagement.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object SupabaseClient {
    const val SUPABASE_URL = "https://qwqoikjsrkyafwkxsjin.supabase.co"
    const val SUPABASE_KEY = "sb_publishable_H3oLP35j4v91Cc6RqyzJ1Q_yk7Dgt6p"

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        defaultRequest {
            header("apikey", SUPABASE_KEY)
            contentType(ContentType.Application.Json)
        }
    }
}