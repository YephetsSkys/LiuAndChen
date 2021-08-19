#### 1.spring 如何解决循环依赖？

>- https://zhuanlan.zhihu.com/p/84267654
>- https://www.jianshu.com/p/8bb67ca11831


下面是循环依赖的代码流程图片
![avatar](img/Spring解决循环依赖.png)


#### 2.spring 事务是怎么实现的？

>- https://www.jianshu.com/p/2449cd914e3c
>- https://www.cnblogs.com/zengweiming/p/8853144.html

#### 3.spring 依赖使用到三级缓存分别是做什么用？

一级缓存`singletonObjects`存储的对象是完全创建好，可以正常使用的bean。

二级缓存`earlySingletonObjects`缓存的是从三级缓存中获取到的bean，这些bean是仅执行了通过构造方法实例化，并没有填充属性和初始化。

三级缓存`singletonFactories`，缓存一个objectFactory工厂。

#### 4.spring 为什么要使用第三级缓存解决循环依赖？

**为了解决代理对象（如aop）循环依赖的问题**

例： a依赖b,b依赖a，同时a,b都被aop增强。

首先明确aop的实现是通过 postBeanProcess后置处理器，在初始化之后做代理操作的。

为什么使用三级缓存原因：

> 1.只使用二级缓存，且二级缓存缓存的是一个不完整的bean

如果只使用二级缓存，且二级缓存缓存的是一个不完整的bean，这个时候a在设置属性的过程中去获取b（这个时候a还没有被aop的后置处理器增强），创建b的过程中，b依赖a，b去缓存中拿a拿到的是没有经过代理的a。就有问题。

> 2.使用二级缓存，且二级缓存是一个工厂方法的缓存

a依赖b，b依赖a，c。c又依赖a。a,b，c均aop增强。

加载开始： a实例化，放入工厂缓存，设置b，b实例化，设置属性，拿到a,此时从工厂缓存中拿到代理后的a。由于a没加载完毕，不会放入一级缓存。这个时候b开始设置c,c实例化，设置属性a,又去工厂缓存中拿对象a。这个时候拿到的a和b从工厂缓存不是一个对象。出现问题。

> 3.使用二级缓存，二级缓存缓存的是增强后的bean

这个与spring加载流程不符合。spring加载流程是：实例化，设置属性，初始化，增强。在有循环引用的时候，之前的bean并不会增强后放入到二级缓存。

***综上1，2，3 可知二级缓存解决不了有aop的循环依赖。spring采用了三级缓存。***

如果使用三级缓存为工厂缓存的话：

场景：a依赖b，b依赖a和c，c依赖a。并且a，b，c都aop增强。

a实例化，放入三级工厂缓存，设置属性b，b实例化放入三级缓存。b设置属性a，从三级工厂缓存中获取代理后的对象a，同时，代理后的a放入二级缓存，然后设置属性c，c实例化放入三级缓存，设置属性a,此时从二级缓存中获取到的代理后的a跟b中的a是一个对象，属性a设置成功。c初始化，然后执行后置处理器。进行aop的增强。增强后将代理的c放入到一级缓存，同时删除三级缓存中的c。c加载完成，b得到c，b设置c成功。b初始化，然后执行后置处理器，进行aop增强，将增强后的代理对象b放入到一级缓存。删除三级缓存中的b。此时 a拿到b，设置属性b成功，开始初始化，初始化后执行后置处理器。

```
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
    // 从一级缓存中获取
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
        synchronized (this.singletonObjects) {
            // 从二级缓存中获取未初始化完全的实体
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                // 从三级缓存中获取工厂类，进行类初始化，成功后设置到二级缓存中，并从三级缓存中删除掉
                ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    this.earlySingletonObjects.put(beanName, singletonObject);
                    this.singletonFactories.remove(beanName);
                }
            }
        }
    }
    return (singletonObject != NULL_OBJECT ? singletonObject : null);
}
```

