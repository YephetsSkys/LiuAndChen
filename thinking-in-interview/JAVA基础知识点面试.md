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

***未完待续***