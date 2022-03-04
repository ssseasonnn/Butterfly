![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly - 蝴蝶

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

Only the mightiest and most experienced of warriors can wield the Butterfly, but it provides incredible dexterity in combat.

只有最强大和最经验的勇士才能挥动蝴蝶，但它在战斗中提供了令人难以置信的灵巧


> 物品介绍:
> +30 敏捷
> +35% 闪避
> +25 攻击
> +30 攻速

## Usage

Butterfly - 小巧而强大的武器，拥有它，让你的Android开发如虎添翼，Carry全场！

蝴蝶通过两个不同的注解来实现不同的功能：

- Agile 用于页面导航
- Evade 用于组件化通信

### 页面导航 - Agile

通过给Activity添加Agile注解，并设置对应的scheme，随后即可通过Butterfly进行导航，或者导航并获取返回数据。

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

Agile支持附带参数导航，有两种方式，一是通过拼接scheme，将参数添加到scheme中，二是通过调用params方法手动传入参数，或者两者混合进行， 随后可在导航后的页面中获取对应的参数。

```kotlin
//拼接scheme
Butterfly.agile("test/scheme?a=1&b=2").carry()

//调用params
Butterfly.agile("test/scheme")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

Agile支持拦截器，可用于在导航前预处理部分逻辑，如进行登录检测，此外拦截器中也可进行导航，但为了避免拦截器套娃，需要添加skipInterceptor()方法以忽略拦截器。

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

Agile除了支持页面导航以外，还支持导航Action，Action无页面，可进行某些逻辑处理。 首先让自定义的Class继承Action，然后添加@Agile注解并设置scheme，其余和页面导航一致。

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

### 组件化通信 - Evade

组件化通信支持两种模式

#### 方式一，通过下沉依赖，在公共模块中定义接口，然后在被调用模块中实现接口

例如以下三个组件：公共组件Base，调用者Foo，被调用者Bar

在Base组件中，定义接口:

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

在组件Bar中，实现接口:

```kotlin
@EvadeImpl
class HomeImpl : Home {
    val TAG = "home_tag"

    fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

在组件Foo中，使用evade方法获取Home并调用:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)

```

#### 方式二，无需下沉依赖，组件Foo和组件Bar之间无需依赖直接进行通信

在Foo组件中，定义接口:

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

在Bar组件中，定义实现:

```kotlin
@EvadeImpl
class HomeImpl {
    val TAG = "home_tag"

    fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

在Foo组件中，使用evade方法获取Home并调用:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)

```

## 依赖

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

## 混淆

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
