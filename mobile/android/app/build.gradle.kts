plugins { id("com.android.application"); kotlin("android") }
android {
 namespace = "org.lgrevival.remote"
 compileSdk = 35
 defaultConfig { applicationId = "org.lgrevival.remote"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1.0" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_21; targetCompatibility = JavaVersion.VERSION_21 }
 kotlinOptions { jvmTarget = "21" }
}
dependencies { implementation(project(":core")) { exclude(group = "org.json", module = "json") } }
