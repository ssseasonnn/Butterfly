![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly

Butterfly - Small and powerful weapons, own it, let your Android are developed like Tiger, Carry whole game!

Only the mightiest and most experienced of warriors can wield the Butterfly, but it provides incredible dexterity in combat.

> Item introduction: +30 Agility +35% Evasion +25 Attack Damage +30 Attack Speed

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

### Feature

The butterfly mainly contains two major features：

- Agile page navigation
- Evade module communicate

### Setup

```gradle
repositories {
  maven { url 'https://jitpack.io' }
}
```

```gradle
apply plugin: 'kotlin-kapt'

dependencies {
  implementation 'com.github.ssseasonnn.Butterfly:butterfly:1.1.1'
  kapt 'com.github.ssseasonnn.Butterfly:compiler:1.1.1'
}
```

## Basic Usage

### Navigation

Butterfly supports navigation for Activity、Fragment and DialogFragment

```kotlin
@Agile("test/activity")
class AgileTestActivity : AppCompatActivity()

@Agile("test/fragment")
class TestFragment : Fragment()

@Agile("test/dialog")
class TestDialogFragment : DialogFragment()

//Navigation
Butterfly.agile("test/xxx").carry()

//Navigation and get result
Butterfly.agile("test/xxx")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

### Communication

Butterfly supports communication between any component

Define the interface in Module A and add Evade annotation:

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

Define the implementation in Module B, and add the EvadeImpl annotation:

```kotlin
//The implementation class name must end with Impl
@EvadeImpl
class HomeImpl {
    val TAG = "home_tag"

    //Implement the method in the Home interface. 
    //The method name and method parameters must be the same
    fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

> There is no dependency between Module A and Module B

The communication between Module A and B can be completed by Butterfly:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

### Passing parameters

You can pass parameters during navigation in two ways:

- Add parameters to scheme by splicing scheme
- Manually pass in the parameters by calling the params method, or a combination of the two, and then obtain the corresponding parameters in the
  navigated page

Passing parameters:

```kotlin
//Splicing scheme
Butterfly.agile("test/scheme?a=1&b=2").carry()

//Call params
Butterfly.agile("test/scheme?a=1&b=2")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

Parses parameters：

```kotlin
//On the navigation target page, you can obtain the passed parameter value through the key field of the parameter
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by lazy { intent?.getStringExtra("a") ?: "" }
    val b by lazy { intent?.getStringExtra("b") ?: "" }
    val intValue by lazy { intent?.getIntExtra("intValue", 0) ?: 0 }
}
```

```kotlin
//In addition to manual parameter analysis, Bracer can also be equipped to realize fully automatic parameter analysis
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    val a by params<String>()
    val b by params<String>()
    val intValue by params<Int>()
}
```

> See Github address for details of Bracer usage: [Bracer](https://github.com/ssseasonnn/Bracer)

### Interceptor

Butterfly supports global interceptors and one-time interceptors

```kotlin
//Custom Interceptor
class TestInterceptor : ButterflyInterceptor {
    override fun shouldIntercept(agileRequest: AgileRequest): Boolean {
        //Detect whether interception is required
        return true
    }

    override suspend fun intercept(agileRequest: AgileRequest) {
        //Processing interception logic
        println("intercepting")
        delay(5000)
        println("intercept finish")
    }
}
```

Configure Global Interceptors：

```kotlin
//Add Global Interceptor
ButterflyCore.addInterceptor(TestInterceptor())

//Skip all global interceptors
Butterfly.agile("test/scheme").skipGlobalInterceptor().carry()
```

Configure one-time interceptors：

```kotlin
//Only the current navigation uses this interceptor
Butterfly.agile(Schemes.SCHEME_AGILE_TEST)
    .addInterceptor(TestInterceptor())
    .carry()
```

### Action

Butterfly supports not only page navigation, but also navigation actions. Actions have no pages and can be processed logically

First let the customized class inherit Action, then add Agile annotation and set scheme. The rest are consistent with page navigation

```kotlin
@Agile("test/action")
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}

//Start Action
Butterfly.agile("test/action").carry()

//Action also support params
Butterfly.agile("test/action?a=1&b=2").carry()

Butterfly.agile("test/action")
    .params("intValue" to 1)
    .carry()
```

> Action does not support obtaining returned data

### Using Flow

In addition to directly calling **carry** to complete navigation, you can also call **flow** or **resultFlow** to return to Flow

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

### Router Table

Butterfly will generate a routing table for each annotated module. The naming rule is: Butterfly[module name]Module

Manual registration:

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ButterflyCore.addModule(ButterflyHomeModule())
        ButterflyCore.addModule(ButterflyFooModule())
        ButterflyCore.addModule(ButterflyBarModule())
    }
}
```

Automatic registration with plugin:

1. Add plug-in dependency

```groovy
//使用 plugins DSL:
plugins {
    id "io.github.ssseasonnn.butterfly" version "1.0.1"
}

//Or use the legacy plugin application:
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

//Add plugin
apply plugin: "io.github.ssseasonnn.butterfly"
```

2. Implement your own Application class

```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
```

## Extra Config

### Activity Configuration

```kotlin
Butterfly.agile("test/activity")
    .clearTop()                  //Launch mode
    //or .singleTop()
    .addFlag(Intent.Flag_XXX)    //Add other Flag
    .enterAnim(R.anim.xxx)       //Add anim
    .exitAnim(R.anim.xxx)
    .carry()
```

### Fragment Configuration

```kotlin
Butterfly.agile("test/fragment")
    .clearTop()                  //Fragment also supports launch mode
    //or .singleTop()
    .disableBackStack()          //Do not add to the backstack
    .enterAnim(R.anim.xxx)       //Add anim
    .exitAnim(R.anim.xxx)
    .container(R.id.container)   //Set the container ID to which Fragment is added
    .tag("customTag")            //Set Fragment tag
    .carry()
```

### DialogFragment Configuration

```kotlin
Butterfly.agile("test/dialog")
    .disableBackStack()         //Do not add to the backstack
    .tag("customTag")           //Set tag
    .carry()
```

### Dialog and Fragment retreat

Butterfly supports the backstack of Fragment and DialogFragment

Back the top page anywhere:

```kotlin
Butterfly.retreat("result" to "123")

Butterfly.retreatFragment("result" to "123")

Butterfly.retreatDialog("result" to "123")
```

Back itself inside the page:

```kotlin
@Agile("test/fragment")
class TestFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
            retreat("result" to "123")
        }
    }
}

@Agile("test/dialog")
class TestDialogFragment : DialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnBack.setOnClickListener {
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
