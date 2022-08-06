#### 腾讯一面：后端15连问

- 1.聊聊项目，好的设计，好的代码
- 2.谈谈什么是零拷贝？
- 3.一共有几种 IO 模型？NIO 和多路复用的区别？
- 4.Future 实现阻塞等待获取结果的原理？
- 5.ReentrantLock和 Synchronized 的区别？Synchronized 的原理？
- 6.聊聊AOS？ReentrantLock的实现原理？
- 7.乐观锁和悲观锁，让你来写你怎么实现？
- 8.Paxos 协议了解？工作流程是怎么样的？
- 9.B+树聊一下？B+树是不是有序？B+树和B-树的主要区别？
- 10.TCP的拥塞机制
- 11.工作中有过JVM实践吗？
- 12.数据库分库分表的缺点是啥？
- 13.分布式事务如何解决？TCC 了解？
- 14.RocketMQ 如何保证消息的准确性和安全性？
- 15.算法题：三个数求和

##### 1.聊聊项目，好的设计，好的代码

随便回答吧。

##### 2.谈谈什么是零拷贝？

[操作系统相关 - 3.什么是零拷贝？](./操作系统相关.md)

##### 3.一共有几种 IO 模型？NIO 和多路复用的区别？

[Netty面试题 - 3.一共有几种 I/O 模型？](./Netty面试题.md)

[Netty面试题 - 9.NIO 和多路复用的区别？](./Netty面试题.md)

##### 4.Future实现阻塞等待获取结果的原理？

`FutureTask`就是`Runnable`和`Future`的结合体，我们可以把`Runnable`看作生产者，`Future`看作消费者。而`FutureTask`是被这两者共享的，生产者运行`run`方法计算结果，消费者通过`get`方法获取结果。

生产者消费者模式，如果生产者数据还没准备的时候，消费者会被阻塞。当生产者数据准备好了以后会唤醒消费者继续执行。

生产者`run`方法：
```
public void run() {
    // 如果状态state不是 NEW，或者设置 runner 值失败,直接返回
    if (state != NEW ||
        !UNSAFE.compareAndSwapObject(this, runnerOffset,
                                     null, Thread.currentThread()))
        return;
    try {
        Callable<V> c = callable;
        if (c != null && state == NEW) {
            V result;
            boolean ran;
            try {
                //调用callable的call方法，获取结果
                result = c.call();
                //运行成功
                ran = true;
            } catch (Throwable ex) {
                result = null;
                //运行不成功
                ran = false;
                //设置异常
                setException(ex);
            }
            //运行成功设置返回结果
            if (ran)
                set(result);
        }
    } finally {
        runner = null;
        int s = state;
        if (s >= INTERRUPTING)
            handlePossibleCancellationInterrupt(s);
    }
}
```

消费者的`get`方法
```
public V get() throws InterruptedException, ExecutionException {
    int s = state;
    //如果状态小于等于 COMPLETING，表示 FutureTask 任务还没有完成， 则调用awaitDone让当前线程等待。
    if (s <= COMPLETING)
        s = awaitDone(false, 0L);
    return report(s);
}
```

`awaitDone`做了什么事情呢？
```
private int awaitDone(boolean timed, long nanos) throws InterruptedException {
    final long deadline = timed ? System.nanoTime() + nanos : 0L;
    WaitNode q = null;
    boolean queued = false;
    for (;;) {
        // 如果当前线程是中断标记，则  
        if (Thread.interrupted()) {
            //那么从列表中移除节点 q，并抛出 InterruptedException 异常
            removeWaiter(q);
            throw new InterruptedException();
        }

        int s = state;
        //如果状态已经完成，表示FutureTask任务已结束
        if (s > COMPLETING) {
            if (q != null)
                q.thread = null;
            //返回
            return s;
        }
        // 表示还有一些后序操作没有完成，那么当前线程让出执行权
        else if (s == COMPLETING) // cannot time out yet
            Thread.yield();
        //将当前线程阻塞等待
        else if (q == null)
            q = new WaitNode();
        else if (!queued)
            queued = UNSAFE.compareAndSwapObject(this, waitersOffset,
                                                 q.next = waiters, q);
        //timed 为 true 表示需要设置超时                                        
        else if (timed) {
            nanos = deadline - System.nanoTime();
            if (nanos <= 0L) {
                removeWaiter(q);
                return state;
            }
            //让当前线程等待 nanos 时间
            LockSupport.parkNanos(this, nanos);
        }
        else
            LockSupport.park(this);
    }
}
```

##### 5.ReentrantLock和Synchronized的区别？Synchronized的原理？

[多线程并发与锁面试题总结 - 6.ReentrantLock和Synchronized的区别？](./多线程并发与锁面试题总结.md)

[多线程并发与锁面试题总结 - 3.synchronized底层原理，wait和notify底层原理？](./多线程并发与锁面试题总结.md)

##### 6.聊聊AQS？ReentrantLock的实现原理？

[多线程并发与锁面试题总结 - 7.说说AQS的底层原理](./多线程并发与锁面试题总结.md)

[多线程并发与锁面试题总结 - 5.ReentrantLock底层原理](./多线程并发与锁面试题总结.md)

##### 7.乐观锁和悲观锁，让你来写你怎么实现？

*悲观锁：*

悲观锁她专一且缺乏安全感了，她的心只属于当前线程，每时每刻都担心着它心爱的数据可能被别的线程修改。因此一个线程拥有（获得）悲观锁后，其他任何线程都不能对数据进行修改啦，只能等待锁被释放才可以执行。

- SQL语句`select ...for update`就是悲观锁的一种实现；
- 还有Java的`synchronized`关键字也是悲观锁的一种体现；

*乐观锁：*

乐观锁的很乐观，它认为数据的变动不会太频繁,操作时一般都不会产生并发问题。因此，它不会上锁，只是在更新数据时，再去判断其他线程在这之前有没有对数据进行过修改。实现方式：乐观锁一般会使用版本号机制或CAS算法实现。

##### 8.Paxos协议了解？工作流程是怎么样的？

`Paxos`涉及三种角色，分别是`Proposer`、`Accecptor`、`Learners`。

- `Proposer`：它可以提出提案 (`Proposal`)，提案信息包括提案编号和提案值。
- `Acceptor`：接受接受（`accept`）提案。一旦接受提案，提案里面的提案值（可以用V表示）就被选定了。
- `Learner`: 哪个提案被选定了,`Learner`就学习这个被选择的提案

>- 一个进程可能是`Proposer`,也可能是`Acceptor`，也可能是`Learner`。

太多了，感觉面试这个直接说不了解就行了吧。

##### 9.B+树聊一下？B+树是不是有序？B+树和B-树的主要区别？

`B+`树是有序的。

*B+树和B-树的主要区别？*
- `B-`树内部节点是保存数据的;而`B+`树内部节点是不保存数据的，只作索引作用，它的叶子节点才保存数据。
- `B+`树相邻的叶子节点之间是通过链表指针连起来的，`B-`树却不是。
- 查找过程中，`B-`树在找到具体的数值以后就结束，而`B+`树则需要通过索引找到叶子结点中的数据才结束；
- `B-`树中任何一个关键字出现且只出现在一个结点中，而`B+`树可以出现多次。

##### 10.TCP的拥塞机制？

[TCPIP与网络通信 - 9.TCP怎么实现拥塞控制？](./TCPIP与网络通信.md)

##### 11.工作中有过JVM实践吗？

参考[JVM面试题目解析](JVM面试题目解析.md)随便说说吧。

*常用调优策略*
- 选择合适的垃圾回收器
- 调整内存大小(垃圾收集频率非常频繁,如果是内存太小，可适当调整内存大小)
- 调整内存区域大小比率（某一个区域的GC频繁，其他都正常。）
- 调整对象升老年代的年龄（老年代频繁GC，每次回收的对象很多。）
- 调整大对象的标准(老年代频繁GC，每次回收的对象很多,而且单个对象的体积都比较大。)
- 调整GC的触发时机(CMS，G1 经常 Full GC，程序卡顿严重。)
- 调整JVM本地内存大小(GC的次数、时间和回收的对象都正常，堆内存空间充足，但是报OOM)。

##### 12.数据库分库分表的缺点是啥？

- 事务问题，已经不可以用本地事务了，需要用分布式事务。
- 跨节点Join的问题：解决这一问题可以分两次查询实现
- 跨节点的`count`,`order by`,`group by`以及聚合函数问题：分别在各个节点上得到结果后在应用程序端进行合并。
- ID问题：数据库被切分后，不能再依赖数据库自身的主键生成机制啦，最简单可以考虑UUID；
- 跨分片的排序分页问题（后台加大pagesize处理？）

##### 13.分布式事务如何解决？TCC了解？

参考[MySQL经典面试题解析 - 13.说说你了解的分布式事务以及`CAP`和`BASE`理论。](./MySQL经典面试题解析.md)

参考[MySQL经典面试题解析 - 14.TCC有了解？](./MySQL经典面试题解析.md)

##### 14.RocketMQ如何保证消息的准确性和安全性？

消息不丢失的话，从生产者、存储端、消费端去考虑。

可以参考[消息队列相关面试题 - 3.kafka生产者和消费者的消息传输保障分别是什么？](./消息队列相关面试题.md)

##### 15.算法题：三个数求和

给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，使得 a + b + c = 0 ？请你找出所有和为 0 且不重复的三元组。

注意：答案中不可以包含重复的三元组

实例1：
```
输入：nums = [-1,0,1,2,-1,-4]
输出：[[-1,-1,2],[-1,0,1]]
```
实例2：
```
输入：nums = [0]
输出：[]
```

思路：
```
这道题可以先给数组排序，接着用左右双指针。
```

完整代码如下：
```
class Solution {
    public List<List<Integer>> threeSum(int[] nums) {

        List<List<Integer>> result = new LinkedList<>();
        if(nums==null||nums.length<3){ //为空或者元素个数小于3，直接返回
            return result;

        }

        Arrays.sort(nums); //排序

        for(int i=0;i<nums.length-2;i++){ //遍历到倒数第三个，因为是三个数总和
            if(nums[i]>0){ //大于0可以直接跳出循环了
                break;
            }

            if(i>0&&nums[i]==nums[i-1]){ //过滤重复
                continue;
            }

            int left = i+1;  //左指针
            int right = nums.length-1; //右指针
            int target = - nums[i];  //目标总和，是第i个的取反，也就是a+b+c=0,则b+c=-a即可

            while(left<right){
                if(nums[left]+ nums[right]==target){ //b+c=-a,满足a+b+c=0
                   result.add(Arrays.asList(nums[i],nums[left],nums[right]));
                   left++;  //左指针右移
                   right--;  //右指针左移
                   while(left<right&&nums[left]==nums[left-1]) left++; //继续左边过滤重复
                   while(left<right&&nums[right]==nums[right+1]) right--; //继续右边过滤重复
                }else if(nums[left]+ nums[right]<target){
                   left++; //小于目标值，需要右移，因为排好序是从小到大的
                }else{
                  right--;  
                }

            }
        }
            return result;
        }
}
```

---

