# TypeScript 基础语法
摘录来源：https://ts.xcatliu.com/basics/type-of-object-interfaces.html
## 对象的类型--接口
在 TypeScript 中，我们使用接口（Interfaces）来定义对象的类型
### 什么是接口
在面向对象语言中，接口（Interfaces）是一个很重要的概念，它是对行为的抽象，而具体如何行动需要由类（classes）去实现（implement）

TypeScript 中的接口是一个非常灵活的概念，除了可用于对类的一部分行为进行抽象以外，也常用于对「对象的形状（Shape）」进行描述
```
interface Person {
    name: string;
    age: number;
}

let tom: Person = {
    name: 'Tom',
    age: 25
};
```
```
interface Person {
    name: string;
    age: number;
}

let tom: Person = {
    name: 'Tom'
};

index.ts(6,5): error TS2322: Type '{ name: string; }' is not assignable to type 'Person'.
Property 'age' is missing in type '{ name: string; }'.
```
```
interface Person {
    name: string;
    age: number;
}

let tom: Person = {
    name: 'Tom',
    age: 25,
    gender: 'male'
};

index.ts(9,5): error TS2322: Type '{ name: string; age: number; gender: string; }' is not assignable to type 'Person'.
Object literal may only specify known properties, and 'gender' does not exist in type 'Person'.
```
可见，赋值的时候，变量的形状必须和接口的形状保持一致
### 可选属性
有时我们希望不要完全匹配一个形状，那么可以用可选属性：
```
interface Person {
    name: string;
    age?: number;
}

let tom: Person = {
    name: 'Tom'
};
```
这时仍然不允许添加未定义的属性：
```
interface Person {
    name: string;
    age?: number;
}

let tom: Person = {
    name: 'Tom',
    age: 25,
    gender: 'male'
};

examples/playground/index.ts(9,5): error TS2322: Type '{ name: string; age: number; gender: string; }' is not assignable to type 'Person'.
Object literal may only specify known properties, and 'gender' does not exist in type 'Person'.
```