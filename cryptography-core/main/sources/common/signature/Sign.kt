package dev.whyoleg.cryptography.signature

import dev.whyoleg.cryptography.*

public interface Signer {
    public val signatureSize: Int
}

public interface SyncSigner : Signer {
    public fun sign(dataInput: Buffer): Buffer
    public fun sign(dataInput: Buffer, signatureOutput: Buffer): Buffer
}

public interface AsyncSigner : Signer {
    public suspend fun sign(dataInput: Buffer): Buffer
    public suspend fun sign(dataInput: Buffer, signatureOutput: Buffer): Buffer
}

public interface SignFunction : Closeable {
    public val signatureSize: Int

    public fun update(dataInput: Buffer)

    public fun finish(): Buffer
    public fun finish(signatureOutput: Buffer): Buffer
}