package dev.whyoleg.cryptography.jdk.operations

import dev.whyoleg.cryptography.io.*
import dev.whyoleg.cryptography.jdk.*
import dev.whyoleg.cryptography.operations.signature.*
import javax.crypto.*

internal class JdkMacSignature(
    private val state: JdkCryptographyState,
    private val key: SecretKey,
    algorithm: String,
) : SignatureGenerator, SignatureVerifier {
    private val mac = state.mac(algorithm)

    override val signatureSize: Int get() = mac.use { it.macLength }

    override fun generateSignatureBlocking(dataInput: Buffer): Buffer = mac.use { mac ->
        mac.init(key)
        mac.doFinal(dataInput)
    }

    override fun generateSignatureBlocking(dataInput: Buffer, signatureOutput: Buffer): Buffer = mac.use { mac ->
        mac.init(key)
        mac.update(dataInput)
        mac.doFinal(signatureOutput, 0)
        signatureOutput
    }

    override fun verifySignatureBlocking(dataInput: Buffer, signatureInput: Buffer): Boolean {
        return generateSignatureBlocking(dataInput).contentEquals(signatureInput)
    }

    override suspend fun generateSignature(dataInput: Buffer): Buffer {
        return state.execute { generateSignatureBlocking(dataInput) }
    }

    override suspend fun generateSignature(dataInput: Buffer, signatureOutput: Buffer): Buffer {
        return state.execute { generateSignatureBlocking(dataInput, signatureOutput) }
    }

    override suspend fun verifySignature(dataInput: Buffer, signatureInput: Buffer): Boolean {
        return state.execute { verifySignatureBlocking(dataInput, signatureInput) }
    }
}