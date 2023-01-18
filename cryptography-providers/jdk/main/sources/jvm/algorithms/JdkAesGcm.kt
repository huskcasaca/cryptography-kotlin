package dev.whyoleg.cryptography.jdk.algorithms

import dev.whyoleg.cryptography.*
import dev.whyoleg.cryptography.algorithms.symmetric.*
import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.jdk.*
import dev.whyoleg.cryptography.jdk.algorithms.*
import dev.whyoleg.cryptography.jdk.materials.*
import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.operations.cipher.*
import javax.crypto.spec.*

internal class JdkAesGcm(
    private val state: JdkCryptographyState,
) : AES.GCM {
    private val keyWrapper: (JSecretKey) -> AES.GCM.Key = { key ->
        object : AES.GCM.Key, EncodableKey<AES.Key.Format> by JdkEncodableKey(state, key) {
            override fun cipher(tagSize: BinarySize): AuthenticatedCipher = AesGcmCipher(state, key, tagSize)
        }
    }
    private val keyDecoder = JdkSecretKeyDecoder<AES.Key.Format, _>(state, "AES", keyWrapper)

    override fun keyDecoder(): KeyDecoder<AES.Key.Format, AES.GCM.Key> = keyDecoder
    override fun keyGenerator(keySize: SymmetricKeySize): KeyGenerator<AES.GCM.Key> = JdkSecretKeyGenerator(state, "AES", keyWrapper) {
        init(keySize.value.inBits, state.secureRandom)
    }
}

private const val ivSizeBytes = 12 //bytes for GCM

private class AesGcmCipher(
    private val state: JdkCryptographyState,
    private val key: JSecretKey,
    private val tagSize: BinarySize,
) : AuthenticatedCipher {
    private val cipher = state.cipher("AES/GCM/NoPadding")

    override fun encryptBlocking(plaintextInput: Buffer, associatedData: Buffer?): Buffer = cipher.use { cipher ->
        val iv = ByteArray(ivSizeBytes).also(state.secureRandom::nextBytes)
        cipher.init(JCipher.ENCRYPT_MODE, key, GCMParameterSpec(tagSize.inBits, iv), state.secureRandom)
        associatedData?.let(cipher::updateAAD)
        iv + cipher.doFinal(plaintextInput)
    }

    override fun decryptBlocking(ciphertextInput: Buffer, associatedData: Buffer?): Buffer = cipher.use { cipher ->
        cipher.init(JCipher.DECRYPT_MODE, key, GCMParameterSpec(tagSize.inBits, ciphertextInput, 0, ivSizeBytes), state.secureRandom)
        associatedData?.let(cipher::updateAAD)
        cipher.doFinal(ciphertextInput, ivSizeBytes, ciphertextInput.size - ivSizeBytes)
    }
}
