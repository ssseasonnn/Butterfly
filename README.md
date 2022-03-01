![](Butterfly.png)

[![](https://jitpack.io/v/ssseasonnn/Butterfly.svg)](https://jitpack.io/#ssseasonnn/Butterfly)

# Butterfly

*Read this in other languages: [中文](README.zh.md), [English](README.md), [Change Log](CHANGELOG.md)*

Only the mightiest and most experienced of warriors can wield the Butterfly, but it provides incredible dexterity in combat.

> Item introduction:
> +30 Agility
> +35% Evasion
> +25 Attack Damage
> +30 Attack Speed

## Prepare

Add dependency

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

#### Activity navigation

```kotlin
@Agile("test/scheme")
class AgileTestActivity : AppCompatActivity() {
    //...
}

//navigation
Butterfly.agile("test/scheme").carry()

//with result
Butterfly.agile("test/scheme")
    .carry {
        val result = it.getStringExtra("result")
        binding.tvResult.text = result
    }
```

#### Pass parameter

```kotlin
//scheme with query
Butterfly.agile("test/scheme?a=1&b=2").carry()

//use params 
Butterfly.agile("test/scheme")
    .params("intValue" to 1)
    .params("booleanValue" to true)
    .params("stringValue" to "test value")
    .carry()
```

#### Interceptor

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

//Add interceptor
ButterflyCore.addInterceptor(TestInterceptor())

//Skip interceptor
Butterfly.agile("test/scheme").skipInterceptor().carry()
```

#### Action

```kotlin
@Agile("test/action")
class TestAction : Action {
    override fun doAction(context: Context, scheme: String, data: Bundle) {
        Toast.makeText(context, "This is an Action", Toast.LENGTH_SHORT).show()
    }
}

//launch Action
Butterfly.agile("test/action").carry()

//Action also support pass param
Butterfly.agile("test/action?a=1&b=2").carry()

//params
Butterfly.agile("test/action")
    .params("intValue" to 1)
    .carry()
```

### Evade

#### Inter-module communication

When **module foo** needs to communicate with **module bar**, first define interfaces in **module foo**, then define implemented in **module bar**,
and add an annotation separately, And, **module bar** **does not need to rely on** **module foo**.

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

//call

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
> 