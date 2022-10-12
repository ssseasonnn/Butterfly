![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly - 蝴蝶

Butterfly - 小巧而强大的武器，拥有它，让你的Android开发如虎添翼，Carry全场！

只有最强大和最经验的勇士才能挥动蝴蝶，但它在战斗中提供了令人难以置信的灵巧

> 物品介绍: +30 敏捷 +35% 闪避 +25 攻击 +30 攻速

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

### 特性

蝴蝶主要包含两大功能：

- Agile 页面导航
- Evade 组件化通信

### 集成

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

## Basic Usage

### 导航

Butterfly支持Activity、Fragment和DialogFragment的导航

```kotlin
@Agile("test/activity")
class AgileTestActivity : AppCompatActivity()

@Agile("test/fragment")
class TestFragment : Fragment()

@Agile("test/dialog")
class TestDialogFragment : DialogFragment()

//导航
Butterfly.agile("test/xxx").carry()

//导航并获取返回数据
Butterfly.agile("test/xxx")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

### 通信

Butterfly支持任意组件之间进行通信

在Module A中定义接口，添加Evade注解：

```kotlin
@Evade
interface Home {
    //定义方法
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

在Module B中定义实现，添加EvadeImpl注解：

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

> Module A和Module B之间没有依赖关系

即可通过Butterfly完成Module A和B之间的通信：

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

### 参数传递

可以通过以下两种方式在导航的过程中传递参数：

- 通过拼接scheme，将参数添加到scheme中
- 通过调用params方法手动传入参数，或者两者混合进行，随后可在导航后的页面中获取对应的参数

传递参数：

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

解析参数：

```kotlin
//在导航目的页面，可通过参数的key字段来获取传递的参数值
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by lazy { intent?.getStringExtra("a") ?: "" }
    val b by lazy { intent?.getStringExtra("b") ?: "" }
    val intValue by lazy { intent?.getIntExtra("intValue", 0) ?: 0 }
}
```

```kotlin
//除了手动解析参数以外，还可以装备Bracer来实现全自动进行参数解析
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by params<String>()
    val b by params<String>()
    val intValue by params<Int>()
}
```

> Bracer 使用方式详情见: Github 地址 [Bracer](https://github.com/ssseasonnn/Bracer)

### 拦截器

Butterfly支持全局拦截器和一次性拦截器

```kotlin
//自定义拦截器
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
```

配置全局拦截器：

```kotlin
//添加全局拦截器
ButterflyCore.addInterceptor(TestInterceptor())

//跳过所有全局拦截器
Butterfly.agile("test/scheme").skipGlobalInterceptor().carry()
```

配置一次性拦截器：

```kotlin
//仅当前导航使用该拦截器
Butterfly.agile(Schemes.SCHEME_AGILE_TEST)
    .addInterceptor(TestInterceptor())
    .carry()
```

### Action

Butterfly除了支持页面导航以外，还支持导航Action，Action没有页面，可进行某些逻辑处理

首先让自定义的Class继承Action，然后添加@Agile注解并设置scheme，其余和页面导航一致

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

> Action 不支持获取返回数据

### 使用Flow

除了直接调用**carry**完成导航以外，还可以调用**flow**或者**resultFlow**返回Flow

```kotlin
Butterfly.agile("test/scheme").flow()
    .onStart { println("start") }
    .onCompletion { println("complete") }
    .launchIn(lifecycleScope)

//or
Butterfly.agile("test/scheme").resultFlow()
    .onStart { println("start") }
    .onCompletion { println("complete") }
    .onEach { println("process result") }
    .launchIn(lifecycleScope)
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

## Extra Config

### Activity配置

```kotlin
Butterfly.agile("test/activity")
    .clearTop()                  //启动模式
    //or .singleTop()
    .addFlag(Intent.Flag_XXX)    //添加其他Flag
    .enterAnim(R.anim.xxx)       //添加动画
    .exitAnim(R.anim.xxx)
    .carry()
```

### Fragment配置

```kotlin
Butterfly.agile("test/fragment")
    .clearTop()                  //Fragment同样支持启动模式
    //or .singleTop()
    .disableBackStack()          //不添加到返回栈中
    .enterAnim(R.anim.xxx)       //添加动画
    .exitAnim(R.anim.xxx)
    .container(R.id.container)   //设置Fragment添加到的容器ID
    .tag("customTag")            //设置Fragment的tag
    .carry()
```

### DialogFragment配置

```kotlin
Butterfly.agile("test/dialog")
    .disableBackStack()         //不添加到返回栈中
    .tag("customTag")           //设置Dialog的tag
    .carry()
```

### Dialog和Fragment回退

Butterfly支持Fragment和DialogFragment的回退栈

在任意地点回退栈顶页面：

```kotlin
//回退栈顶页面, 并返回数据
Butterfly.retreat("result" to "123")

//回退栈顶Fragment, 并返回数据
Butterfly.retreatFragment("result" to "123")

//回退栈顶DialogFragment, 并返回数据
Butterfly.retreatDialog("result" to "123")
```

在页面内部回退自身：

```kotlin
//在Fragment内部回退
@Agile("test/fragment")
class TestFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            //回退当前Fragment并返回数据
            retreat("result" to "123")
        }
    }
}

//在DialogFragment内部回退
@Agile("test/dialog")
class TestDialogFragment : DialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            //回退当前DialogFragment并返回数据
            retreat("result" to "123")
        }
    }
}
```

## License

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
