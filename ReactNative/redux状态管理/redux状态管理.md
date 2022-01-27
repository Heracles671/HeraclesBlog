# redux 状态管理
来源：https://gauliang.github.io/blog/2020/dive-react-redux/#redux-toolkit

要谈论 react-redux 得先从了解 flux 和 redux 开始，flux 本身是一种架构模式，redux 是一个实现了 Flux 架构模式的面向 JS 的通用状态管理器， react-redux 提供了一种机制使得我们能够更方便的在 react 中使用 Redux
## Flux 模型
Flux 是 Facebook 用于构建客户端 Web 应用程序的应用程序体系结构，它更像是一种模式，而不是正式的框架。Flux 模型包含三个主要部分：Dispatcher，Store 和 View(React组件)。
![flux](./flux-simple.png)
Flux 应用程序的数据沿单一方向流动，上图中 Dispatcher、Store、View 是具有独立输入输出的节点，Action 是一个简单对象，包含新数据和标识 Action 类型的 type 属性。 用户与 View 的交互会在系统中产生 Action 传播。

1. 所有数据都流经 Dispatcher，提供给 Dispatcher 的 Action 是通过 Action creator 方法创建的对象，这通常来自用户与视图的交互。
2. Dispatcher 通过调用注册在 Store 中的回调函数完成 Action 到 Store 的分发。在已注册的回调中，Store 将响应与其所维护状态相关的具体操作。
3. Store 触发 change 事件，将数据层的变化通知到 Controller-views。
4. Controller-views 监听这些事件，并从 event handler 中读取来自 Stores 的数据。
5. Controller-views 调用他们自己的 setState() 方法，从而在组件树中更新自身及其所有后代。
## Redux 模型
由于 Flux 本身并不是框架，因此开发人员已尝试提出 Flux 模式的许多实现。最终，一个明显的赢家出现了，那就是 Redux，其已成为 React 生态中开发人员集中管理应用状态的事实标准库。

Redux 是一个面向 JS 应用程序的可预测状态管理器，它使用基于 Action 的事件模型来管理和更新应用程序状态，从而现集中化管理应用程序状态和逻辑。 作为一个通用解决方案，Redux 可与任何 UI 层一起使用，如 React、Angular、Vue 等。虽然 Redux 是 Flux 架构的实现，但也存在一些细微差异。
### Actions
这与 Flux 中的定义相同，Action 是一个 JS 简单对象，包含 type 字段及一些可选的载荷信息。可以把 Action 理解为描述应用中事件的描述。
```
const addTodoAction = {
  type: 'todos/todoAdded',
  payload: 'Buy milk'
}
```
### Action Creators
Action Creator 是一个创建并返回 Action 对象的方法，通过它可以避免在每次使用 Action 时书写重复的代码。
```
const addTodo = text => {
  return {
    type: 'todos/todoAdded',
    payload: text
  }
}
```
### Reduces
Reducer 是一个函数，它接收两个参数，分别为当前 state 和 action 对象，Reducer 内部决定如何更新 state，并返回一个新的 state。可以描述为 (state, action) => newState。 可以把 Reducer 视为事件侦听器，该事件侦听器根据接收到的 action(event) 来处理事件。Reducer 必须遵循下列规则：

1. 通过 state 和 action 参数计算并返回 全新的 state
2. 不能修改已经存在的 state，内部可以通过拷贝已经存在的 state 进行相关操作
3. 不能具有副作用(side effects)。如果用同样的参数两次调用必须返回可预期的结果，内部不能出现如异步或 random 之类的操作
```
const initialState = { value: 0 }

function counterReducer(state = initialState, action) {
  if (action.type === 'counter/increment') {
    return {
      ...state,
      value: state.value + 1
    }
  }
  return state
}
```
### Store
Redux 应用的当前 state 保存在一个称为 Store 的对象上，Store 对象通过传入 Reducer 创建，并对外提供 getState 方法，该方法返回当前 state 信息。
```
const store = createStore(counterReducer)
console.log(store.getState())   // {value: 0}
```
### Dispatch
Redux Store 对象还拥有一个名为 dispatch 的方法，更新 state 的唯一方法就是传递一个 action 对象给该方法。Store 内部会执行 reducer 方法，并保存一份新数据。 当我们再次调用 getState() 将会返回更新过的 state。
```
store.dispatch({ type: 'counter/increment' })
console.log(store.getState())   // {value: 1}
```
您可以将 Dispatch 视为 触发事件，通过 Dispatch 通知 Store 应用中存在 Action 需要处理，Reducer 的行为就像事件侦听器，当他们接收到自己感兴趣的 Action 时，会响应事件并更新状态。
### Selector
Selector 是可以从 Store State 中提取特定信息的函数。当应用程序变得庞大时，它将变得很有用。
## Redux 数据流
### 初始化
1. 使用 root reducer 创建 Redux store;
2. Store 调用 reducer 并保存其初始值；
3. 当 View 第一次被渲染，会从 Redux store 中访问当前 state 并根据 state 渲染视图。View 也可以订阅 Store 的变更消息，这样在未来当 state 再次变化，视图就能够发现并处理它。
### 更新数据
1. 当一些事情在应用中发生，如用户点击了某个按钮，会创建 Action；
2. 分发 Action 到 Redux Store，代码类似 dispatch({type:"counter/increment"});
3. Store 再次执行 reducer 函数，通过之前的 state 和 action 完成计算、存储并返回新的 state；
4. Store 通知所有订阅了 Store 数据变更消息的 View 组件；
5. 每个依赖 state 数据的 View 组件都会对 state 数据进行检查，以确定是否需要重新渲染自己；
6. 需要重新渲染的组件使用新数据完成渲染。
![redux](./ReduxDataFlowDiagram.gif)
## React-redux
Redux 本身是一个独立的库，可与任何 UI 层或框架一起使用，通常需要使用 UI 绑定库 将 Redux 与 UI 框架绑定在一起，而不是通过 UI 代码直接与 Store 进行交互。 React Redux 是 React 的官方 Redux UI 绑定库，它使得 React 组件可以从 Redux store 读取数据或分发 action 到 Store 以更新 state。
### Provider 
React Redux 提供了一个 <Provider /> 组件，将 store 对象以 props 形式注入到组件中，可以使其所有子组件中均可访问到 store 对象。
```
import React from 'react'
import ReactDOM from 'react-dom'

import { Provider } from 'react-redux'
import store from './store'

import App from './App'

const rootElement = document.getElementById('root')
ReactDOM.render(
  <Provider store={store}>
    <App />
  </Provider>,
  rootElement
)
```
### Connect
React Redux 通过名为 connect 的函数将组件和 store 对象进行链接，随后在组件内部能以 props 的形式访问 dispatch 和 state。
```
import { connect } from 'react-redux'
import { increment, decrement, reset } from './actionCreators'

const mapStateToProps = (state /*, ownProps*/) => {
  return {
    counter: state.counter
  }
}

const mapDispatchToProps = { increment, decrement, reset }

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Counter)
```
## Redux Toolkit
Redux Toolkit 通过遵循 Redux 最佳实践，提供了良好的默认行为，捕获错误并允许您编写更简单的代码，使编写良好的 Redux 应用程序和加快开发速度变得更加容易。

Redux Toolkit 提供了一系列实用功能，包括：

1. configureStore() —— 简化创建 Store 配置选项。它可以自动组合 Reducer slice，并加载常用中间件，如 redux-thunk，并默认启用哪个 Redux DevTools Extension；
2. createReducer() —— 内部自动使用 immer 简化了数据更新；
3. createAction() —— 为给定动作类型字符串创建 action creator 函数。
4. createSlice —— 接受一组配置信息，如 name、initialState。自动生成 action coretor；
5. createSelector —— 更加便捷的从 state 中导出需要的数据

Redux Toolkit 不仅简化了很多操作流程，还提供了一系列 Hook，使得在 React 组件中与 Redux Store 交互变轻而易举。

