plugins { id("com.android.application"); kotlin("android") }
android {
 namespace = "org.lgrevival.remote"
 compileSdk = 35
 testOptions { unitTests.isIncludeAndroidResources = true }
 defaultConfig { applicationId = "org.lgrevival.remote"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_21; targetCompatibility = JavaVersion.VERSION_21 }
 kotlinOptions { jvmTarget = "21" }
}
dependencies {
 testImplementation("junit:junit:4.13.2")
 testImplementation("org.robolectric:robolectric:4.14.1")
 implementation(project(":core")) { exclude(group = "org.json", module = "json") } }
// Robolectric's SDK resolver runs in a separate JVM; honor an explicitly configured build proxy.
tasks.withType<Test>().configureEach {
    listOf("https.proxyHost", "https.proxyPort", "http.proxyHost", "http.proxyPort").forEach { key ->
        System.getProperty(key)?.let { systemProperty(key, it) }
    }
    systemProperty("robolectric.dependency.repo.url", "https://repo.maven.apache.org/maven2")
}
