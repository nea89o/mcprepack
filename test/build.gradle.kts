plugins {
    `maven-publish`
}


publishing {
    publications {
        create<MavenPublication>("yarn") {
            artifact(file("aaa")) { classifier = ""
            this.extension = "jar"}
        }
    }
    publications.filterIsInstance<MavenPublication>().forEach {
        it.pom {
            url.set("https://git.nea.moe/nea/mcprepack")
        }
    }
}

