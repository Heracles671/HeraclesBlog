```
Java <-> React Native <-> JS
```
1. 原生代码调用 React Native 函数：事件
2. React Native 调用原生方法：原生模块 

参考：https://reactnative.cn/docs/next/render-pipeline
# 渲染流水线
1. 渲染阶段（Render）：构建 React 元素树和 React 影子树，大部分在 JS 线程完成
2. 提交阶段（Commit）：通过 yoga 计算每一个影子节点的位置和大小，并且进行树提升（New Tree -> Next Tree），都是在后台线程中执行，绝大部分布局计算发生在 C++ 中
3. 挂载阶段（Mount）：进行树对比、树拍平、树提升（Next Tree -> Rendered Tree），视图挂载（原子操作 curd），所有操作在 UI 线程执行
# 通信
## 参数传递
1. java -> js：通过 RCTRootView 读写 initialProperties 或 appProperties 来传递参数
2. js -> java：注册模块，通过函数调用直接传递参数
原生这边会把 ReadableMap/ReadableArray 类型参数转为 JSON 字符串进行传递
## 函数传递/promise函数
1. java -> js：
2. js -> java：通过 Callback/Promise 类型参数来传递回调函数
## 多线程（AsyncTask）
1. java -> js：
2. js -> java：在原生模块中使用通过 AysncTask 来处理耗时任务
## 事件发送
1. java -> js：原生通过 RCTDeviceEventEmitter 来向 RN 发送消息，RN 通过 NativeEventEmitter 模块来接收事件
2. js -> java： 
## Activity事件监听
1. java -> js：原生通过 ActivityEventListener/LifecycleEventListener 来监听页面返回、页面生命周期等事件
2. js -> java：