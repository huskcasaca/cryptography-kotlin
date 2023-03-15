/*
 * Copyright (c) 2023 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

package dev.whyoleg.cryptography.jdk

import java.security.*

public sealed class JdkProvider {
    public object Default : JdkProvider()
    public class Instance(public val provider: Provider) : JdkProvider()
    public class Name(public val provider: String) : JdkProvider()
}