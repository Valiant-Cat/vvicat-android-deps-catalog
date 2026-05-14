# Android Deps Catalog

共享的 Gradle Version Catalog 仓库，用于统一维护团队项目里的第三方依赖、AndroidX 依赖和 Gradle 插件版本。

发布后，下游项目只需要依赖一个 Maven 坐标，就可以复用这里维护的 `libs.versions.toml`。

## Maven 坐标

```text
com.vvicat.android:deps-catalog:<version>
```

真实版本号以发布时的 `VERSION_NAME` 为准。GitHub Actions 通过 tag 自动发布时，tag `vX.Y.Z` 会发布版本 `X.Y.Z`。

## 下游项目接入

在下游项目的 `settings.gradle.kts` 中引入：

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("your-maven-url")
            credentials {
                username = "your-maven-username"
                password = "your-maven-password"
            }
        }
    }

    versionCatalogs {
        create("libs") {
            from("com.vvicat.android:deps-catalog:<version>")
        }
    }
}
```

将 `<version>` 替换为已经发布的真实版本号，例如 `0.0.2`。

然后在模块 `build.gradle.kts` 中使用：

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

## 本仓库结构

```text
.
├── build.gradle.kts              # deps-catalog 发布配置
├── gradle/libs.versions.toml     # 统一依赖与插件版本
├── example                       # 独立下游 Android 项目使用示例
├── gradle.properties             # 非敏感 Gradle 配置
└── settings.gradle.kts           # 仓库与项目名配置
```

## 维护依赖

依赖统一维护在 [gradle/libs.versions.toml](gradle/libs.versions.toml)：

```toml
[versions]
coreKtx = "1.18.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
```

命名建议：

- AndroidX 依赖使用 `androidx-*`。
- Compose 依赖使用 `androidx-compose-*`。
- Android 测试依赖使用 `androidx-test-*`。
- Kotlin 官方插件使用 `kotlin-*`。
- 插件别名和库别名保持语义清晰，不使用 Android Studio 模板里的临时命名。

## 本地验证

修改 catalog 后先运行：

```bash
./gradlew generateCatalogAsToml
```

查看可用任务：

```bash
./gradlew tasks --all
```

验证独立 example 工程：

```bash
./gradlew publishToMavenLocal
./gradlew -p example testDebugUnitTest
./gradlew -p example assembleDebug
```

`example` 默认会从 `mavenLocal()` 读取 `com.vvicat.android:deps-catalog:0.0.1`，这是本地开发默认值，不代表远程 Maven 最新版本。如需验证其他 catalog 版本：

```bash
VERSION_CATALOG_VERSION=0.0.2 ./gradlew -p example testDebugUnitTest
```

发布到本机 Maven 仓库验证：

```bash
./gradlew publishToMavenLocal
```

## 发布到 Maven 仓库

发布命令：

```bash
./gradlew publish
```

## GitHub Actions 自动发布

仓库内置 [Deploy workflow](.github/workflows/deploy.yml)：

- `pull_request`：验证 catalog 生成、本机发布和 example 单元测试。
- `push main`：执行同样的轻量验证。
- `push tag v*`：先验证，再发布到 Maven 仓库。tag `v0.0.2` 会发布版本 `0.0.2`。
- `workflow_dispatch`：手动输入版本号并发布。

发布前需要在 GitHub 仓库 `Settings -> Secrets and variables -> Actions` 配置：

```text
MAVEN_URL
MAVEN_USERNAME
MAVEN_PASSWORD
```

发布新版本：

```bash
git tag v0.0.2
git push origin v0.0.2
```

指定版本发布：

```bash
VERSION_NAME=0.0.2 ./gradlew publish
```

发布版本和凭据不要写入仓库。请放到本机 `~/.gradle/gradle.properties`、环境变量或 CI Secret 中：

```properties
MAVEN_URL=your-maven-url
MAVEN_USERNAME=your-maven-username
MAVEN_PASSWORD=your-maven-password
VERSION_NAME=0.0.2
```

也可以使用环境变量：

```bash
export MAVEN_URL="your-maven-url"
export MAVEN_USERNAME="your-maven-username"
export MAVEN_PASSWORD="your-maven-password"
export VERSION_NAME="0.0.2"
./gradlew publish
```

## 版本发布规则

- 新增依赖别名：递增 patch 版本，例如 `0.0.1` -> `0.0.2`。
- 升级已有依赖版本：递增 patch 或 minor，取决于影响范围。
- 删除或重命名已有别名：视为破坏性变更，递增 minor 或 major，并提前通知下游项目。
- 发布前必须运行 `./gradlew generateCatalogAsToml`。
