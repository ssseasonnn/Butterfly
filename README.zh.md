![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly - 蝴蝶

Butterfly - 小巧而强大的武器，拥有它，让你的Android开发如虎添翼，Carry全场！

只有最强大和最经验的勇士才能挥动蝴蝶，但它在战斗中提供了令人难以置信的灵巧

> 物品介绍: +30 敏捷 +35% 闪避 +25 攻击 +30 攻速

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

## Usage

### 特性

蝴蝶主要包含两大功能：

- Agile 页面导航
- Evade 组件化通信

### 依赖

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```

```gradle
apply plugin: 'kotlin-kapt'

dependencies {
  implementation 'com.github.ssseasonnn.Butterfly:butterfly:1.0.0'
  kapt 'com.github.ssseasonnn.Butterfly:compiler:1.0.0'
}
```

### Agile

#### 1.导航

通过给Activity添加Agile注解，并设置对应的scheme，随后即可通过Butterfly进行导航，或者导航并获取返回数据

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    //...
}

//导航
Butterfly.agile("test/scheme").carry()

//导航并获取返回数据
Butterfly.agile("test/scheme")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

#### 2.传递参数

Agile支持附带参数导航，有两种方式，一种是通过拼接scheme，将参数添加到scheme中 另一种是通过调用params方法手动传入参数，或者两者混合进行， 随后可在导航后的页面中获取对应的参数

```kotlin
//拼接scheme
Butterfly.agile("test/scheme?a=1&b=2").carry()

//调用params
Butterfly.agile("test/scheme?a=1&b=2")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

#### 3.解析参数

在导航目的页面，可通过参数的key字段来获取传递的参数值

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by lazy { intent?.getStringExtra("a") ?: "" }
    val b by lazy { intent?.getStringExtra("b") ?: "" }
    val intValue by lazy { intent?.getIntExtra("intValue", 0) ?: 0 }
}
```

除了手动解析参数以外，还可以装备Bracer来实现全自动进行参数解析

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by params<String>()
    val b by params<String>()
    val intValue by params<Int>()
}
```

> Bracer 使用方式详情见: Github 地址 [Bracer](https://github.com/ssseasonnn/Bracer)

#### 4.拦截器

Agile支持拦截器，可用于在导航前预处理部分逻辑，如进行登录检测 此外拦截器中也可进行导航，但为了避免拦截器套娃，需要添加skipInterceptor()方法以忽略拦截器

```kotlin
//实现自定义拦截器
class TestInterceptor : ButterflyInterceptor {
    override fun shouldIntercept(agileRequest: AgileRequest): Boolean {
        //检测是否需要拦截
        return true
    }

    override suspend fun intercept(agileRequest: AgileRequest) {
        //处理拦截逻辑
        println("intercepting")
        delay(5000)
        println("intercept finish")
    }
}

//注册拦截器
ButterflyCore.addInterceptor(TestInterceptor())

//跳过拦截器
Butterfly.agile("test/scheme").skipInterceptor().carry()
```

#### 5.Action

Agile除了支持页面导航以外，还支持导航Action，Action无页面，可进行某些逻辑处理 首先让自定义的Class继承Action，然后添加@Agile注解并设置scheme，其余和页面导航一致

```kotlin
@Agile("test/action")
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        //可从data中获取传入的参数
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}

//启动Action
Butterfly.agile("test/action").carry()

//Action同样支持传参
Butterfly.agile("test/action?a=1&b=2").carry()

//params 传参
Butterfly.agile("test/action")
    .params("intValue" to 1)
    .carry()
```

#### 6.流程控制

Agile除了直接调用carry导航以外，还可以调用flow返回Flow对象， 利用Flow对象可对导航流程进行处理

```kotlin
Butterfly.agile("test/scheme").flow()
    .onStart { println("start") }
    .onCompletion { println("complete") }
    .onEach { println("process result") }
    .launchIn(lifecycleScope)
```

### Evade

蝴蝶使用简单的两个注解即可实现任意组件之间进行通信，而组件之间无需任何直接或间接依赖

例如有两个组件：Module Foo 和Module Bar 需要通信

在Module Foo中，定义接口，并添加Evade注解：

```kotlin
@Evade
interface Home {
    //定义方法
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

在Module Bar中，定义实现,，并添加EvadeImpl注解：

```kotlin
//实现类名必须以Impl结尾
@EvadeImpl
class HomeImpl {
    val TAG = "home_tag"

    //实现Home接口中的方法, 方法名和方法参数必须相同
    fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

> 由于Evade使用类名称作为定义和实现关联的重要依据，因此接口类名和实现类名必须相同，并且实现类名以Impl结尾． 如无法以类名作为关联，也可使用相同的字符串类型作为关联key
>```kotlin
>@Evade(identity = "same key")
>interface Home
>
>@EvadeImpl(identity = "same key")
>class OtherNameImpl
>```

随后即可在Module Foo中，使用evade方法获取Home并调用:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

除此之外, Evade也支持通过下沉依赖的形式, 进行强关联类型的通信

例如以下三个组件：公共组件Module Base，Module Foo，Module Bar

首先将Home接口下沉至公共组件Module Base中:

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

然后在Module Bar中，实现接口:

```kotlin
//同样需要使用相同的命名规则, 实现类名必须以Impl结尾
@EvadeImpl
class HomeImpl : Home {
    val TAG = "home_tag"

    override fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

之后便可在Module Foo中，使用evade方法获取Home并调用:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

### 路由表

Butterfly会为每个使用了注解的Module生成一个路由表, 命名规则为: Butterfly[模块名称]Module

手动注册:

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //注册
        ButterflyCore.addModule(ButterflyHomeModule())
        ButterflyCore.addModule(ButterflyFooModule())
        ButterflyCore.addModule(ButterflyBarModule())
    }
}
```

使用插件自动注册:

1. 添加插件依赖

```groovy
//使用 plugins DSL:
plugins {
    id "io.github.ssseasonnn.butterfly" version "1.0.1"
}

//或者使用legacy plugin application:
buildscript {
    repositories {
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath "io.github.ssseasonnn:plugin:1.0.1"
    }
}

//添加plugin
apply plugin: "io.github.ssseasonnn.butterfly"
```

2. 实现自己的Application类

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
```

### 混淆配置

```pro
-keep public class zlc.season.butterfly.module.**
-keep public class zlc.season.butterfly.annotation.**
-keep public class zlc.season.butterfly.ButterflyCore {*;}
-keep public class * extends zlc.season.butterfly.Action

-keep @zlc.season.butterfly.annotation.Agile class * {*;}
-keep @zlc.season.butterfly.annotation.Evade class * {*;}
-keep @zlc.season.butterfly.annotation.EvadeImpl class * {*;}
```

### License

> ```
> Copyright 2022 Season.Zlc
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>    http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.
> ```
