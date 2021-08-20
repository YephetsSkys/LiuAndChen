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

***未完待续***