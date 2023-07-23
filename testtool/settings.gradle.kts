/*
 * Copyright (c) 2023 Oleg Yukhnevich. Use of this source code is governed by the Apache 2.0 license.
 */

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("../gradle/plugins/kotlin-version-catalog")
}

plugins {
    id("kotlin-version-catalog")
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    versionCatalogs {
        val libs by creating {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "testtool"

includeBuild("../gradle/plugins/build-parameters")

include("client")
include("server")
include("plugin")