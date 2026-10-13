plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // api ile açıyoruz: common'ı kullanan domain modülü de Coroutine'leri doğrudan kullanabilsin
    api(libs.kotlinx.coroutines.core)

    // @Inject anotasyonu için
    implementation(libs.javax.inject)

    // Birim testler için
    testImplementation(libs.junit)
}