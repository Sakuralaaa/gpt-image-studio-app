# GPT Image Studio Android Wrapper

这个仓库提供一个 Android 原生壳应用，用来封装你部署在云端的 ChatGPT Image Studio 前端。

## 功能

- 输入并保存你的云端项目地址
- 输入并保存 OAuth Key（作为 `Authorization` 请求头）
- 在手机应用内通过 WebView 原生打开并使用你的项目
- 支持随时切换配置并管理目标站点

## 本地构建

```bash
./gradlew assembleDebug
```

生成产物位置：

- `app/build/outputs/apk/debug/app-debug.apk`
- `app/build/outputs/apk/release/app-release-unsigned.apk`
- `app/build/outputs/bundle/release/app-release.aab`

## GitHub Actions

已提供工作流：`.github/workflows/android-build.yml`

工作流会自动执行：

- `assembleDebug`
- `assembleRelease`
- `bundleRelease`

并上传 APK/AAB 构建产物。
