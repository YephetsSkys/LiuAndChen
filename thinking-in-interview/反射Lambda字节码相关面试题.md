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

#### 4.在有大量的判断的时候，为什么推荐使用switch而不是if？

使用`if`判断，最坏情况是`O(n)`的时间复杂度。如果是字符串，在每次执行字符串比较的时候需要调用`equals()`方法，可能还会对字符串的内容会进行遍历操作。

而使用`switch`的话，会被编译为两种指令，一种是`lookupswitch`和`tableswitch`：
- `tableswitch`将栈顶部的int值直接用作表中的索引，以获取跳转目标并立即执行跳转。时间复杂度为O(1)操作，这意味着它的速度非常快；
- `lookupswitch`将栈顶部的int值与表中的键进行比较，直到找到匹配项。可以理解为维护了一个升序排列的索引表，通过逐个比较key来查找匹配的待跳转的行数。意味着JVM在做优化的时候可以使用二分查找来减少循环次数，算法复杂度是`O(log n)`。

`tableswitch`字节码指令代码如下：
```
CASE(_tableswitch): {
  jint* lpc  = (jint*)VMalignWordUp(pc+1);
  int32_t  key  = STACK_INT(-1);
  int32_t  low  = Bytes::get_Java_u4((address)&lpc[1]);
  int32_t  high = Bytes::get_Java_u4((address)&lpc[2]);
  int32_t  skip;
  key -= low;
  skip = ((uint32_t) key > (uint32_t)(high - low))
              ? Bytes::get_Java_u4((address)&lpc[0])
              : Bytes::get_Java_u4((address)&lpc[key + 3]);
  // ...
}
```

`lookupswitch`字节码指令代码如下：
```
CASE(_lookupswitch): {
  jint* lpc  = (jint*)VMalignWordUp(pc+1);
  int32_t  key  = STACK_INT(-1);
  int32_t  skip = Bytes::get_Java_u4((address) lpc); /* default amount */
  int32_t  npairs = Bytes::get_Java_u4((address) &lpc[1]);
  while (--npairs >= 0) {
      lpc += 2;
      if (key == (int32_t)Bytes::get_Java_u4((address)lpc)) {
          skip = Bytes::get_Java_u4((address)&lpc[1]);
          break;
      }
  }
  // ...
}
```


**什么条件下`switch`被编译为`tableswitch`或`lookupswitch`？**

我们查看`jdk.compiler/share/classes/com/sun/tools/javac/jvm/Gen.java`源码

```
long table_space_cost = 4 + ((long) hi - lo + 1); // words
long table_time_cost = 3; // comparisons
long lookup_space_cost = 3 + 2 * (long) nlabels;
long lookup_time_cost = nlabels;
int opcode =
    nlabels > 0 &&
    table_space_cost + 3 * table_time_cost <=
    lookup_space_cost + 3 * lookup_time_cost
    ?
    tableswitch : lookupswitch;
```

这里可以看出，基本跟`switch`的选项以及最低值和最高值相关。如：`1,2,6`就会被编译为`lookupswitch`，而`1,2,5`会被编译为`tableswitch`。

那`switch`又如何支持字符串比较的呢？我们通过下面代码看一下：
```
public static void main(String[] args) {
    String s = "1";
    switch(s) {
        case "A":
        case "B":
        case "X":
        default :
    }
}
```

看一下编译后的字节码指令：
```
  0 ldc #2 <1>
  2 astore_1
  3 aload_1
  4 astore_2
  5 iconst_m1
  6 istore_3
  7 aload_2
  8 invokevirtual #3 <java/lang/String.hashCode>
 11 lookupswitch 3
	65:  44 (+33)
	66:  58 (+47)
	88:  72 (+61)
	default:  83 (+72)
 44 aload_2
 45 ldc #4 <A>
 47 invokevirtual #5 <java/lang/String.equals>
 50 ifeq 83 (+33)
 53 iconst_0
 54 istore_3
 55 goto 83 (+28)
 58 aload_2
 59 ldc #6 <B>
 61 invokevirtual #5 <java/lang/String.equals>
 64 ifeq 83 (+19)
 67 iconst_1
 68 istore_3
 69 goto 83 (+14)
 72 aload_2
 73 ldc #7 <X>
 75 invokevirtual #5 <java/lang/String.equals>
 78 ifeq 83 (+5)
 81 iconst_2
 82 istore_3
 83 iload_3
 84 tableswitch 0 to 2	0:  112 (+28)
	1:  112 (+28)
	2:  112 (+28)
	default:  112 (+28)
112 return
```

其实说白了，就是首先调用字符串的`hashCode()`方法后，通过`lookupswitch`来计算出值匹配的项后再通过`tableswitch`指令来执行具体的代码。将上面的代码翻译就是如下所示：
```
public static void main(String[] args) {
    String s = "1";
    int hashCode = s.hashCode();
    int k = 0;
    switch(s) {
        case 44:
            if("A".equals(s)) {
                k = 0;
            }
        case 58:
            if("B".equals(s)) {
                k = 1;
            }
        case 72:
            if("X".equals(s)) {
                k = 2;
            }
        default :
    }
    switch(k) {
        case 0:
        case 1:
        case 2:
        default:
    }
}
```

字节码面前一切都暴露了，其实字符串的`switch`就是个语法糖，`javac`为我们承担了一切。

**未完待续**
