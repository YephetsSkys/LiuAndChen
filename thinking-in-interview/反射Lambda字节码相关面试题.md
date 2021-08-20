### 一、Java Method Invoke 相关面试题

#### 1.Java反射的性能为什么比直接调用慢一个数量级左右？

主要影响性能的问题：
- 每一个Method都有一个root，不暴漏给外部，在查找相关的方法的时候（如：`getMethods()`，`getDeclaredMethods()`等），每次copy一个Method；
- method.invoke中每次都要进行参数数组包装；
- 在method.invoke中要进行方法可见性检查；
- 在accessor的java实现方式下，invoke时会检查参数的类型匹配。而在JDK7中MethodHandle来做反射调用，形参和实参是准确的，所以只需要在链接方法的时候做检查，调用时不用再做检查。并且MethodHandle是不可变值，所以jvm可以做激进优化，例如内联。`Method.invoke()`就像是个独木桥一样，各处的反射调用都要挤过去，在调用点上收集到的类型信息就会很乱，影响内联程序的判断，使得`Method.invoke()`自身难以被内联到调用方；

#### 2.为什么MethodHandle性能比反射要快？

***主要原因是：MethodHandle的访问性检查只在创建时检查一次，而Method则是每次调用都检查。***

### 二、字节码

#### 1.请问调用方法的字节码有哪几个？

#### 2.为什么有了invokeVirtual还要有invokeInterface指令？

#### 3.请讲一讲Java字节码层中的`vtable`和`itable`的作用

**未完待续**
