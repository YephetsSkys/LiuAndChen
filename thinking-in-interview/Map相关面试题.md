### HashMap问题整理

#### 1.hashMap 的实现原理？

>- https://blog.csdn.net/qq_37113604/article/details/81353626


#### 2.hashMap负载因子为什么是0.75？怎么扩容的？

>- https://www.jianshu.com/p/2449cd914e3c
>- https://www.cnblogs.com/zengweiming/p/8853144.html

>- HashMap负载因子为什么是0.75？
HashMap有一个初始容量大小，默认是16
static final int DEAFULT_INITIAL_CAPACITY = 1 << 4; // aka 16    
为了减少冲突概率，当HashMap的数组长度达到一个临界值就会触发扩容，把所有元素rehash再放回容器中，这是一个非常耗时的操作。
而这个临界值由负载因子和当前的容量大小来决定：
DEFAULT_INITIAL_CAPACITY*DEFAULT_LOAD_FACTOR
即默认情况下数组长度是16*0.75=12时，触发扩容操作。
所以使用hash容器时尽量预估自己的数据量来设置初始值。
那么，为什么负载因子要默认为0.75，在HashMap注释中有这么一段：

Ideally, under random hashCodes, the frequency of
* nodes in bins follows a Poisson distribution
* (http://en.wikipedia.org/wiki/Poisson_distribution) with a
* parameter of about 0.5 on average for the default resizing
* threshold of 0.75, although with a large variance because of
* resizing granularity. Ignoring variance, the expected
* occurrences of list size k are (exp(-0.5) * pow(0.5, k) /
* factorial(k)). The first values are:
*
* 0:    0.60653066
* 1:    0.30326533
* 2:    0.07581633
* 3:    0.01263606
* 4:    0.00157952
* 5:    0.00015795
* 6:    0.00001316
* 7:    0.00000094
* 8:    0.00000006
* more: less than 1 in ten million
*

>- 在理想情况下，使用随机哈希吗，节点出现的频率在hash桶中遵循泊松分布，同时给出了桶中元素的个数和概率的对照表。
从上表可以看出当桶中元素到达8个的时候，概率已经变得非常小，也就是说用0.75作为负载因子，每个碰撞位置的链表长度超过8个是几乎不可能的。
hash容器指定初始容量尽量为2的幂次方。
HashMap负载因子为0.75是空间和时间成本的一种折中。

>- 泊松分布：https://blog.csdn.net/ccnt_2012/article/details/81114920

#### 3.hashMap怎么添加元素的？为什么hashMap是不安全的?
>- https://blog.csdn.net/swpu_ocean/article/details/88917958

>- 总结
>- HashMap的线程不安全主要体现在下面两个方面：
>- 1.在JDK1.7中，当并发执行扩容操作时会造成环形链和数据丢失的情况。
>- 2.在JDK1.8中，在并发执行put操作时会发生数据覆盖的情况。

#### 4.为什么重写equals方法同时需要重写hashCode方法？

Object类中的`equals`方法中有一段注释，意思是：通常每当此方法被覆盖时，都需要覆盖`hashCode`方法，以维护`hashCode`方法的一般约定，其中规定相等的对象必须具有相同的哈希码。

当我们重写`equals`时，说明我们想更改判断对象是否相等的方式，这样子我们在利用`HashMap`或者`HashSet`的时候来根据key去重，才能保证对象判断方式是我们想要的效果。

### ConcurrentHashMap问题整理

#### 1.ConcurrentHashMap为什么不支持key为null以及value为null？

我们在put的时候，会抛出空指针异常：
```
public V put(K key, V value) {
    return putVal(key, value, false);
}
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    // ...
}
```

主要原因是`Doug Lea`如此设计最主要的原因是，不容忍在并发场景下出现歧义！

比如，通过调用`map.get(key)`获取值，但是返回结果为`null`，如果是在并发场景下，是无法判断是键不存在，还是键对应的值为`null`。在非并发的场景，可以通过`map.contains(key)`的方式检查，但是并发场景中，两次调用之间数据是会发生变化的。

在并发场景下，即使`if`条件满足，键存在，但是并不能保证之后获取值时，键还存在，键是有可能被其它线程删除的。
```
if (map.contains(key)) {
    obj = map.get(key);
}
```

如果非要使用`null`的话，可以使用一个特殊的对象来代替`null`，如：
```
public static final Object NULL = new Object();
```

#### 2.ConcurrentHashMap实现线程安全：put实现线程安全，get加锁了吗？put和get怎么保证线程安全？

第一个：`ConcurrentHashMap`的`get`操作是没有添加锁的。

`get`没有加锁的话，`ConcurrentHashMap`是如何保证读到的数据不是脏数据的呢？

- `get`操作全程不需要加锁是因为`Node`的成员`val`是用`volatile`修饰的和数组用`volatile`修饰没有关系；
- `Node数组`用`volatile`修饰主要是保证在数组扩容的时候保证可见性；

第二个：我们理解一下在get的时候，`ConcurrentHashMap`可能会执行的写操作？

- 1、整个Map为空，正在执行`initTable`方法；
- 2、定位到的`Node`节点正在执行`put`操作；
- 3、正在执行`rehash`操作；
- 4、`remove`操作；

**第一个：** 很简单，如果Map正在执行初始化，那get获取不到数据，就直接返回null即可；

**第二个：** 当不为空的话，执行put操作，涉及到Node节点的新增或者值替换。如果当前Node为空，则直接通过CAS来插入新节点。如果不为空，这个时候会对头节点进行加锁（`synchronized`），再进行Node节点的遍历并将新节点插入到链表的末尾（插入末尾就不涉及到链表前节点的指针变化），get操作只需要进行遍历Node即可。

**第三个：** Map在执行`rehash`的操作是比较复杂的。`rehash`的时候，Map会对Node节点的头节点加锁，那get是无锁的，如何做到在`rehash`的时候，还能正常的遍历？在`rehash`的时候，会将暂时扩容的Node数组存储到`nextTable`属性中，首先每个线程承担不小于 16 个槽中的元素的扩容，然后从右向左划分`16`个槽给当前线程去迁移，每当开始迁移一个槽中的元素的时候，线程会锁住当前槽中列表的头元素，假设这时候正好有`get`请求过来会仍旧在旧的列表中访问，如果是插入、修改、删除、合并、compute等操作时遇到 ForwardingNode，当前线程会加入扩容大军帮忙一起扩容，扩容结束后再做元素的更新操作。

**第四个：** Map在执行`remove`操作的时候，如果找到了元素，则会执行`pred.next = e.next;`或者`setTabAt(tab, i, e.next);`，不会去更改`next`指针，所以可以顺利的能够进行遍历。而且在执行`remove`操作的时候，会对定位的桶上的首节点进行加锁，防止多线程并发造成`next`指向遗漏出问题。
