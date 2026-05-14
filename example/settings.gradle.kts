pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("deps") {
            val catalogVersion = providers.gradleProperty("VERSION_CATALOG_VERSION")
                .orElse(providers.environmentVariable("VERSION_CATALOG_VERSION"))
                .orElse("0.0.1")
                .get()

            from("com.vvicat.android:deps-catalog:$catalogVersion")
        }
    }
}

rootProject.name = "android-deps-catalog-example"
