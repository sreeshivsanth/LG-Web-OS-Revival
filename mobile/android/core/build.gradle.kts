plugins { kotlin("jvm"); `java-library` }
kotlin { jvmToolchain(21) }
dependencies {
 api("com.squareup.okhttp3:okhttp:4.12.0")
 implementation("org.json:json:20240303")
 testImplementation("junit:junit:4.13.2")
}
