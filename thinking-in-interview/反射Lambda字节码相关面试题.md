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

一共有`5`种：
- `invokestatic`: 用于调用静态方法；
- `invokespecial`: 用于调用私有实例方法、构造器方法以及使用super关键词调用父类的实例方法等；
- `invokevirtual`: 用于调用非私有实例方法；
- `invokeinterface`: 用于调用接口方法（子类实现父类调用那种）；
- `invokedynamic`: 用于调用动态方法；

#### 2.为什么有了invokeVirtual还要有invokeInterface指令？

`invokeVirtual`指令用于调用虚方法，每个类的虚方法表是固定的，因为Java只能单继承。子类只要包含父类`vtable`，并且和父类的函数包含部分编号是一致的，就可以直接使用`父类的函数编号找到对应的子类实现函数`（这里在查找方法的时候性能就快多了）。

但是接口确实可以多个，不同的类可能会实现了多个不同的接口，在虚表里该接口所实现方法的索引会不一致。这样每次解析的虚表索引都可能会不同，因此不能进行缓存，需要每次都进行重新遍历搜索其索引位置。

接口的方法调用会比普通的子类继承的虚函数调用要慢。另外，为了表现接口调用的不同解析做法，JVM会插入另外的字节码`invokeinterface`来指示需要每次调用解析。

#### 3.请讲一讲Java字节码层中的`vtable`和`itable`的作用

`vtable`用于保存类的所有的虚方法表，维护了方法符号引用->方法入口。在方法调用`invokevirtual`指令的时候，可以通过常量池的符号引用获取方法的入口，并执行方法。

`itable`保存了接口方法在`itable`中的索引值以及方法入口。在方法调用`invokeinterface`指令的时候，首先先扫描`itableOffset`中此方法的索引值，最后通过索引值找到`itableMethod`的方法入口，并执行方法。

**未完待续**
