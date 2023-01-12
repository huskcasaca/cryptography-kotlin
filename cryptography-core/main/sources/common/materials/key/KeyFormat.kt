package dev.whyoleg.cryptography.materials.key

import dev.whyoleg.cryptography.provider.*

@SubclassOptInRequired(CryptographyProviderApi::class)
public interface KeyFormat {
    public interface RAW : KeyFormat
    public interface PEM : KeyFormat
    public interface DER : KeyFormat
    public interface JWK : KeyFormat
}
