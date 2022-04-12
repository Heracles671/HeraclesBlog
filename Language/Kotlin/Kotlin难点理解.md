# 属性
1. 平台类型

kotlin 法用其他平台代码类型，一般是类型后加感叹号，编译器会看成不可空类型，但这也增加了空指针异常的风险。可以通过注解减少平台类型的产生。
```kotlin
val btn = findViewById<Button>(R.id.btn)
```
2. @JvmField

生成公共的成员变量，而不会生成 set 和 get 方法

2. @JvmStatic

在伴生对象的方法上加上次注解可以变成真正的静态函数供在 Java 中调用，否则在 Java 中需要加上 Companion 对象来调用函数
```java
BaseApplication.Companion.currentApplication();
```
3. @file:JvmName("KotlinUtils")

可以加在顶层函数文件顶部，方便在 Java 中按照定义的文件名调用。类似的还有 `@get:JvmName("application")` 可以改写在 Java 中获取变量的调用函数名

4. 解构
```
val (code, status, message) = execute()
```
通过事先定义 component1()、component2()... 来获取

5. operator 关键字

通过 operator 关键定可以自定义操作符，比如可以使用[]来像数组一样获取集合中的元素

6. 抽象属性
```kotlin
interface BaseView<T> {
    val present: T
}

override val present: Present?
    get() = Present()

```
# 函数
1. 顶层函数

可以用作静态函数，在 kotlin 中直接导包，在 Java 中调用时添加文件名KT.的形式调用

2. infix

只能加在成员函数和扩展函数之前，并且有且仅有一个函数参数，目的是为了在调用函数的时候可以少写 . 和 ()

3. 可以通过给函数参数设置默认值的方式实现函数重载
```kotlin
fun test(name: String, password: String = "aa") {

}

test("jack")
test("jack", "12")
```
但是如果需要在 Java 中调用，需要在函数名上加上注解: `@JvmOverloads`

4. 匿名函数和lambda表达式
```kotlin
    val a = fun (param: Int) : String {
        return param.toString()
    }
    
    val b = { it: Int ->
        it.toString()
    }

    fun c(param: Int): String {
        return  param.toString()
    }
    
    val d = a
     
    val e = b
    
    val f = ::c
    
    fun main() {
        d.invoke(1)
        d(1)
        a.invoke(1)
        a(1)
        
        e.invoke(1)
        e(1)
        b.invoke(1)
        b(1)
        
        c(1)
        f.invoke(1)
        f(1)
    }
```
Kotlin 的匿名函数和 Lambda 表达式的本质：它们都是函数类型的对象，而不是函数！！

5. 扩展函数
```kotlin
fun Activity.log(text: String) {
    Log.e("Activity", text)
}

fun Context.log(text: String) {
    Log.e("Context", text)
}
```
如果在父子类上都加上相同名字的扩展函数，那么会根据调用对象的类型来确定函数调用，因为扩展函数在编译期就会确定调用关系。

6. 扩展属性
```
val ViewGroup.firstChild: View
    get() = getChildAt(0)
```
可以方便地为类添加成员属性

7. 属性委托
```kotlin
var token: String by Saver("token")

class Saver(var token: String) {
    operator fun getValue(nothing: Nothing?, property: KProperty<*>): String {
        CacheUtil.get(token)
    }

    operator fun setValue(nothing: Nothing?, property: KProperty<*>, value: String) {
        CacheUtil.save(token, value)
    }

}
```
通过 by 关键字来把相同的逻辑委托给同一个类对象来完成