# Android 生命周期和启动模式
## 生命周期
### 正常情况
流程：
```
onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy
    |          |         |<-onNewIntent-|        |           |
    |          |<-----------onRestart------------|           |
    |<-------------------------------------------------------|
```
页面跳转：
```
MainActivity -> onPause
SecindActivity -> onCreate
SecindActivity -> onStart
SecindActivity -> onResume
MainActivity -> onStop
```
### 意外情况
旋转屏幕重建：
```
     Activity                            Activity
        |                                    | 
     意外情况                                 ｜
        |                                    |
onSaveInstanceState     --重新创建-->      onCreate
        |                                    |
     onDestroy                       onRestoreInstanceState

onSaveInstanceState调用时机在onStop之前，onRestoreInstanceState调用时机在onStart之后

MainActivity -> onPause
MainActivity -> onSaveInstanceState
MainActivity -> onStop
MainActivity -> onDestroy
MainActivity -> onCreate
MainActivity -> onRestoreInstanceState
```
内存不足销毁：流程和上一种情况一致
## 启动模式
1. standard：每次调用都会实例化，生命周期走正常流程
2. singleTop：位于栈顶就复用，否则重新创建
3. singleTask：位于栈内就复用，同时清除之上的活动，否则就新建任务栈并创建活动
4. singleInstance：加强版的singleTask模式，并且只能单独位于一个任务栈中
## Activity的标识位
1. 设定activity启动模式
```
FLAG_ACTIVITY_NEW_TASK：类似于singleTask模式
FLAG_ACTIVITY_SINGLE_TOP：等同于singleTop模式
```
2. 影响activity运行状态
```
FLAG_ACTIVITY_CLAER_TOP：
清除之上活动，和FLAG_ACTIVITY_NEW_TASK配合等同于singleTask模式
如果被启动活动是standard模式，则会连同自己一起被清除后重新创建并置于栈顶

FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS：
在任务历史列表中去除，类似于android:excludeFromRecents="true"属性
```
## IntentFilter 匹配
1. 隐式匹配指标：action、category、data
2. 必须满足所有指标才算匹配成功
3. 一个activity可以有多个intent-filter，只要匹配上一个就可以启动
4. 可以用intent的resolveActivity函数事先检查是否存在对应界面，第二个参数传递MATCH_DEFAULT_ONLY类型
### action
action是一个字符串，要求intent中的action存在且必须和过滤规则中的其中一个action相同；另外，action区分大小写
### category
category是一个字符串，可以没有（系统会自动为intent添加Default类型的），如果有则必须是intent-filter中category规则集中的定义的。所以为了activity能够被隐式调用，intent-filter中必须添加default类型的category

ps：由此可见，launcher启动应用采用的是隐式启动方案
### data
如果匹配规则中出现了，则要求intent中一定要定义，它由两部分构成：URI + mimeType
#### mimeType
指代媒体类型：image/jpeg、audio/mpeg4-generic、video/*、text/plain。此属性在intent中必须配合uri才能使用
#### URI
```
<scheme>://<host>:<port>/[<path>|<pathPrefix>|<pathPattern>]
```
类似category，URI有默认值：content和file