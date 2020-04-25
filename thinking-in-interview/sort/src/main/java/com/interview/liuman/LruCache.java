package com.interview.liuman;

import java.util.HashMap;

/**
 * 双向链表知识点: https://blog.csdn.net/varyall/article/details/82319319
 * @Author: liuman
 * @Date: 2020-04-23 09:47
 * @Descript: 使用双向链表实现lru
 * LRUCache原理:
 * A cache that holds strong references to a limited number of values.
 * Each time a value is accessed, it is moved to the head of a queue.
 * When a value is added to a full cache,
 * the value at the end of that queue is evicted and may become eligible for garbage collection.
 * 一个包含有限数量强引用的缓存，每次访问一个值，它都会被移动到队列的头部，
 * 将一个新的值添加到已经满了的缓存队列时，该队列末尾的值将会被逐出，
 * 并且可能会被垃圾回收机制进行回收。
 *
 * 作者：kevenZheng
 * 链接：https://www.jianshu.com/p/8215107977a9
 * 来源：简书
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 *
 */
public class LruCache {

    HashMap<Integer,Node> map;
    //缓存中的最大容量
    int capicity;
    //缓存当前的容量
    int count;
    //头结点
    Node head;
    //尾节点
    Node tail;

    public LruCache(int capicity) {
        this.capicity = capicity;
        map = new HashMap<>();
        head = new Node(0,0);
        tail = new Node(0,0);
        head.next = tail;
        tail.pre = head;
        head.pre = null;
        tail.next = null;
        count = 0;
    }

    public void deleteNode(Node node) {
        node.pre.next = node.next;
        node.next.pre = node.pre;
    }

    public void addToHead(Node node) {
        node.next = head.next;
        node.next.pre = node;
        node.pre = head;
        head.next = node;
    }

    public int get(int key) {
        if (map.get(key) != null) {
            Node node = map.get(key);
            int result = node.value;
            //当前要使用该节点数据则需要放到头部
            deleteNode(node);
            addToHead(node);
            return result;
        }

        return -1;
    }

    public void put(int key, int value) {
        //缓存中如果有该值的话
        if (map.get(key) != null) {
            Node node = map.get(key);
            //覆盖原有节点的value
            node.value = value;
            deleteNode(node);
            addToHead(node);
        } else {
            Node node = new Node(key,value);
            map.put(key,node);
            if (count < capicity) {
                count++;
                //放到队列前面
                addToHead(node);
            } else {//如果队列已满，则需要删除队尾元素
                map.remove(tail.pre.key);
                deleteNode(tail.pre);
                //最新元素放入到队头
                addToHead(node);
            }
        }
    }
}

class Node {
    int key;
    int value;
    Node pre = null;
    Node next = null;

    public Node(int key,int value) {
        this.key = key;
        this.value = value;
    }
}
