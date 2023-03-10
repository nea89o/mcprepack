plugins {
    kotlin("jvm") version "1.8.0"
    application
}


java.toolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://maven.architectury.dev")
    maven("https://maven.fabricmc.net")
    maven("https://maven.minecraftforge.net")
    maven("https://repository.ow2.org/repositories/releases/")
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
    implementation("dev.architectury:architectury-pack200:0.1.3")
    implementation("net.fabricmc:stitch:0.6.2")
    implementation("net.minecraftforge:srgutils:0.4.13")
    implementation(platform("org.ow2.asm:asm-bom:9.4"))
    implementation("org.ow2.asm:asm")
    implementation("net.fabricmc:tiny-remapper:0.8.6")
    implementation("com.github.jponge:lzma-java:1.3")
    implementation("com.nothome:javaxdelta:2.0.1") // TODO maybe remove?
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.minecraftforge:mergetool:1.1.5")
}

application {
    mainClass.set("mcprepack.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
