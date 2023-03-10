plugins {
    `maven-publish`
}


publishing {
    publications {
        create<MavenPublication>("yarn") {
            artifact(file("aaa"))
        }
    }
}