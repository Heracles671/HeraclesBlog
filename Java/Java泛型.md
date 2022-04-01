# ? extends 泛型上界：
声明完之后，只能读，不能写：
```java
ArrayList<? extends Fruit> fruits =  new ArrayList<Apple>();

getTotalWeight(fruits);
float getTotalWeight(List<? extends Fruit> fruits){
    float weight = 0;
    for (Fruit fruit : fruits) {
        weight += fruit.getWeight();
    }
    return weight;
}  
```
# ？super 泛型下界
声明完之后，只能写，不能读：
```java
ArrayList<? super Apple> apples =  new ArrayList<Fruit>();

Apple apple = new Apple();
apple.addMeToList(apples);
public class Apple extends Fruit {
    public void addMeToList(List<? super Apple> list) {
        list.add(this);
    }
}
```