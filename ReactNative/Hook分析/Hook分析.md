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

通过for循环，执行了3次setCount(count+1)，那么你觉得count会 +3 吗？  
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
1、prevData为我们定义的一个形参，指当前count应该的值；  
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
1. 对于简单类型的值，例如String、Number 新旧值一样的情况下是不会引起重新渲染的；
2. 对于复杂类型的值，即使新旧值 “看上去是一样的” 也会引起重新渲染。除非新旧值指向同一个对象，或者可以说成新旧值分别是同一个对象的引用；

采用复杂类型的值不是不可以用，很多场景下都需要用到，但是请记得上面的测试结果。

为了可能存在的性能问题，如果可以，最好避免使用复杂类型的值。
