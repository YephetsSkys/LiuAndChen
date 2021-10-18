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

import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.PlatformDependent;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * A special variant of {@link ThreadLocal} that yields higher access performance when accessed from a
 * {@link io.netty.util.concurrent.FastThreadLocalThread}.
 * <p>
 * Internally, a {@link FastThreadLocal} uses a constant index in an array, instead of using hash code and hash table,
 * to look for a variable.  Although seemingly very subtle, it yields slight performance advantage over using a hash
 * table, and it is useful when accessed frequently.
 * </p><p>
 * To take advantage of this thread-local variable, your thread must be a {@link io.netty.util.concurrent.FastThreadLocalThread} or its subtype.
 * By default, all threads created by {@link DefaultThreadFactory} are {@link io.netty.util.concurrent.FastThreadLocalThread} due to this reason.
 * </p><p>
 * Note that the fast path is only possible on threads that extend {@link io.netty.util.concurrent.FastThreadLocalThread}, because it requires
 * a special field to store the necessary state.  An access by any other kind of thread falls back to a regular
 * {@link ThreadLocal}.
 * </p>
 *
 * 每个FastThreadLocal实例在初始化的时候都会被分配一个 JVM 全局唯一 ID：index。在获取线程本地变量时，使用这个索引。
 *
 * @param <V> the type of the thread-local variable
 * @see ThreadLocal
 */
public class FastThreadLocal<V> {

    // 占用第0个位置的数组索引，因为此值会在FastThreadLocal类加载的时候加载
    // 这个其实维护了一个Set集合，里面存储了所有的FastThreadLocal信息。
    private static final int variablesToRemoveIndex = InternalThreadLocalMap.nextVariableIndex();

    /**
     * 删除绑定到当前线程的所有 {@link FastThreadLocal} 变量。 当您处于容器环境中并且不想将线程局部变量留在您不管理的线程中时，此操作很有用。
     */
    public static void removeAll() {
        // 获取当前维护的InternalThreadLocalMap
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
        if (threadLocalMap == null) {
            return;
        }

        try {
            // 获取维护的所有的FastThreadLocal集合 variablesToRemoveIndex = 0固定位置
            Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
            if (v != null && v != InternalThreadLocalMap.UNSET) {
                @SuppressWarnings("unchecked")
                Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>) v;
                // 循环调用并清理threadLocalMap维护的FastThreadLocal变量
                FastThreadLocal<?>[] variablesToRemoveArray =
                        variablesToRemove.toArray(new FastThreadLocal[variablesToRemove.size()]);
                for (FastThreadLocal<?> tlv: variablesToRemoveArray) {
                    tlv.remove(threadLocalMap);
                }
            }
        } finally {
            // 清理掉绑定在当前线程上的threadLocalMap属性
            InternalThreadLocalMap.remove();
        }
    }

    /**
     * Returns the number of thread local variables bound to the current thread.
     */
    public static int size() {
        InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
        if (threadLocalMap == null) {
            return 0;
        } else {
            return threadLocalMap.size();
        }
    }

    /**
     * Destroys the data structure that keeps all {@link FastThreadLocal} variables accessed from
     * non-{@link FastThreadLocalThread}s.  This operation is useful when you are in a container environment, and you
     * do not want to leave the thread local variables in the threads you do not manage.  Call this method when your
     * application is being unloaded from the container.
     */
    public static void destroy() {
        InternalThreadLocalMap.destroy();
    }

    /**
     * 添加本实例到 variablesToRemove 集合
     * @param threadLocalMap 当前线程绑定的InternalThreadLocalMap对象
     * @param variable       当前需要添加到 variablesToRemove 集合的 FastThreadLocal 对象
     */
    @SuppressWarnings("unchecked")
    private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
        // 获取variablesToRemove集合
        Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
        Set<FastThreadLocal<?>> variablesToRemove;
        if (v == InternalThreadLocalMap.UNSET || v == null) {
            // 初始化一个 variablesToRemove 集合 并存放到 threadLocalMap 的 variablesToRemoveIndex 位置
            variablesToRemove = Collections.newSetFromMap(new IdentityHashMap<>());
            threadLocalMap.setIndexedVariable(variablesToRemoveIndex, variablesToRemove);
        } else {
            variablesToRemove = (Set<FastThreadLocal<?>>) v;
        }
        // 存放到集合中
        variablesToRemove.add(variable);
    }

    /**
     * 将 FastThreadLocal 对象从 variablesToRemove 集合中移除
     * @param threadLocalMap 当前线程绑定的InternalThreadLocalMap对象
     * @param variable       当前需要从 variablesToRemove 集合中移除的 FastThreadLocal 对象
     */
    private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
        Object v = threadLocalMap.indexedVariable(variablesToRemoveIndex);
        // 如果集合不存在，则直接返回
        if (v == InternalThreadLocalMap.UNSET || v == null) {
            return;
        }

        // 否则调用 variablesToRemove 的remove方法移除对象
        @SuppressWarnings("unchecked")
        Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>) v;
        variablesToRemove.remove(variable);
    }

    /**
     * 全局唯一的ID值，主要用于快速定位InternalThreadLocalMap.indexedVariables的索引，并存放此对象
     */
    private final int index;

    public FastThreadLocal() {
        // 生成全局唯一的ID值
        index = InternalThreadLocalMap.nextVariableIndex();
    }

    /**
     * 获取线程本地变量，没有就初始化一个值再返回，并把本FastThreadLocal实例加入variablesToRemoveindex索引的variablesToRemove集合
     */
    public final V get() {
        return get(InternalThreadLocalMap.get());
    }

    /**
     * Returns the current value for the specified thread local map.
     * The specified thread local map must be for the current thread.
     * <br>
     * 从 threadLocalMap 获取线程本地变量，threadLocalMap 必须属于当前线程
     */
    @SuppressWarnings("unchecked")
    public final V get(InternalThreadLocalMap threadLocalMap) {
        //用本 FastThreadLocal 实例的 index 去 indexedVariables 数组中取数据
        Object v = threadLocalMap.indexedVariable(index);
        if (v != InternalThreadLocalMap.UNSET) {
            // 非占位符数据，返回
            return (V) v;
        }

        // 如果未成功获取到，则初始化
        return initialize(threadLocalMap);
    }

    private V initialize(InternalThreadLocalMap threadLocalMap) {
        V v = null;
        try {
            // 调用子类实现初始化一个值或null
            v = initialValue();
        } catch (Exception e) {
            PlatformDependent.throwException(e);
        }

        // 放入 indexedVariables 数组
        threadLocalMap.setIndexedVariable(index, v);
        // 添加本实例到 variablesToRemove 集合
        addToVariablesToRemove(threadLocalMap, this);
        return v;
    }

    /**
     * Set the value for the current thread.
     *
     * 设置 value 到 当前线程的 threadLocalMap 属性中
     */
    public final void set(V value) {
        if (value != InternalThreadLocalMap.UNSET) {
            set(InternalThreadLocalMap.get(), value);
        } else {
            remove();
        }
    }

    /**
     * 设置指定线程本地映射的值。指定的线程本地映射必须是当前线程的。
     */
    public final void set(InternalThreadLocalMap threadLocalMap, V value) {
        // 判断是移除还是新增
        if (value != InternalThreadLocalMap.UNSET) {
            // 设置当前是新增，则需要将此值添加到 variablesToRemove 集合中。否则直接替换 indexedVariables 索引上的项即可
            if (threadLocalMap.setIndexedVariable(index, value)) {
                addToVariablesToRemove(threadLocalMap, this);
            }
        } else {
            remove(threadLocalMap);
        }
    }

    /**
     * Returns {@code true} if and only if this thread-local variable is set.
     */
    public final boolean isSet() {
        return isSet(InternalThreadLocalMap.getIfSet());
    }

    /**
     * Returns {@code true} if and only if this thread-local variable is set.
     * The specified thread local map must be for the current thread.
     */
    public final boolean isSet(InternalThreadLocalMap threadLocalMap) {
        return threadLocalMap != null && threadLocalMap.isIndexedVariableSet(index);
    }
    /**
     * 将值设置为未初始化；对 get() 的后续调用将触发对 initialValue() 的调用。
     */
    public final void remove() {
        remove(InternalThreadLocalMap.getIfSet());
    }

    /**
     * 将指定线程本地映射的值设置为未初始化；
     * 对 get() 的后续调用将触发对 initialValue() 的调用。
     * 指定的线程本地映射必须是当前线程的。
     * @param threadLocalMap 当前线程绑定的 threadLocalMap 对象
     */
    @SuppressWarnings("unchecked")
    public final void remove(InternalThreadLocalMap threadLocalMap) {
        if (threadLocalMap == null) {
            return;
        }

        // 从指定的索引处移除此 FastThreadLocal 对象
        Object v = threadLocalMap.removeIndexedVariable(index);
        // 从 variablesToRemove 集合中移除此 FastThreadLocal 对象
        removeFromVariablesToRemove(threadLocalMap, this);

        // 如果v是正常值，则触发onRemoval回调
        if (v != InternalThreadLocalMap.UNSET) {
            try {
                onRemoval((V) v);
            } catch (Exception e) {
                PlatformDependent.throwException(e);
            }
        }
    }

    /**
     * 返回此线程局部变量的初始值。
     */
    protected V initialValue() throws Exception {
        return null;
    }

    /**
     * 当此线程局部变量被 {@link #remove()} 删除时调用。
     */
    protected void onRemoval(@SuppressWarnings("UnusedParameters") V value) throws Exception { }
}
