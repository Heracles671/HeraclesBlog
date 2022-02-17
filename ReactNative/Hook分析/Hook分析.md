# Hook 分析
React 的很多钩子光靠官方文档的讲解无法真正地理解，还是要本地跑一下来验证
## 参考
1. [react-hook-tutorial](https://github.com/puxiao/react-hook-tutorial)
------
## useState
```
const [variable,setVariable] = useState(value);

setVariable(newValue);//修改variable的值
```
### 解决数据异步：

还是基于上面那个示例，假设现在新增1个按钮，点击该按钮后执行以下代码：  

    for(let i=0; i<3; i++){
      setCount(count+1);
    }

>通过for循环，执行了3次setCount(count+1)，那么你觉得count会 +3 吗？  
答案是：肯定不会   
无论for循环执行几次，最终实际结果都将是仅仅执行一次 +1。  
为什么？  
类组件中setState赋值过程是异步的，同样在Hook中 setXxx 赋值也是异步的，比如上述代码中的setCount。  
虽然执行了3次setCount(count+1)，可是每一次修改后的count并不是立即生效的。当第2次和第3次执行时获取到count的值和第1次获取到的count值是一样的，所以最终其实相当于仅执行了1次。

### 解决办法：

你肯定第一时间想到的是这样解决方式：  

    let num = count;
    for(let i=0; i<3; i++){
      num +=1;
    }
    setCount(num);

这样做肯定没问题，只不过有更简便、性能更高的方式。  

和类组件中解决异步的办法类似，就是不直接赋值，而是采用“箭头函数返回值的形式”赋值。 

把代码修改为：  

    for(let i=0; i<3; i++){
      setCount(prevData => {return prevData+1});
      //可以简化为 setCount(prevData => prevData+1);
    }

代码分析：  
>1、prevData为我们定义的一个形参，指当前count应该的值；  
2、{return prevData+1} 中，将 prevData+1，并将运算结果return出去。当然也非常推荐使用更加简化的写法：setCount(prevData => prevData+1)；  
3、最终将prevData赋值给count；
补充说明：你可以将prevData修改成任意你喜欢的变量名称，比如prev，只需要确保和后面return里的一致即可。  

### 不同数值类型的赋值
简单数值类型：
```
setCount(count+1);
```
对象：
```
const [person, setPerson] = useState({name:'cc',age:17});

setPerson({...person,age:18});
```
数组：
```
const [arr, setArr] = useState(['react', 'Koa']);

setArr([str,...arr]);//添加至头

setArr([...arr, str]);//添加至尾

const new_arr = [...arr];
new_arr.shift();//从头删除1项目
setArr(new_arr);

const new_arr = [...arr];
new_arr.pop();//从尾删除1项目
setArr(new_arr);

const new_arr = [...arr];
new_arr.splice(index,1);//删除当前项
setArr(new_arr);
```
### 性能问题
>1、对于简单类型的值，例如String、Number 新旧值一样的情况下是不会引起重新渲染的；   
2、对于复杂类型的值，即使新旧值 “看上去是一样的” 也会引起重新渲染。除非新旧值指向同一个对象，或者可以说成新旧值分别是同一个对象的引用；  
采用复杂类型的值不是不可以用，很多场景下都需要用到，但是请记得上面的测试结果。
为了可能存在的性能问题，如果可以，最好避免使用复杂类型的值。
------
## useEffect
```
  useEffect(() => {
    console.log('mount');  //1. --> 组件加载后
    return () => {
      console.log('ummount');  //2. --> 组件卸载前
    };
  }, [deps]);
```

useEffect 回调函数的执行时机：
1. 组件加载后：只会执行语句1
2. 组件更新后：先执行语句2，再执行语句1
3. 组件卸载前：只会执行语句2

其实完全可以用加载和卸载来解释更新，换言之，什么叫更新：先卸载，后加载。所以更新时会先调用 `return` 后的函数，再调用 `useEffect` 的回调函数

关于[deps]的效用，其实就是取了组件更新的所有情况中的子集，即：只关心由某些因素导致的组件更新：
1. 若缺省，关心全部更新情况；   
2. 若传值，只关心某些更新情况；   
3. 若传值，不关心更新；
-----
## useContext
避免在多层嵌套中向每层组件都传递相同的参数，提高子组件的复用性
```
//global-context.js
import React from 'react';
const GlobalContext = React.createContext(); //请注意，这里还可以给React.createContext()传入一个默认值
//例如：const GlobalContext = React.createContext({name:'Yang',age:18})
//假如<GlobalContext.Provider>中没有设置value的值，就会使用上面定义的默认值
export default GlobalContext;

...

//component.js
import React, { useContext } from 'react';
import GlobalContext from './global-context';

function AppComponent() {
  //标签<GlobalContext.Provider>中向下传递数据，必须使用value这个属性，且数据必须是键值对类型的object
  //如果不添加value，那么子组件获取到的共享数据value值是React.createContext(defaultValues)中的默认值defaultValues
  return <div>
    <GlobalContext.Provider value={{name:'puxiao',age:34}}>
        <MiddleComponent />
    </GlobalContext.Provider>
  </div>
}

function MiddleComponent(){
  //MiddleComponent 不需要做任何 “属性数据传递接力”，因此降低该组件数据传递复杂性，提高组件可复用性
  return <div>
    <ChildComponent />
  </div>
}

function ChildComponent(){
  const global = useContext(GlobalContext); //获取共享数据对象的value值
  //忘掉<GlobalContext.Consumer>标签，直接用global获取需要的值
  return <div>
    {global.name} - {global.age}
  </div>
}

export default AppComponent;
```
同时传递多个共享数据值给子组件：
```
import React,{ useContext } from 'react'

const UserContext = React.createContext();
const NewsContext = React.createContext();

function AppComponent() {
  return (
    <UserContext.Provider value={{name:'puxiao'}}>
        <NewsContext.Provider value={{title:'Hello React Hook.'}}>
            <ChildComponent />
        </NewsContext.Provider>
    </UserContext.Provider>
  )
}

function ChildComponent(){
  const user = useContext(UserContext);
  const news = useContext(NewsContext);
  return <div>
    {user.name} - {news.title}
  </div>
}

export default AppComponent;
```
-----
## useReducer
在React源码中，实际上useState就是由useReducer实现的，所以useReducer准确来说是useState的原始版
```
import React, { useReducer } from 'react'; //引入useReducer

//定义好“事件处理函数” reducer
function reducer(state, action) {
  switch (action) {
    case 'xx':
        return xxxx;
    case 'xx':
        return xxxx;
    default:
        return xxxx;
  }
}

function Component(){
  //声明一个变量xxx，以及对应修改xxx的dispatch
  //将事件处理函数reducer和默认值initialValue作为参数传递给useReducer
  const [xxx, dispatch] = useReducer(reducer, initialValue); 

  //若想获取xxx的值，直接使用xxx即可
  
  //若想修改xxx的值，通过dispatch来修改
  dispatch('xx');
}

//请注意，上述代码中的action只是最基础的字符串形式，事实上action可以是多属性的object，这样可以自定义更多属性和更多参数值
//例如 action 可以是 {type:'xx',param:xxx}
```
举例：
```
import React, { useReducer } from 'react';

function reducer(state,action){
  //根据action.type来判断该执行哪种修改
  switch(action.type){
    case 'add':
      //count 最终加多少，取决于 action.param 的值
      return state + action.param;
    case 'sub':
      return state - action.param;
    case 'mul':
      return state * action.param;
    default:
      console.log('what?');
      return state;
  }
}

function getRandom(){
  return Math.floor(Math.random()*10);
}

function CountComponent() {
  const [count, dispatch] = useReducer(reducer,0);

  return <div>
    {count}
    <button onClick={() => {dispatch({type:'add',param:getRandom()})}} >add</button>
    <button onClick={() => {dispatch({type:'sub',param:getRandom()})}} >sub</button>
    <button onClick={() => {dispatch({type:'mul',param:getRandom()})}} >mul</button>
  </div>;
}

export default CountComponent;
```
个按钮只是负责通知reducer“我希望做什么事情”，具体怎么做完全由reducer来执行。这样实现了修改数据具体执行逻辑与按钮点击处理函数的抽离。

如果不使用useReducer，而是使用之前学习过的useState，那么对count的每一种修改逻辑代码，都必须分散写在每个按钮的点击事件处理函数中。

### useReducer + useContext 实现 Redux 的功能

>实现原理   
用 useContext 实现“获取全局数据”   
用 userReducer 实现“修改全局数据”

>实现思路   
1、用React.createContext()定义一个全局数据对象；   
2、在父组件中用 userReducer 定义全局变量xx和负责抛出修事件的dispatch；   
3、在父组件之外，定义负责具体修改全局变量的处理函数reducer，根据修改xx事件类型和参数，执行修改xx的值；   
4、在父组件中用 <XxxContext.Provider value={{xx,dispathc}}> 标签把 全局共享数据和负责抛出修改xx的dispatch 暴露给子组件；   
5、在子组件中用 useContext 获取全局变量；   
6、在子组件中用 xxContext.dispatch 去抛出修改xx的事件，携带修改事件类型和参数；

共享对象代码：
```
import React from 'react';
const CountContext = React.createContext();
export default CountContext;
```
父组件代码：
```
import React, { useReducer } from 'react';
import CountContext from './CountContext';
import ComponentA from './ComponentA';
import ComponentB from './ComponentB';
import ComponentC from './ComponentC';

const initialCount = 0; //定义count的默认值

//修改count事件处理函数，根据修改参数进行处理
function reducer(state, action) {
//注意这里先判断事件类型，然后结合携带的参数param 来最终修改count
switch (action.type) {
    case 'add':
        return state + action.param;
    case 'sub':
        return state - action.param;
    case 'mul':
        return state * action.param;
    case 'reset':
        return initialCount;
    default:
        console.log('what?');
        return state;
}
}

function ParentComponent() {
  //定义全局变量count，以及负责抛出修改事件的dispatch
  const [count, dispatch] = useReducer(reducer, initialCount);

  //请注意：value={{count,dispatch} 是整个代码的核心，把将count、dispatch暴露给所有子组件
  return <CountContext.Provider value={{count,dispatch}}>
    <div>
        ParentComponent - count={count}
        <ComponentA />
        <ComponentB />
        <ComponentC />
    </div>
  </CountContext.Provider>
}

export default ParentComponent;
```
子组件A的代码：
```
import React,{ useState, useContext } from 'react';
import CountContext from './CountContext';

function CopmpoentA() {
  const [param,setParam] = useState(1);
  //引入全局共享对象，获取全局变量count，以及修改count对应的dispatch
  const countContext = useContext(CountContext);

  const inputChangeHandler = (eve) => {
    setParam(eve.target.value);
  }

  const doHandler = () => {
    //若想修改全局count，先获取count对应的修改抛出事件对象dispatch，然后通过dispatch将修改内容抛出
    //抛出的修改内容为：{type:'add',param:xxx}，即告诉count的修改事件处理函数，本次修改的类型为add，参数是param
    //这里的add和param完全是根据自己实际需求自己定义的
    countContext.dispatch({type:'add',param:Number(param)});
  }

  const resetHandler = () => {
    countContext.dispatch({type:'reset'});
  }

  return <div>
        ComponentA - count={countContext.count}
        <input type='number' value={param} onChange={inputChangeHandler} />
        <button onClick={doHandler}>add {param}</button>
        <button onClick={resetHandler}>reset</button>
    </div>
}

export default CopmpoentA;
```