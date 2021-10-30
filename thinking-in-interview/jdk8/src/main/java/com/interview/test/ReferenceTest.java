package com.interview.test;

import com.google.common.collect.Lists;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;

//-Xmx50M -Xms50M -XX:+PrintGCDetails -Dreference.type=phantom/weak/soft
//会出现什么情况，问为什么？那如何解决呢？
public class ReferenceTest {

    private static final int _M = 1024 * 1024;

    public static void main(String[] args) {
        String referenceType = System.getProperty("reference.type");
        System.out.println("referenceType = " + referenceType);
        if("soft".equals(referenceType)) {
            makeSoftReferenceOOM(50);
        } else if("weak".equals(referenceType)) {
            makeWeakReferenceOOM(50);
        } else {
            makePhantomReferenceOOM(50);
        }
    }

    private static void makeSoftReferenceOOM(int c) {
        List<Reference<byte[]>> list = Lists.newArrayListWithCapacity(c);
        for(int i=0;i<c;i++) {
            list.add(new SoftReference<>(new byte[_M]));
        }
        System.out.println("soft reference finished, list size = " + list.size());
    }

    private static void makeWeakReferenceOOM(int c) {
        List<Reference<byte[]>> list = Lists.newArrayListWithCapacity(c);
        for(int i=0;i<c;i++) {
            list.add(new WeakReference<>(new byte[_M]));
        }
        System.out.println("soft reference finished, list size = " + list.size());
    }

    private static void makePhantomReferenceOOM(int c) {
        List<Reference<byte[]>> list = Lists.newArrayListWithCapacity(c);
        for(int i=0;i<c;i++) {
            list.add(new PhantomReference<>(new byte[_M], null));
        }
        System.out.println("phantom reference finished, list size = {}" + list.size());
    }

}
