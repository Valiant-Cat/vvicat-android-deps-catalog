plugins {
    alias(deps.plugins.android.application)
}

android {
    namespace = "com.vvicat.depscatalog.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vvicat.depscatalog.example"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(deps.androidx.core.ktx)
    implementation(deps.androidx.lifecycle.runtime.ktx)
    implementation(deps.androidx.activity.compose)

    implementation(platform(deps.androidx.compose.bom))
    implementation(deps.androidx.compose.ui)
    implementation(deps.androidx.compose.ui.graphics)
    implementation(deps.androidx.compose.ui.tooling.preview)
    implementation(deps.androidx.compose.material3)

    testImplementation(deps.junit)

    androidTestImplementation(deps.androidx.test.ext.junit)
    androidTestImplementation(deps.androidx.test.espresso.core)
    androidTestImplementation(platform(deps.androidx.compose.bom))
    androidTestImplementation(deps.androidx.compose.ui.test.junit4)

    debugImplementation(deps.androidx.compose.ui.tooling)
    debugImplementation(deps.androidx.compose.ui.test.manifest)
}
