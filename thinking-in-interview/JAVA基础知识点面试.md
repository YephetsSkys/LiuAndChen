### 1.字符串相关

***java中一个String可以有多大***

一个字符串的大小限制有如下:
1：常量池大小(1.8移到堆中)的限制 堆内存的限制；
2：常量池 utf-8字符串的结构 tag [u1] length [u2] bytes [length] u2为无符号16bit 长度最大为65535 bytes；
3：new String 结构 为 char[] + hash char 数组最大值取决于 int；

***字符串常量池在Java内存区域的哪里？可以设置大小吗？***

字符串常量池在JDK6的时候在永久代，在JDK7及以后在堆中。
在JDK6中他的默认大小1009，JDK7+默认大小为60013。可以通过JVM参数-XX:StringTableSize=N来设置其大小。（可以将此结构想象成HashMap，注意此大小为桶大小，非最大存储大小）。可以通过-XX:+PrintStringTableStatistics来打印字符串常量池的统计信息。

***字符串常量池在何时会被回收？***

在Full GC或者CMS GC过程会对StringTable做清理。

### 2.Object类相关

***Object类的hashCode的生成规则***

`hashCode`底层方法的底层在`hotspot/src/share/vm/runtime/synchronizer.cpp`文件中的`get_next_hash`方法实现的。

```
static inline intptr_t get_next_hash(Thread * Self, oop obj) {
  intptr_t value = 0 ;
  // hashCode是一个配置选项，从JDK6开始，其默认值是5，在 hotspot/src/share/vm/runtime/globals.hpp 中
  // product(intx, hashCode, 5, "(Unstable) select hashCode generation algorithm")
  // -XX:hashCode=5 默认值
  if (hashCode == 0) {
     // 获取随机数
     value = os::random() ;
  } else if (hashCode == 1) {
     // 根据对象地址计算
     intptr_t addrBits = cast_from_oop<intptr_t>(obj) >> 3 ;
     value = addrBits ^ (addrBits >> 5) ^ GVars.stwRandom ;
  } else if (hashCode == 2) {
     value = 1 ;            // for sensitivity testing
  } else if (hashCode == 3) {
     value = ++GVars.hcSequence ;
  } else if (hashCode == 4) {
     // 直接返回对象地址
     value = cast_from_oop<intptr_t>(obj) ;
  } else {
     // Marsaglia's xor-shift 算法，默认实现
     // _hashStateX是线程创建时生成的随机数
     unsigned t = Self->_hashStateX ;
     t ^= (t << 11) ;
     // _hashStateY,_hashStateZ,_hashStateW初始化时是固定值
     // 通过重置_hashStateW，来动态改变_hashStateX，_hashStateY，_hashStateZ的属性
     Self->_hashStateX = Self->_hashStateY ;
     Self->_hashStateY = Self->_hashStateZ ;
     Self->_hashStateZ = Self->_hashStateW ;
     unsigned v = Self->_hashStateW ;
     v = (v ^ (v >> 19)) ^ (t ^ (t >> 8)) ;
     Self->_hashStateW = v ;
     value = v ;
  }
  // 因为hash值是保存在对象头的特定位上，此处且运算，检查对应位的hash值是否是0
  value &= markOopDesc::hash_mask;
  if (value == 0) value = 0xBAD ;
  assert (value != markOopDesc::no_hash, "invariant") ;
  TEVENT (hashCode: GENERATE) ;
  return value;
}

// _hashStateX 的初始化代码 hotspot/src/share/vm/runtime/thread.cpp Thread::Thread()方法中
Thread::Thread() {
    // ...
    // thread-specific hashCode stream generator state - Marsaglia shift-xor form
    _hashStateX = os::random() ; // 这里可以看到，唯一的变量就是这个值了
    _hashStateY = 842502087 ;
    _hashStateZ = 0x8767 ;    // (int)(3579807591LL & 0xffff) ;
    _hashStateW = 273326509 ;
    // ...
}
```

这里可以看出通过`-XX:hashCode=n`来选择最底层`hashCode`的生成规则。默认情况下使用`Marsaglia's xor-shift 算法`。

### 3.序列化与反序列化

**为什么Java序列化要实现Serializable接口**

我们可以查看序列化写入对象相关的代码，在`ObjectOutputStream`类的`writeObject0`方法：
```
private void writeObject0(Object obj, boolean unshared) throws IOException {
    // ...
    if (obj instanceof String) {
       writeString((String) obj, unshared);
    } else if (cl.isArray()) {
       writeArray(obj, desc, unshared);
    } else if (obj instanceof Enum) {
       writeEnum((Enum<?>) obj, desc, unshared);
    } else if (obj instanceof Serializable) {
       writeOrdinaryObject(obj, desc, unshared);
    } else {
       if (extendedDebugInfo) {
           throw new NotSerializableException(cl.getName() + "\n" + debugInfoStack.toString());
       } else {
           throw new NotSerializableException(cl.getName());
       }
    }
    // ...
}
```

这里可以看到，如果对象是`String`、`数组`、`枚举`以及实现了`Serializable`类型可以被正常序列化，否则会抛出`NotSerializableException`异常。

**既然已经实现了`Serializable`接口，为什么还要显示指定`serialVersionUID`的值呢?**

因为序列化对象时，如果**不显示的设置`serialVersionUID`，Java在序列化时会根据对象属性自动的生成一个`serialVersionUID`**，再进行存储或用作网络传输。

在反序列化时，会根据对象属性**自动再生成一个新的`serialVersionUID`，和序列化时生成的`serialVersionUID`进行比对，两个`serialVersionUID`相同则反序列化成功，否则就会抛异常***。
```
Exception in thread "main" java.io.InvalidClassException: serial.User; local class incompatible: stream classdesc serialVersionUID = 8985745470054656491, local class serialVersionUID = -4967160969146043535
	at java.base/java.io.ObjectStreamClass.initNonProxy(ObjectStreamClass.java:715)
	...
```

具体代码在`ObjectInputStream`类的`readOrdinaryObject`方法：
```
private Object readOrdinaryObject(boolean unshared) throws IOException {
    // ...
    ObjectStreamClass desc = readClassDesc(false);
    desc.checkDeserialize();
    // ...
}

private ObjectStreamClass readClassDesc(boolean unshared) throws IOException {
    // ...
    byte tc = bin.peekByte();
    ObjectStreamClass descriptor;
    switch (tc) {
        case TC_CLASSDESC:
            descriptor = readNonProxyDesc(unshared);
            break;
    }
    // ...
}

private ObjectStreamClass readNonProxyDesc(boolean unshared) throws IOException {
    // ...
    desc.initNonProxy(readDesc, cl, resolveEx, readClassDesc(false));
    // ...
}

void initNonProxy(ObjectStreamClass model,
                      Class<?> cl,
                      ClassNotFoundException resolveEx,
                      ObjectStreamClass superDesc) throws InvalidClassException {
    // ...
    // 这里如果获取到的suid不一致，会抛出异常
    if (model.serializable == osc.serializable &&
            !cl.isArray() &&
            suid != osc.getSerialVersionUID()) {
        throw new InvalidClassException(osc.name,
                "local class incompatible: " +
                        "stream classdesc serialVersionUID = " + suid +
                        ", local class serialVersionUID = " +
                        osc.getSerialVersionUID());
    }
    // ...
}
```

而当显示的设置`serialVersionUID`后，Java在序列化和反序列化对象时，生成的`serialVersionUID`都为我们设定的`serialVersionUID`，这样就**保证了反序列化的成功**。

**如果我们不设置`serialVersionUID`还能被序列化吗？会有问题吗？**

如果我们不设置`serialVersionUID`可以被序列化，但是可能会报`InvalidClassException`异常。因为我们不显示设置java会给我们自动设置一个默认的。代码在`ObjectStreamCLass`类中：
```
public long getSerialVersionUID() {
    // 这里如果suid为空，则java会通过computeDefaultSUID计算出一个suid
    if (suid == null) {
        suid = AccessController.doPrivileged(
            new PrivilegedAction<Long>() {
                public Long run() {
                    return computeDefaultSUID(cl);
                }
            }
        );
    }
    return suid.longValue();
}
```

这里`computeDefaultSUID`方法其实做的事情简单来说就是通过反射获取这个类的各种信息，将它们放到一个字节数组中，然后使用`hash函数（SHA）`进行运算得到一个代表类的“摘要”。

总结起来就是影响suid的因素有如下：
| 因素 | 具体动作 |
| --- | --- |
| 类名 | 修改类名 |
| 类修饰符 | 增加、减少和修改类修饰符 |
| 类接口 | 增加、减少和实现接口 |
| 类成员方法和构造方法 | 增加和减少方法；修改方法签名 |
| 类成员变量（包括静态、常量） | 增加和减少变量；修改变量签名 |

如果只是改变成员变量的顺序是不会影响到`suid`的计算的。

***未完待续***