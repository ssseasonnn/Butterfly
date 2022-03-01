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

## Prepare

添加依赖

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

## Usage

### Agile

#### 页面跳转

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    //...
}

//跳转
Butterfly.agile("test/scheme").carry()

//需要返回结果
Butterfly.agile("test/scheme")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

#### 传参

```kotlin
//scheme 传参
Butterfly.agile("test/scheme?a=1&b=2").carry()

//params 传参
Butterfly.agile("test/scheme")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

#### 拦截器

```kotlin
class TestInterceptor : ButterflyInterceptor {
    override fun shouldIntercept(agileRequest: AgileRequest): Boolean {
        return true
    }
    override suspend fun intercept(agileRequest: AgileRequest) {
        println("intercepting")
        delay(5000)
        println("intercept finish")
    }
}

//注册
ButterflyCore.addInterceptor(TestInterceptor())

//跳过拦截器
Butterfly.agile("test/scheme").skipInterceptor().carry()
```

- Action

```kotlin
@Agile("test/action")
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
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

### Evade

#### 模块间通信

当**模块foo**需要和**模块bar**通信时，首先在**模块foo**中定义接口，然后在**模块bar**中定义实现，同时分别添加上注解即可， 并且，**模块bar** **不需要依赖** **模块foo**。

module foo

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

module bar

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

//调用

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
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
