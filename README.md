# Android Deps Catalog

[![Deploy](https://github.com/Valiant-Cat/android-deps-catalog/actions/workflows/deploy.yml/badge.svg)](https://github.com/Valiant-Cat/android-deps-catalog/actions/workflows/deploy.yml)
[![Release](https://img.shields.io/badge/release-0.0.3-blue)](https://github.com/Valiant-Cat/android-deps-catalog/releases)

Android 团队共享的 Gradle Version Catalog，用于集中维护第三方依赖、AndroidX 依赖和 Gradle 插件版本。

下游项目只需要引入一个 Maven 坐标，就可以复用本仓库维护的 `libs.versions.toml`。

## 当前版本

<!-- latest-version-start -->
当前发布版本：`0.0.3`
<!-- latest-version-end -->

```text
com.vvicat.android:deps-catalog:0.0.3
```

GitHub Actions 发布成功后会自动更新这里的版本号。通过 tag 发布时，`vX.Y.Z` 会发布 Maven 版本 `X.Y.Z`。

## 下游项目接入

在下游项目的 `settings.gradle.kts` 中配置 Maven 仓库和 catalog：

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://packages.aliyun.com/maven/repository/2179341-release-zlWhtt/")
            credentials {
                username = "your-maven-username"
                password = "your-maven-password"
            }
        }
    }

    versionCatalogs {
        create("libs") {
            from("com.vvicat.android:deps-catalog:0.0.3")
        }
    }
}
```

在模块 `build.gradle.kts` 中使用：

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}
```

## 仓库结构

```text
.
├── build.gradle.kts              # catalog 发布配置
├── gradle/libs.versions.toml     # 依赖、插件和版本别名
├── example                       # 独立下游 Android 项目示例
├── gradle.properties             # 非敏感 Gradle 配置
└── settings.gradle.kts           # 根项目配置
```

## 维护依赖

所有依赖和插件别名都维护在 [gradle/libs.versions.toml](gradle/libs.versions.toml)。

示例：

```toml
[versions]
coreKtx = "1.18.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
```

命名约定：

- AndroidX 依赖使用 `androidx-*`。
- Compose 依赖使用 `androidx-compose-*`。
- Android 测试依赖使用 `androidx-test-*`。
- Kotlin 官方插件使用 `kotlin-*`。
- 别名保持语义清晰，避免使用模板生成的临时命名。

## 本地验证

修改 catalog 后先验证生成结果：

```bash
./gradlew generateCatalogAsToml
```

发布到本机 Maven 仓库：

```bash
./gradlew publishToMavenLocal
```

验证独立 `example` 工程：

```bash
./gradlew -p example testDebugUnitTest
./gradlew -p example assembleDebug
```

`example` 默认从 `mavenLocal()` 读取：

```text
com.vvicat.android:deps-catalog:0.0.1
```

如需验证其他本地 catalog 版本：

```bash
VERSION_CATALOG_VERSION=0.0.2 ./gradlew -p example testDebugUnitTest
```

## 发布

### 自动发布

仓库内置 [Deploy workflow](.github/workflows/deploy.yml)。

触发规则：

- `pull_request`：只验证，不发布。
- `push main`：只验证，不发布。
- `push tag v*`：验证通过后发布到 Maven，并创建 GitHub Release。
- `workflow_dispatch`：手动输入版本号并发布。

发布前需要在 GitHub 仓库 `Settings -> Secrets and variables -> Actions` 配置：

```text
MAVEN_USERNAME
MAVEN_PASSWORD
```

发布新版本：

```bash
git tag v0.0.3
git push origin v0.0.3
```

发布成功后，workflow 会自动：

- 把 README 中的当前发布版本和接入示例更新为新版本。
- 创建同名 GitHub Release。
- 用 GitHub 自动生成的 release notes 作为 changelog。

### 手动发布

本机发布到远程 Maven：

```bash
VERSION_NAME=0.0.3 ./gradlew publish
```

发布配置不要写入仓库。请放到本机 `~/.gradle/gradle.properties`、环境变量或 GitHub Actions Secrets 中：

```properties
MAVEN_USERNAME=your-maven-username
MAVEN_PASSWORD=your-maven-password
VERSION_NAME=0.0.3
```

## 版本规则

- 新增依赖别名：递增 patch，例如 `0.0.2` -> `0.0.3`。
- 升级已有依赖：按影响范围递增 patch 或 minor。
- 删除或重命名别名：视为破坏性变更，递增 minor 或 major，并提前通知下游项目。
- 发布前必须至少运行 `./gradlew generateCatalogAsToml`。
