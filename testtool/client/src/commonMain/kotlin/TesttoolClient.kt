/*
 * Copyright (c) 2023-2024 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.testtool.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.content.*
import kotlinx.coroutines.flow.*
import kotlinx.io.*

object TesttoolClient {

    object Compatibility {

        suspend fun saveParameters(algorithm: String, path: String, bytes: ByteArray): String =
            postData("compatibility/$algorithm/$path", bytes)

        fun getParameters(algorithm: String, path: String): Flow<Pair<String, ByteArray>> =
            getData("compatibility/$algorithm/$path")

        suspend fun saveData(algorithm: String, path: String, parametersId: String, bytes: ByteArray): String =
            postData("compatibility/$algorithm/$path/$parametersId/data", bytes)

        fun getData(algorithm: String, path: String, parametersId: String): Flow<Pair<String, ByteArray>> =
            getData("compatibility/$algorithm/$path/$parametersId/data")
    }
}

internal expect fun hostOverride(): String?

private val client = HttpClient {
    expectSuccess = true
    install(DefaultRequest) {
        host = hostOverride() ?: ""
        port = 9000
    }
    install(HttpRequestRetry)
}

internal suspend fun postData(path: String, bytes: ByteArray): String = client.post(path) {
    setBody(ByteArrayContent(bytes))
}.body<Source>().use { it.readString() }

internal fun getData(path: String): Flow<Pair<String, ByteArray>> = flow {
    client.get(path).body<Source>().use { source ->
        while (!source.exhausted()) {
            val idLength = source.readInt()
            val id = source.readString(idLength.toLong())
            val contentLength = source.readInt()
            val content = source.readByteArray(contentLength)
            emit(id to content)
        }
    }
}
