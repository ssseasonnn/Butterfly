![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly

Butterfly - Small and powerful weapons, own it, let your Android are developed like Tiger, Carry whole game!

Only the mightiest and most experienced of warriors can wield the Butterfly, but it provides incredible dexterity in combat.

> Item introduction: +30 Agility +35% Evasion +25 Attack Damage +30 Attack Speed

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

## Usage

#### import

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

Butterfly achieve different functions through two different annotations：

- Agile Used for page navigation
- Evade Used for component communication


#### Agile

- Page navigation

By adding an Agile annotation to Activity, set the corresponding scheme, 
then you can navigate through butterfly, or navigate and get the return data.

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    //...
}

//navigation
Butterfly.agile("test/scheme").carry()

//Navigate and get the return data
Butterfly.agile("test/scheme")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

- Pass parameter

Agile supports a parameter navigation, there are two ways, 
one is to add parameters to scheme to the Scheme by calling the parameter method, 
or the mixing of the two, then the page after navigationGet the corresponding parameters

```kotlin
//add parameters to scheme
Butterfly.agile("test/scheme?a=1&b=2").carry()

//call params method
Butterfly.agile("test/scheme?a=1&b=2")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

- Parse parameters

On the navigation purpose page, you can get the passed parameter value by using the key field of the parameter

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
	val a by lazy { intent?.getStringExtra("a") ?: "" }
	val b by lazy { intent?.getStringExtra("b") ?: "" }
    val intValue by lazy { intent?.getIntExtra("intValue", 0) ?: 0 }
}
```

In addition to manual parameter parsing, Bracer can also be equipped for fully automated parameter parsing

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
	val a by params<String>()
	val b by params<String>()
	val intValue by params<Int>()
}
```
> See Details on how Bracer is used: Github [Bracer](https://github.com/ssseasonnn/Bracer)

- Interceptors

Agile supports interceptors that can be used to preprocess parts of logic, such as login detection, before navigation
Navigation is also possible in the interceptor, but to avoid the interceptor nesting doll, 
the skipInterceptor() method needs to be added to ignore the interceptor

```kotlin
//Implement a custom interceptor
class TestInterceptor : ButterflyInterceptor {
    override fun shouldIntercept(agileRequest: AgileRequest): Boolean {
        //Detects whether interception is required
        return true
    }

    override suspend fun intercept(agileRequest: AgileRequest) {
        //Handles the interception logic
        println("intercepting")
        delay(5000)
        println("intercept finish")
    }
}

//Register the interceptor
ButterflyCore.addInterceptor(TestInterceptor())

//Skip the interceptor
Butterfly.agile("test/scheme").skipInterceptor().carry()
```

- Action

In addition to supporting page navigation, Agile also supports navigation Action, action has no pages, 
and can do some logic processing
First let the custom Class inherit the Action, then add @Agile annotations and set the scheme, 
and the rest is consistent with the page navigation

```kotlin
@Agile("test/action")
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        //The parameters passed in can be obtained from the data
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}

//Start Action
Butterfly.agile("test/action").carry()

//Action also supports pass parameters
Butterfly.agile("test/action?a=1&b=2").carry()

//params
Butterfly.agile("test/action")
    .params("intValue" to 1)
    .carry()
```

- Process control

In addition to directly calling carry navigation, Agile can also call flow to return the Flow object, 
which can be used to process the navigation flow

```kotlin
Butterfly.agile("test/scheme").flow()
		.onStart { println("start") }
		.onCompletion { println("complete") }
		.onEach { println("process result") }
		.launchIn(lifecycleScope)
```


#### Evade

Butterfly can communicate between any component using a simple two annotations without 
any direct or indirect dependency between the components

For example, there are two components: Module Foo and Module Bar that require communication

In Module Foo, define the interface and add the Evade annotation:

```kotlin
@Evade
interface Home {
	//Define the method
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

In module Bar, define the implementation, and add the EvadeImpl annotation:

```kotlin
//The implementation class name must end in Impl
@EvadeImpl
class HomeImpl {
    val TAG = "home_tag"

	//To implement a method in the Home interface, the method name and method parameters must be the same
    fun showHome(fragmentManager: FragmentManager, container: Int) {
        val homeFragment = HomeFragment()
        fragmentManager.beginTransaction()
            .replace(container, homeFragment, TAG)
            .commit()
    }
}
```

> Since Evade uses the class name as an important basis for defining and implementing associations, 
> the interface class name and the implementation class name must be the same, and the implementation class name ends in Impl.
If you cannot use a class name as an association, you can also use the same string type as the association key
>```kotlin
>@Evade(identity = "same key")
>interface Home
>
>@EvadeImpl(identity = "same key")
>class OtherNameImpl
>```

Then in Module Foo, you can use the evaluate method to get home and call:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

In addition, Evade also supports strong association type communication in the form of sinking dependencies

For example, the following three components: the common component Module Base, Module Foo, and Module Bar

First sink the Home interface into the common component Module Base:

```kotlin
@Evade
interface Home {
    fun showHome(fragmentManager: FragmentManager, container: Int)
}
```

Then in the Module Bar, implement the interface:

```kotlin
//The same naming convention needs to be used, and the implementation class name must end in Impl
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

Then in Module Foo, you can use the evaluate method to get home and call:

```kotlin
val home = Butterfly.evade<Home>()
home.showHome(supportFragmentManager, R.id.container)
```

#### Routing table

Butterfly generates a route table for each Module that uses the annotation, and the naming convention is: Butterfly[module name]Module

Manual registration:
```kotlin
class DemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //register
        ButterflyCore.addModule(ButterflyHomeModule())
        ButterflyCore.addModule(ButterflyFooModule())
        ButterflyCore.addModule(ButterflyBarModule())
    }
}
```

To register automatically with a plugin:
1. Add plugin dependencies

```groovy
//using plugins DSL:
plugins {
    id "io.github.ssseasonnn.butterfly" version "1.0.1"
}

//or useing legacy plugin application:
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

//add plugin
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

#### Proguard config

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
