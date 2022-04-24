# object 单例
```kotlin
object Singleton {
    
}
```
可以看到，kotlin 的单例写法非常简单，那它到底是如何实现的呢？线程安全吗？
# 反编译成 Java 代码：
```java
public final class Singleton {
   @NotNull
   public static final Singleton INSTANCE;

   private Singleton() {
   }

   static {
      Singleton var0 = new Singleton();
      INSTANCE = var0;
   }
}
```
可以看到，这里采用的是饿汉式单例写法，利用 JVM 在进行类初始化时的锁机制来实现线程安全。我们比较常用的是静态内部类的懒汉式写法。[Java 常见几种单例写法](https://juejin.cn/post/6844903858276139021)

那么啥叫*利用 JVM 在进行类初始化时的锁机制来实现线程安全*?
# JVM 初始化类的经过
[JVM 类加载机制](https://www.cnblogs.com/chanshuyi/p/jvm_serial_07_jvm_class_loader_mechanism.html)

[类初始化加锁](https://tuonioooo-notebook.gitbook.io/java-concurrent/di-san-zhang-java-nei-cun-mo-xing/shuang-zhong-jian-cha-suo-ding-yu-yan-chi-chu-shi-hua/ji-yu-lei-chu-shi-hua-de-jie-jue-fang-an)

到这里应该可以比较好的理解 kotlin object 单例模式了。但事情还没完，关于 Java 单例中的双重检查模式为啥这样写呢？volatile、synchronize 关键字的作用是什么？
# Java 多线程的同步机制
todo