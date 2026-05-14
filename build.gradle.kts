plugins {
    id("version-catalog")
    id("maven-publish")
}

catalog {
    versionCatalog {
        from(files("gradle/libs.versions.toml"))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.vvicat.android"
            artifactId = "deps-catalog"
            version = providers.gradleProperty("VERSION_NAME")
                .orElse(providers.environmentVariable("VERSION_NAME"))
                .orElse("0.0.1")
                .get()
            from(components["versionCatalog"])
        }
    }

    repositories {
        val mavenUrl = providers.gradleProperty("MAVEN_URL")
            .orElse(providers.environmentVariable("MAVEN_URL"))

        if (mavenUrl.isPresent) {
            maven {
                url = uri(mavenUrl.get())

                credentials {
                    username = providers.gradleProperty("MAVEN_USERNAME")
                        .orElse(providers.environmentVariable("MAVEN_USERNAME"))
                        .orNull
                    password = providers.gradleProperty("MAVEN_PASSWORD")
                        .orElse(providers.environmentVariable("MAVEN_PASSWORD"))
                        .orNull
                }
            }
        }
    }
}
