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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The internal data structure that stores the thread-local variables for Netty and all {@link FastThreadLocal}s.
 * Note that this class is for internal use only and is subject to change at any time.  Use {@link FastThreadLocal}
 * unless you know what you are doing.
 */
class UnpaddedInternalThreadLocalMap {

    /**
     * 如果在 Thread 中使用 FastThreadLocal, 则实际上使用 ThreadLocal 存放资源.
     */
    static final ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal<>();

    /**
     * 资源索引, 每一个 FastThreadLocal 对象都会有对应的ID, 即通过 nextIndex 自增得到.
     */
    static final AtomicInteger nextIndex = new AtomicInteger();

    /**
     * Used by {@link FastThreadLocal}，直接使用数组避免了hash冲突的发生。
     * 每一个FastThreadLocal实例创建时，分配一个下标index；分配index使用AtomicInteger实现，每个FastThreadLocal都能获取到一个不重复的下标。
     * 默认数组大小为32，并且数组中的所有值初始化为 InternalThreadLocalMap.UNSET
     */
    Object[] indexedVariables;

    // Core thread-locals
    int futureListenerStackDepth;
    int localChannelReaderStackDepth;
    Map<Class<?>, Boolean> handlerSharableCache;
    IntegerHolder counterHashCode;
    /**
     * 可以有效的减少多线程之间的随机对象中的种子多次CAS重试，这是会降低并发性能的。
     */
    ThreadLocalRandom random;
    Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache;
    Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache;

    // String-related thread-locals
    StringBuilder stringBuilder;
    Map<Charset, CharsetEncoder> charsetEncoderCache;
    Map<Charset, CharsetDecoder> charsetDecoderCache;

    // ArrayList-related thread-locals
    ArrayList<Object> arrayList;

    UnpaddedInternalThreadLocalMap(Object[] indexedVariables) {
        this.indexedVariables = indexedVariables;
    }
}
