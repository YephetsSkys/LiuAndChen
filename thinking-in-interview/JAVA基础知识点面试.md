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

### 2.ThreadLocal相关

***ThreadLocal有没有内存泄露问题？***

是有的。

底层为了解决问题，使用到了弱引用。
开发者层面如果在使用完ThreadLocal后，最好手动调用remove操作来删除掉引用。如果不主动删除并且也不把ThreadLocal实例设置为null，则内存也会泄露，除非所属线程消亡。

***ThreadLocal为什么要使用弱指针？***

```
static class Entry extends WeakReference<ThreadLocal<?>> {
    /** The value associated with this ThreadLocal. */
    Object value;

    Entry(ThreadLocal<?> k, Object v) {
        super(k);
        value = v;
    }
}
```

因为Thread实例中引用了ThreadLocalMap，ThreadLocalMap中又使用到了Entry，而Entry的key就是ThreadLocal对象。如果ThreadLocal被回收了，但是因为强引用关系，造成Entry的key一直指向ThreadLocal实例，造成内存泄露。

但是如果只是简单的将Entry改成弱引用，还是会有Value值的内存泄露，因为ThreadLocalMap中还一直维护着一个Entry对象，需要在ThreadLocal=null的时候，主动去remove，才能做到真正的全部回收。

所以ThreadLocal在get和set的时候会主动去清理一下ThreadLocalMap中key=null的Entry对象。

在get的时候，如果命中到则直接返回，如果没有命中到则调用`getEntryAfterMiss`进行内存清理工作并重新rehash。
在set的时候后，会调用`cleanSomeSlots`方法完成内存清理工作并重新rehash。




***未完待续***