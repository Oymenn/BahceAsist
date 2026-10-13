plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // 1. common modülümüz
    api(project(":core:common"))

    // 2. Canlı veri akışı (Flow) ve Coroutine'ler (Eksik olan buydu!)
    api(libs.kotlinx.coroutines.core)

    // 3. Dependency Injection (@Inject)
    implementation(libs.javax.inject)

    // 4. Testler için
    testImplementation(libs.junit)
}