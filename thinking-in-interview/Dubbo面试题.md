### 一、Dubbo基础知识

#### 2.Dubbo中有哪些角色？

***Registry***

注册中心. 是用于发布和订阅服务的一个平台.用于替代SOA结构体系框架中的ESB服务总线的。
订阅服务的时候, 会将发布的服务所有信息,一次性下载到客户端。
客户端也可以自定义, 修改部分服务配置信息. 如: 超时的时长, 调用的重试次数等。

***Consumer***

服务的消费者, 就是服务的客户端。消费者必须使用Dubbo技术开发部分代码. 基本上都是配置文件定义。

***Provider***

服务的提供者, 就是服务端。服务端必须使用Dubbo技术开发部分代码. 以配置文件为主。

***Container***

ubbo技术的服务端(Provider), 在启动执行的时候, 必须依赖容器才能正常启动。默认依赖的就是spring容器. 且Dubbo技术不能脱离 Spring框架。

***Monitor***

监控中心是Dubbo提供的一个jar工程。主要功能是监控服务端(Provider)和消费端(Consumer)的使用数据的. 如: 服务端是什么,有多少接口,多少方法, 调用次数, 压力信息等. 客户端有多少, 调用过哪些服务端, 调用了多少次等。

#### 2.Dubbo支持哪几种协议？

>- dubbo 协议 (默认)
>- rmi 协议
>- hessian 协议
>- http 协议
>- webservice 协议
>- thrift 协议
>- memcached 协议
>- redis 协议

***dubbo协议***

>- 连接个数：单连接
>- 连接方式：长连接
>- 传输协议：TCP
>- 传输方式：NIO异步传输
>- 序列化：Hessian 二进制序列化
>- 适用范围：传入传出参数数据包较小（建议小于100K），消费者比提供者个数多，单一消费者无法压满提供者，尽量不要用dubbo协议传输大文件或超大字符串。
>- 适用场景：常规远程服务方法调用

dubbo默认采用dubbo协议，dubbo协议采用单一长连接和NIO异步通讯，适合于小数据量大并发的服务调用，以及服务消费者机器数远大于服务提供者机器数的情况。
他不适合传送大数据量的服务，比如传文件，传视频等，除非请求量很低。

配置方式如下：
```
<dubbo:protocol name="dubbo" port="20880" />
<!-- Set default protocol: -->
<dubbo:provider protocol="dubbo" />
<!-- Set service protocol -->
<dubbo:service protocol="dubbo" />
<!-- Multi port -->
<dubbo:protocol id="dubbo1" name="dubbo" port="20880" />
<dubbo:protocol id="dubbo2" name="dubbo" port="20881" />.
<!-- Dubbo protocol options: -->
<dubbo:protocol name="dubbo" port="9090" server="netty" client="netty" codec="dubbo" serialization="hessian2" charset="UTF-8" threadpool="fixed" threads="100" queues="0" iothreads="9" buffer="8192" accepts="1000" payload="8388608" />
```

Dubbo协议缺省每服务每提供者每消费者使用单一长连接，如果数据量较大，可以使用多个连接。
```
<dubbo:protocol name="dubbo" connections="2" />
<dubbo:service connections="0" />或<dubbo:reference connections="0" />//表示该服务使用JVM共享长连接。(缺省) 
<dubbo:service connections="1" />或<dubbo:reference connections="1" />//表示该服务使用独立长连接。 
<dubbo:service connections="2" />或<dubbo:reference connections="2" />//表示该服务使用独立两条长连接。
```

为防止被大量连接撑挂，可在服务提供方限制大接收连接数，以实现服务提供方自我保护
```
<dubbo:protocol name="dubbo" accepts="1000" />
```

***rmi协议***

>- 连接个数：多连接
>- 连接方式：短连接
>- 传输协议：TCP
>- 传输方式：同步传输
>- 序列化：Java标准二进制序列化
>- 适用范围：传入传出参数数据包大小混合，消费者与提供者个数差不多，可传文件
>- 适用场景：常规远程服务方法调用，与原生RMI服务互操作

RMI协议采用JDK标准的java.rmi.*实现，采用阻塞式短连接和JDK标准序列化方式。如果服务接口继承了java.rmi.Remote接口，可以和原生RMI互操作。如果服务接口没有继承java.rmi.Remote接口，缺省Dubbo将自动生成一个com.xxx.XxxService$Remote的接口，并继承java.rmi.Remote接口，并以此接口暴露服务。但如果设置了`<dubbo:protocol name="rmi" codec="spring" />`，将不生成$Remote接口，而使用Spring的RmiInvocationHandler接口暴露服务，和Spring兼容。

```
<!-- Define rmi protocol -->
<dubbo:protocol name="rmi" port="1099" />.
<!-- Set default protocol: -->
<dubbo:provider protocol="rmi" />
<!-- Set service protocol: -->
<dubbo:service protocol="rmi" />
<!-- Multi port -->
<dubbo:protocol id="rmi1" name="rmi" port="1099" />
<dubbo:protocol id="rmi2" name="rmi" port="2099" />
<dubbo:service protocol="rmi1" />
<!-- Spring compatible: -->
<dubbo:protocol name="rmi" codec="spring" />
```

***hessian协议***

>- 连接个数：多连接
>- 连接方式：短连接
>- 传输协议：HTTP
>- 传输方式：同步传输
>- 序列化：Hessian二进制序列化
>- 适用范围：传入传出参数数据包较大，提供者比消费者个数多，提供者压力较大，可传文件
>- 适用场景：页面传输，文件传输，或与原生hessian服务互操作

Hessian协议用于集成Hessian的服务，Hessian底层采用Http通讯，采用Servlet暴露服务，Dubbo缺省内嵌Jetty作为服务器实现，可以和原生Hessian服务互操作。

注意点：
>- 参数及返回值需实现Serializable接口。
>- 参数及返回值不能自定义实现List, Map, Number, Date, Calendar等接口，只能用JDK自带的实现，因为hessian会做特殊处理，自定义实现类中的属性值都会丢失。

```
<!-- Define hessian protocol: -->
<dubbo:protocol name="hessian" port="8080" server="jetty" />
<!-- Set default protocol: -->
<dubbo:provider protocol="hessian" />
<!-- Set service protocol: -->
<dubbo:service protocol="hessian" />
<!-- Multi port: -->
<dubbo:protocol id="hessian1" name="hessian" port="8080" />
<dubbo:protocol id="hessian2" name="hessian" port="8081" />
<!-- Directly provider: -->
<dubbo:reference id="helloService" interface="HelloWorld" url="hessian://10.20.153.10:8080/helloWorld" />
<!-- Jetty Server -->
<dubbo:protocol ... server="jetty" />
<!-- Servlet Bridge Server -->
<dubbo:protocol ... server="servlet" />
```

***http协议***

>- 连接个数：多连接
>- 连接方式：短连接
>- 传输协议：HTTP
>- 传输方式：同步传输
>- 序列化：表单序列化
>- 适用范围：传入传出参数数据包大小混合，提供者比消费者个数多，可用浏览器查看，可用表单或URL传入参数，暂不支持传文件
>- 适用场景：需同时给应用程序和浏览器JS使用的服务

```
<dubbo:protocol name="http" port="8080" />
<!-- Jetty Server -->
<dubbo:protocol ... server="jetty" />
<!-- Servlet Bridge Server -->
<dubbo:protocol ... server="servlet" />
```

注意:如果使用 servlet 派发请求 
协议的端口`<dubbo:protocol port="8080" />`必须与servlet容器的端口相同，协议的上下文路径`<dubbo:protocol contextpath="foo" />`必须与servlet应用的上下文路径相同。

***webservice协议***

>- 连接个数：多连接
>- 连接方式：短连接
>- 传输协议：HTTP
>- 传输方式：同步传输
>- 序列化：SOAP文本序列化
>- 适用场景：系统集成，跨语言调用

***thrift协议***

Thrift是Facebook捐给Apache的一个RPC框架，当前 dubbo 支持的 thrift 协议是对 thrift 原生协议的扩展，在原生协议的基础上添加了一些额外的头信息，比如service name，magic number等。

***memcached协议***

可以通过脚本或监控中心手工填写表单注册memcached服务的地址。

***redis协议***

可以通过脚本或监控中心手工填写表单注册redis服务的地址。

#### 3.Dubbo有几种集群容错模式？

目前Dubbo主要支持6种模式：Failover、Failfast、Failsafe、Failback、Forking和Broadcast。

配置如下：
```
//服务提供者配置
<dubbo:service cluster="failfast" />
//服务消费者配置
<dubbo:reference cluster="failfast" />
```

***1.Failover Cluster模式***

配置值为failover，是Dubbo集群容错默认选择的模式，在调用失败时会自动切换，重新尝试调用其他节点上可用的服务。一些幂等操作可以使用该模式，对调用者完全透明。可通过`retries`属性来设置重试次数，配置方式有以下几种：
```
//服务器提供者一方配置重试次数
<dubbo:service retries="2" />
//服务消费者一方配置重试次数
<dubbo:reference retries="2" />
//还可以在方法级别上配置重试次数
<dubbo:reference>
	<dubbo:method name="sayHello" retries="2" />
</dubbo:refernce>
```

***2.Failfast Cluster模式***

配置值为failfast，又叫做快速失败模式，调用只执行依次，如失败则立即报错。这种模式适用于非幂等性操作。

***3.Failsafe Cluster模式***

配置值为failsafe，又叫做失败安全模式，如果调用失败，则直接忽略失败的调用，记录失败的调用到日志文件中，以便后续查询。

***4.Failback Cluster模式***

配置值为failback。在失败后自动恢复，后台记录失败的请求，定时重发。通常用于消息通知操作。

***5.Forking Cluster模式***

配置值为forking。并行调用多个服务器，只要一个成功便返回。通常用于实时性要求较高的读操作，但需要浪费更多的服务资源。可通过`forks`属性来设置最大的并行数。

***6.Broadcast Cluster模式***

配置值为broadcast。广播低矮用所有提供者，逐个调用，任意一台报错则报错。通常用于通知所有提供者更新缓存或日志等本地资源信息。

#### 4.Dubbo的负载均衡策略

Dubbo框架内置了负载均衡的功能及扩展接口，我们可以透明地扩展一个服务或服务集群，根据需要能非常容易地增加或移除节点，提供服务的可伸缩性。Dubbo内置了4种负载均衡策略：***随机(Random)***、***轮询(RoundRobin)***、***最少活跃调用数(LeastActive)***、***一致性Hash(ConsistentHash)***。

***1)Random LoadBalance***

随机，按权重设置随机概率。在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。(权重可以在Dubbo管控台配置)

***2)RoundRobin LoadBalance***

轮循，按公约后的权重设置轮循比率。存在慢的提供者累积请求问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

***3)LeastActive LoadBalance***

最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

***4)ConsistentHash LoadBalance***

一致性Hash，相同参数的请求总是发到同一提供者。当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。缺省只对第一个参数Hash，如果要修改，请配置。

#### 5.Dubbo支持的注册中心有哪些？

***1) Zookeeper***

- 优点:支持分布式.很多周边产品；
- 缺点: 受限于Zookeeper软件的稳定性.Zookeeper专门分布式辅助软件,稳定较优。

***2)Multicast***

- 优点:去中心化,不需要单独安装软件。
- 缺点:Provider和Consumer和Registry不能跨机房(路由)。

***3)Redis***

- 优点:支持集群,性能高。
- 缺点:要求服务器时间同步.否则可能出现集群失败问题。

***4)Simple***

- 优点: 标准RPC服务.没有兼容问题。
- 缺点: 不支持集群。

#### 6.Dubbo中ZK作为注册中心，如果注册中心集群挂掉了，发布者和订阅者之间还能通信吗？

可以的，启动Dubbo时，消费者会从zk拉取注册的生产者的地址接口等数据，缓存在本地。每次调用时，按照本地存储的地址进行调用。

- 注册中心对等集群，任意一台宕掉后，会自动切换到另一台。
- 注册中心全部宕掉，服务提供者和消费者仍可以通过本地缓存通讯。
- 服务提供者无状态，任一台 宕机后，不影响使用。
- 服务提供者全部宕机，服务消费者会无法使用，并无限次重连等待服务者恢复。


### 二、注意事项和建议

#### 1.Dubbo协议的使用注意事项

***(1) 在实际情况下消费者的数据比提供者的数量要多。***

因为Dubbo协议采用单一长连接，假设网络为千兆网卡，则根据测试经验，数据的每条连接最多只能压满7M（在不同的环境下可能不一样）。所以理论上1个服务提供者需要20个服务消费者才能压满网卡。

***(2)不能传大的数据包***

因为Dubbo协议采用单一长连接，所以如果每次请求的数据包为500KB，假设网络为千兆网卡，则每条连接最大7M，单个服务提供者的TPS（每秒处理的事务数）最大为：128M / 500K = 262。单个消费者调用单个服务提供者的TPS最大为：7M / 500K = 14，如果能接受，则可以考虑使用，否则网络将成为瓶颈。

***(3)推荐使用异步单一长连接方式***

因为服务的现状大多是服务提供者少，通常只要几台机器，而服务消费者多，可能整个网站都在访问该服务。如果采用常规的Hessian服务，则服务提供者很容易就被压垮，而通过单一连接，可保证单一消费者不会压垮提供者。长连接可以减少连接握手验证等，并且使用异步I/O，可以复用线程池，防止出现C10K问题。

#### 2.关于Dubbo线程有以下几条建议

(1)在消费者和提供者之间默认只会建立一条TCP长连接。为了增加消费者调用服务提供者的吞吐量，也可以在消费者的`<dubbo:reference/>`中配置`connections`来单独增加消费者和服务提供者的TCP长连接。但线上业务由于有多个消费者和多个提供者，因为不建议增加`connections`参数。

(2)在服务连接成功后，具体的请求会交给I/O线程处理。由于I/O线程是异步读写数据的，所以它消耗更多的是CPU资源，因此I/O线程数(`iothreads`属性)默认为CPU的个数+1比较合理，不建议调整此参数。

(3)数据在被读取并反序列化后，会被交给业务线程池处理，在默认情况下线程池为固定的大小，并且在线程池满时排队等待执行的队列大小为0，所以它的最大并发量等于业务线程池的大小。但是，如果希望有请求的堆积能力，则可以调整`queues`属性来设置队列的大小。一般建议不要设置，因为在线程池满时应该立即失效，再自动重试其它服务提供者，而不是排队。

### 三、故障排除

### 四、源码和原理

### 五、面试连珠炮解析

未完待续