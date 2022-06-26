package dev.whyoleg.cryptography.engine.webcrypto.external

import org.khronos.webgl.*
import kotlin.js.Promise

internal external interface Crypto {
    val subtle: SubtleCrypto
    fun getRandomValues(array: ByteArray): ByteArray
}

internal external interface SubtleCrypto {
    fun digest(algorithmName: String, data: ByteArray): Promise<ArrayBuffer>
    fun encrypt(algorithm: CipherAlgorithm, key: CryptoKey, data: ByteArray): Promise<ArrayBuffer>
    fun decrypt(algorithm: CipherAlgorithm, key: CryptoKey, data: ByteArray): Promise<ArrayBuffer>

    fun importKey(
        format: String /*"raw" | "pkcs8" | "spki"*/,
        keyData: ByteArray,
        algorithm: KeyAlgorithm,
        extractable: Boolean,
        keyUsages: Array<String>
    ): Promise<CryptoKey>

    fun generateKey(
        algorithm: SymmetricKeyAlgorithm,
        extractable: Boolean,
        keyUsages: Array<String>
    ): Promise<CryptoKey>

    fun generateKey(
        algorithm: AsymmetricKeyAlgorithm,
        extractable: Boolean,
        keyUsages: Array<String>
    ): Promise<CryptoKeyPair>
}

internal val WebCrypto: Crypto by lazy {
    val isNodeJs =
        js("typeof process !== 'undefined' && process.versions != null && process.versions.node != null").unsafeCast<Boolean>()
    if (isNodeJs) {
        js("eval('require')('node:crypto').webcrypto")
    } else {
        js("(window ? (window.crypto ? window.crypto : window.msCrypto) : self.crypto)")
    }
}

internal fun ArrayBuffer.toByteArray(): ByteArray = Int8Array(this).unsafeCast<ByteArray>()
