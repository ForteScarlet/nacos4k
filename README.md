# Nacos For Ktor
通过 [Ktor](https://ktor.io) 来使用 [nacos](https://nacos.io/) 吧！


## 前言
实用主义万岁！在做某些项目的时候想要试着脱离Spring，但是nacos却拖住了我的步伐。实际上实现一个 "可用的" nacos for ktor并不麻烦，但是如果想要全面一点，那可能还差点儿意思。




## 安装

### 模块说明
`nacos4k` 目前存在模块有：
- **nacos4k-api**: 注意为下述其余模块提供通用支持，例如部分实体类定义。
- **nacos4k-client**: ktor 客户端所使用的插件所在模块。
- **nacos4k-server**: ktor 服务端所使用的插件所在模块。


版本参考: [![](https://img.shields.io/maven-central/v/love.forte.nacos4k/nacos4k-api)](https://repo1.maven.org/maven2/love/forte/nacos4k/)

### ⚠ 版本说明
本库基于 **`ktor v2.0.0`** 开发，有可能不能用在 **`ktor v1.x`** 的版本中。


**Maven**
```xml
<!-- ktor客户端插件 -->
<dependency>
    <groupId>love.forte.nacos4k</groupId>
    <artifactId>nacos4k-client</artifactId>
    <version>${version}</version>
</dependency>

<!-- ktor服务端插件 -->
<dependency>
    <groupId>love.forte.nacos4k</groupId>
    <artifactId>nacos4k-server</artifactId>
    <version>${version}</version>
</dependency>
```

**Gradle Kotlin DSL**
```kotlin
// ktor客户端插件
implementation("love.forte.nacos4k:nacos4k-client:${version}")
// ktor服务端插件
implementation("love.forte.nacos4k:nacos4k-server:${version}")
```

**Gradle Groovy DSL**
```groovy
// ktor客户端插件
implementation 'love.forte.nacos4k:nacos4k-client:${version}'
// ktor服务端插件
implementation 'love.forte.nacos4k:nacos4k-server:${version}'
```

## 使用

⚠ 注: 目前实现的功能还很简易，只是根据 [nacos open-api](https://nacos.io/zh-cn/docs/open-api.html) 中的部分API进行实现并主要用于满足我个人使用场景。

目前实现的功能为最基础的单机nacos下的服务注册与服务发现。


当然，假如你希望能够协助我们一起使得它能够实现更多功能，非常欢迎并期待您的 [pr](https://github.com/ForteScarlet/nacos4k/pulls) !


### 客户端
```kotlin
val client = HttpClient {
    // ...
    
    // 由于需要请求nacos的Open API, 需要解析json数据，因此你需要确保安装了json插件。
    install(ContentNegotiation) {
        jackson {
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            // ... 
        }
    }
    
    // 安装 Nacos Client 插件。
    install(NacosClient)
    
    // ...
}
```

可以进行一定程度的配置：
```kotlin
val client = HttpClient {
    // ...
    
    install(NacosClient) {
        // nacos服务的地址
        serverAddress = "http://127.0.0.1:8848"
        // group name. 默认为null
        groupName = "..."
        // namespace id. 默认为null
        namespaceId = "..."
        // clusters. 默认为空
        clusters = emptyList()
        // 是否只获取healthy的实例。默认为false
        healthyOnly = true
        // 用于定期同步和获取实例的HttpClient实例。默认为null，为null的时候就是使用当前的client。
        syncClient = null
        // host选择器。即用来选择对应服务获取到的所有服务实例中选一个结果。默认为纯随机。
        // 是个 fun interface, 可以自己实现。
        hostSelector = NacosHostSelector.Random
    }
    
    // ...
}
```




### 服务端
```kotlin
embeddedServer(Netty, port = 8080) {
    // ...
    install(NacosServer) {
        // 当前服务的服务名，用于向nacos注册。这是必须的参数
        serviceName = "test"
        // nacos的服务器地址
        serverAddress = "http://localhost:8848"
    }
    // ...
}.start(wait = true)
```

可以进行一定程度的配置：
```kotlin
embeddedServer(Netty, port = 8080) {
    // ...
    install(NacosServer) {
        // 当前服务注册的服务名，必填。
        serviceName = "test"
        
        // 当前服务的IP。默认尝试自动获取。
        ip = null
        
        // 当前服务的端口。如果值小于等于0，则与当前的服务一致。默认为-1
        port = -1

        
        // 由于需要向nacos的open api发送注册、销毁、心跳的请求, 因此需要提供一个 HttpClient 实例用于发送请求。
        // 默认会通过 `HttpClient()` 构建一个新的实例。
        client = HttpClient()
        
        // group name. 默认为null
        groupName = null
        
        // cluster name. 默认为null
        clusterName = "..."
        
        // namespace id. 默认为null
        namespaceId = "..."
        
        // 大概是‘是否为临时实例’，默认为null。
        ephemeral = false

        // 发送心跳请求的周期。单位ms。默认为5000ms, 即5s
        beatInterval: Long = 5000L
    }
    // ...
}.start(wait = true)

```