# Android 生命周期和启动模式
## 生命周期
### 正常情况
流程：
```
onCreate -> onStart -> onResume -> onPause -> onStop -> onDestroy
    |          |          |<----------|          |           |
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
## IntentFilter 匹配