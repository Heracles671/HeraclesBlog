1. 匿名函数和lambda表达式
```
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