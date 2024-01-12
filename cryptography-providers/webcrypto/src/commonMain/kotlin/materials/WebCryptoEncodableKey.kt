/*
 * Copyright (c) 2023-2024 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.providers.webcrypto.materials

import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.providers.webcrypto.*
import dev.whyoleg.cryptography.providers.webcrypto.internal.*

internal class WebCryptoEncodableKey<KF : KeyFormat>(
    private val key: CryptoKey,
    private val keyFormat: (KF) -> String,
) : EncodableKey<KF> {
    override suspend fun encodeTo(format: KF): ByteArray {
        return WebCrypto.exportKey(
            format = keyFormat(format),
            key = key
        )
    }

    override fun encodeToBlocking(format: KF): ByteArray = nonBlocking()
}