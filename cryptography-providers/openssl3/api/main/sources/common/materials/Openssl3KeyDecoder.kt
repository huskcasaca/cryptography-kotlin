package dev.whyoleg.cryptography.openssl3.materials

import dev.whyoleg.cryptography.algorithms.asymmetric.*
import dev.whyoleg.cryptography.materials.key.*
import dev.whyoleg.cryptography.openssl3.algorithms.*
import dev.whyoleg.cryptography.openssl3.internal.*
import dev.whyoleg.kcwrapper.libcrypto3.cinterop.*
import kotlinx.cinterop.*
import platform.posix.*

internal abstract class Openssl3PrivateKeyDecoder<KF : KeyFormat, K : Key>(
    algorithm: String,
) : Openssl3KeyDecoder<KF, K>(algorithm) {
    override fun selection(format: KF): Int = OSSL_KEYMGMT_SELECT_PRIVATE_KEY
    override fun inputStruct(format: KF): String = "PrivateKeyInfo"
}

internal abstract class Openssl3PublicKeyDecoder<KF : KeyFormat, K : Key>(
    algorithm: String,
) : Openssl3KeyDecoder<KF, K>(algorithm) {
    override fun selection(format: KF): Int = OSSL_KEYMGMT_SELECT_PUBLIC_KEY
    override fun inputStruct(format: KF): String = "SubjectPublicKeyInfo"
}

internal abstract class Openssl3KeyDecoder<KF : KeyFormat, K : Key>(
    private val algorithm: String,
) : KeyDecoder<KF, K> {
    protected abstract fun wrapKey(key: CPointer<EVP_PKEY>): K

    protected abstract fun selection(format: KF): Int
    protected abstract fun inputType(format: KF): String
    protected abstract fun inputStruct(format: KF): String

    override fun decodeFromBlocking(format: KF, input: ByteArray): K = memScoped {
        nativeHeap.safeAlloc<CPointerVar<EVP_PKEY>, _> { pkey ->
            val context = checkError(
                OSSL_DECODER_CTX_new_for_pkey(
                    pkey = pkey.ptr,
                    input_type = inputType(format).cstr.ptr,
                    input_struct = inputStruct(format).cstr.ptr,
                    keytype = algorithm.cstr.ptr,
                    selection = selection(format),
                    libctx = null,
                    propquery = null
                )
            )
            //println("PRI_DECODE: $format")
            //println("PRI_DECODE_SIZE[1]: ${input.size}")
            try {
                val pdataLenVar = alloc(input.size.convert<size_t>())
                val pdataVar = alloc<CPointerVar<UByteVar>> { value = allocArrayOf(input).reinterpret() }
                checkError(OSSL_DECODER_from_data(context, pdataVar.ptr, pdataLenVar.ptr))
                //println("PRI_DECODE_SIZE[2]: ${pdataLenVar.value}")
                wrapKey(checkNotNull(pkey.value))
            } finally {
                OSSL_DECODER_CTX_free(context)
            }
        }
    }
}