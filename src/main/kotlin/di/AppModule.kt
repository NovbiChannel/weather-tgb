package org.novbicreate.di

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.novbicreate.common.MetadataCache
import org.novbicreate.domain.ApiRepository
import org.novbicreate.domain.ApiRepositoryImpl

val appModule = module {
    single { HttpClient(CIO) {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }
    } }
    single<ApiRepository> { ApiRepositoryImpl(get<HttpClient>()) }
    single { MetadataCache(lazy { get<ApiRepository>() }) }
}