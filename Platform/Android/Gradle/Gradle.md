# 基本语法
gradle 文件是用 groovy 语言编写的一种 DSL 规则，用来定义构建流程。它利用 groovy 的 Closure 类型传递方法，写出类似 JSON 的文件格式。所有的 key 都是一个方法，可以右键查看方法定义，如果没有定义对应的方法，那么就是走 methodMissing 方法。
# 文件作用
1. setting.gradle：用来添加需要打包的项目
2. build.gradle：用来构建项目
3. gradle.properties：gradle 构建过程中用的的一些属性
4. gradlew：gradle 构建脚本
5. gradle-wrapper.properties：主要用来定义 gradle 版本
# task
如果需要一个任务在运行时才执行，一定要把执行逻辑放在 `doLast` 或 `doFirst` 中：
```groovy
task bumpVersion(dependsOn: bump) {
    doLast {
        def versionPropsFile = file('version.properties')
        def versionProps = new Properties()
        versionProps.load(new FileInputStream(versionPropsFile))
        def codeBumped = versionProps['VERSION_CODE'].toInteger + 1
        versionProps['VERSION_CODE'] = codeBumped.toString()
        versionProps.store(versionPropsFile.newWriter(), null)
    }
}
```
# gradle 生命周期
1. 初始化阶段：setting.gradle
2. 配置阶段，画出有向无环图：在 gradle 中定义 afterEvaluate 方法
3. 执行阶段
# gradle Plugin
gradle 插件：`apply plugin:XXX`

本质：把逻辑独立的代码抽取和封装，其实就是一个继承 Plugin 接口的类