package dev.whyoleg.cryptography.webcrypto

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.*
import dev.whyoleg.cryptography.algorithms.digest.*
import org.khronos.webgl.*
import kotlin.coroutines.*
import kotlin.js.Promise

internal suspend fun <T> Promise<T>.await() = suspendCoroutine<T> { continuation ->
    then(
        { continuation.resume(it) },
        { continuation.resumeWithException(it) }
    )
}

internal fun nonBlocking(): Nothing = throw CryptographyException("Only non-blocking(suspend) calls are supported in WebCrypto")

internal fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()

internal fun CryptographyAlgorithmId<Digest>.hashAlgorithmName(): String = when (this) {
    SHA1   -> "SHA-1"
    SHA256 -> "SHA-256"
    SHA384 -> "SHA-384"
    SHA512 -> "SHA-512"
    else   -> throw CryptographyException("Unsupported hash algorithm: ${this}")
}

//TODO: recheck
internal fun hashAlgorithmDigestSize(algorithm: String): Int = when (algorithm) {
    "SHA-1"   -> 20
    "SHA-256" -> 32
    "SHA-384" -> 48
    "SHA-512" -> 64
    else      -> throw CryptographyException("Unsupported hash algorithm: $algorithm")
}