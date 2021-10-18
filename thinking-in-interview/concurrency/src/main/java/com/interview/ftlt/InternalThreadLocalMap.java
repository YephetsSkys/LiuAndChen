/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.interview.ftlt;

import io.netty.util.internal.IntegerHolder;
import io.netty.util.internal.TypeParameterMatcher;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.*;

/**
 * The internal data structure that stores the thread-local variables for Netty and all {@link FastThreadLocal}s.
 * Note that this class is for internal use only and is subject to change at any time.  Use {@link FastThreadLocal}
 * unless you know what you are doing.
 */
public final class InternalThreadLocalMap extends UnpaddedInternalThreadLocalMap {

    private static final int DEFAULT_ARRAY_LIST_INITIAL_CAPACITY = 8;

    /**
     * 用于标识数组的槽位还未使用
     */
    public static final Object UNSET = new Object();

    /**
     * 获取InternalThreadLocalMap。如果线程不是FastThreadLocalThread，则从ThreadLocal中获取
     * @return InternalThreadLocalMap
     */
    public static InternalThreadLocalMap getIfSet() {
        Thread thread = Thread.currentThread();
        // 如果是ftlt，则走netty的ftl，否则走JDK的ThreadLocal
        if (thread instanceof FastThreadLocalThread) {
            return ((FastThreadLocalThread) thread).threadLocalMap();
        }
        return slowThreadLocalMap.get();
    }

    /**
     * 获取InternalThreadLocalMap。如果线程不是FastThreadLocalThread，则从ThreadLocal中获取。如果为null，则创建一个新的InternalThreadLocalMap
     * @return InternalThreadLocalMap
     */
    public static InternalThreadLocalMap get() {
        Thread thread = Thread.currentThread();
        // 如果是ftlt，则走netty的ftl，否则走JDK的ThreadLocal
        if (thread instanceof FastThreadLocalThread) {
            return fastGet((FastThreadLocalThread) thread);
        } else {
            return slowGet();
        }
    }

    /**
     * 走Netty的ftlt模式，获取InternalThreadLocalMap，如果为空，则创建一个新的InternalThreadLocalMap，并且设置到FastThreadLocalThread中
     * @param thread FastThreadLocalThread
     * @return InternalThreadLocalMap
     */
    private static InternalThreadLocalMap fastGet(FastThreadLocalThread thread) {
        InternalThreadLocalMap threadLocalMap = thread.threadLocalMap();
        if (threadLocalMap == null) {
            thread.setThreadLocalMap(threadLocalMap = new InternalThreadLocalMap());
        }
        return threadLocalMap;
    }

    /**
     * 走JDK的ThreadLocal，如果为空，则创建新的InternalThreadLocalMap并设置到slowThreadLocalMap属性中
     * @return InternalThreadLocalMap
     */
    private static InternalThreadLocalMap slowGet() {
        ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = UnpaddedInternalThreadLocalMap.slowThreadLocalMap;
        InternalThreadLocalMap ret = slowThreadLocalMap.get();
        if (ret == null) {
            ret = new InternalThreadLocalMap();
            slowThreadLocalMap.set(ret);
        }
        return ret;
    }

    /**
     * 从当前线程中移除InternalThreadLocalMap。其中也是分FastThreadLocalThread走不同的逻辑
     */
    public static void remove() {
        Thread thread = Thread.currentThread();
        // 如果是ftlt，则走netty的ftl，否则走JDK的ThreadLocal
        if (thread instanceof FastThreadLocalThread) {
            ((FastThreadLocalThread) thread).setThreadLocalMap(null);
        } else {
            slowThreadLocalMap.remove();
        }
    }

    /**
     * 从ThreadLocal中清空InternalThreadLocalMap变量
     */
    public static void destroy() {
        slowThreadLocalMap.remove();
    }

    /**
     * 用于计算FastThreadLocal中的variablesToRemoveIndex属性值赋值，每次有新的FastThreadLocal创建的时候，
     * 将此值自增1，此值关系到将FastThreadLocal存储到InternalThreadLocalMap的indexedVariables数组下标。
     * 可以防止ThreadLocal的不足，在使用拉链法探测hash冲突的时候，如果一直没有找到空闲空间，造成不断的探测空闲空间，影响性能。
     * @return int 索引值
     */
    public static int nextVariableIndex() {
        int index = nextIndex.getAndIncrement();
        if (index < 0) {
            nextIndex.decrementAndGet();
            throw new IllegalStateException("too many thread-local indexed variables");
        }
        return index;
    }

    /**
     * 获取最后一个设置了FastThreadLocal值的索引位置
     * @return int 索引值
     */
    public static int lastVariableIndex() {
        return nextIndex.get() - 1;
    }

    // Cache line padding (must be public)
    // With CompressedOops enabled, an instance of this class should occupy at least 128 bytes.
    public long rp1, rp2, rp3, rp4, rp5, rp6, rp7, rp8, rp9;

    /**
     * 初始化一个indexedVariables为32长度的数组，并且填充UNSET对象
     */
    private InternalThreadLocalMap() {
        super(newIndexedVariableTable());
    }

    /**
     * 初始化数组并填充UNSET对象
     * @return Object[]
     */
    private static Object[] newIndexedVariableTable() {
        Object[] array = new Object[32];
        Arrays.fill(array, UNSET);
        return array;
    }

    public int size() {
        int count = 0;

        if (futureListenerStackDepth != 0) {
            count ++;
        }
        if (localChannelReaderStackDepth != 0) {
            count ++;
        }
        if (handlerSharableCache != null) {
            count ++;
        }
        if (counterHashCode != null) {
            count ++;
        }
        if (random != null) {
            count ++;
        }
        if (typeParameterMatcherGetCache != null) {
            count ++;
        }
        if (typeParameterMatcherFindCache != null) {
            count ++;
        }
        if (stringBuilder != null) {
            count ++;
        }
        if (charsetEncoderCache != null) {
            count ++;
        }
        if (charsetDecoderCache != null) {
            count ++;
        }
        if (arrayList != null) {
            count ++;
        }

        for (Object o: indexedVariables) {
            if (o != UNSET) {
                count ++;
            }
        }

        // 因为 indexedVariables 的第一个元素位置是保留的，所以
        // We should subtract 1 from the count because the first element in 'indexedVariables' is reserved
        // by 'FastThreadLocal' to keep the list of 'FastThreadLocal's to remove on 'FastThreadLocal.removeAll()'.
        return count - 1;
    }

    public StringBuilder stringBuilder() {
        final int stringBuilderCapacity = 1024;
        if (stringBuilder == null) {
            stringBuilder = new StringBuilder(stringBuilderCapacity);
        } else {
            if (stringBuilder.capacity() > stringBuilderCapacity) {
                stringBuilder.setLength(stringBuilderCapacity);
                stringBuilder.trimToSize();
            }
            stringBuilder.setLength(0);
        }
        return stringBuilder;
    }

    public Map<Charset, CharsetEncoder> charsetEncoderCache() {
        Map<Charset, CharsetEncoder> cache = charsetEncoderCache;
        if (cache == null) {
            charsetEncoderCache = cache = new IdentityHashMap<Charset, CharsetEncoder>();
        }
        return cache;
    }

    public Map<Charset, CharsetDecoder> charsetDecoderCache() {
        Map<Charset, CharsetDecoder> cache = charsetDecoderCache;
        if (cache == null) {
            charsetDecoderCache = cache = new IdentityHashMap<Charset, CharsetDecoder>();
        }
        return cache;
    }

    public <E> ArrayList<E> arrayList() {
        return arrayList(DEFAULT_ARRAY_LIST_INITIAL_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public <E> ArrayList<E> arrayList(int minCapacity) {
        ArrayList<E> list = (ArrayList<E>) arrayList;
        if (list == null) {
            arrayList = new ArrayList<Object>(minCapacity);
            return (ArrayList<E>) arrayList;
        }
        list.clear();
        list.ensureCapacity(minCapacity);
        return list;
    }

    public int futureListenerStackDepth() {
        return futureListenerStackDepth;
    }

    public void setFutureListenerStackDepth(int futureListenerStackDepth) {
        this.futureListenerStackDepth = futureListenerStackDepth;
    }

    /**
     * 获取当前线程绑定的随机对象
     * @return ThreadLocalRandom
     */
    public ThreadLocalRandom random() {
        ThreadLocalRandom r = random;
        if (r == null) {
            random = r = new ThreadLocalRandom();
        }
        return r;
    }

    public Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache() {
        Map<Class<?>, TypeParameterMatcher> cache = typeParameterMatcherGetCache;
        if (cache == null) {
            typeParameterMatcherGetCache = cache = new IdentityHashMap<Class<?>, TypeParameterMatcher>();
        }
        return cache;
    }

    public Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache() {
        Map<Class<?>, Map<String, TypeParameterMatcher>> cache = typeParameterMatcherFindCache;
        if (cache == null) {
            typeParameterMatcherFindCache = cache = new IdentityHashMap<Class<?>, Map<String, TypeParameterMatcher>>();
        }
        return cache;
    }

    public IntegerHolder counterHashCode() {
        return counterHashCode;
    }

    public void setCounterHashCode(IntegerHolder counterHashCode) {
        this.counterHashCode = counterHashCode;
    }

    public Map<Class<?>, Boolean> handlerSharableCache() {
        Map<Class<?>, Boolean> cache = handlerSharableCache;
        if (cache == null) {
            // Start with small capacity to keep memory overhead as low as possible.
            handlerSharableCache = cache = new WeakHashMap<Class<?>, Boolean>(4);
        }
        return cache;
    }

    public int localChannelReaderStackDepth() {
        return localChannelReaderStackDepth;
    }

    public void setLocalChannelReaderStackDepth(int localChannelReaderStackDepth) {
        this.localChannelReaderStackDepth = localChannelReaderStackDepth;
    }

    /**
     * 获取指定索引位置存储的对象
     * @param index 索引值
     * @return Object
     */
    public Object indexedVariable(int index) {
        Object[] lookup = indexedVariables;
        return index < lookup.length? lookup[index] : UNSET;
    }

    /**
     *
     * 设置指定索引位置存储的对象，如果当前索引位置超过了indexedVariables的长度，则会涉及到扩容。
     * @param index 索引位置
     * @param value 设置的对象
     * @return {@code true} true则代表是新增,false是替换
     */
    public boolean setIndexedVariable(int index, Object value) {
        Object[] lookup = indexedVariables;
        if (index < lookup.length) {
            Object oldValue = lookup[index]; // 获取当前的值
            lookup[index] = value; // 将新值设置进去
            return oldValue == UNSET; // 判断之前的值是不是占位符，如果是则代表新增，不是代表更新
        } else {
            expandIndexedVariableTableAndSet(index, value);
            return true;
        }
    }

    /**
     * 扩容数组，每次扩容一倍，并且将指定的值存储到指定位置
     * @param index 需要存储的索引位置
     * @param value 设置的对象
     */
    private void expandIndexedVariableTableAndSet(int index, Object value) {
        Object[] oldArray = indexedVariables; // 当前的Object数组
        final int oldCapacity = oldArray.length; // 当前的长度
        int newCapacity = index; // index是这个FastThreadLocal对应的索引值

        // 下面这段代码其实就是扩容到下一个2的次数位置，一般就是扩容一倍。如32扩容到64
        newCapacity |= newCapacity >>>  1;
        newCapacity |= newCapacity >>>  2;
        newCapacity |= newCapacity >>>  4;
        newCapacity |= newCapacity >>>  8;
        newCapacity |= newCapacity >>> 16;
        newCapacity ++;

        Object[] newArray = Arrays.copyOf(oldArray, newCapacity); // 将老的数据往新的数组进行拷贝
        Arrays.fill(newArray, oldCapacity, newArray.length, UNSET); // 将新的多出来的部分，设置占位符
        newArray[index] = value; // 将index对应的值设置完
        indexedVariables = newArray; // 将新的数组提升成Map中的indexedVariables
    }

    /**
     * 移除并返回指定索引位置的对象值，移除的位置会被设置为UNSET对象
     * @param index 要移除的索引位置
     * @return Object
     */
    public Object removeIndexedVariable(int index) {
        Object[] lookup = indexedVariables;
        if (index < lookup.length) {
            Object v = lookup[index];
            lookup[index] = UNSET;
            return v;
        } else {
            return UNSET;
        }
    }

    /**
     * 判断指定位置是否被占用了
     * @param index 要判断的索引位置
     * @return boolean
     */
    public boolean isIndexedVariableSet(int index) {
        Object[] lookup = indexedVariables;
        return index < lookup.length && lookup[index] != UNSET;
    }
}
