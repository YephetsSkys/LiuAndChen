### 一、Dubbo基础知识

#### 1.Dubbo中有哪些角色？

***Registry***

注册中心. 是用于发布和订阅服务的一个平台.用于替代SOA结构体系框架中的ESB服务总线的。
订阅服务的时候, 会将发布的服务所有信息,一次性下载到客户端。
客户端也可以自定义, 修改部分服务配置信息. 如: 超时的时长, 调用的重试次数等。

***Consumer***

服务的消费者, 就是服务的客户端。消费者必须使用Dubbo技术开发部分代码. 基本上都是配置文件定义。

***Provider***

服务的提供者, 就是服务端。服务端必须使用Dubbo技术开发部分代码. 以配置文件为主。

***Container***

Dubbo技术的服务端(Provider), 在启动执行的时候, 必须依赖容器才能正常启动。默认依赖的就是spring容器. 且Dubbo技术不能脱离 Spring框架。

***Monitor***

监控中心是Dubbo提供的一个jar工程。主要功能是监控服务端(Provider)和消费端(Consumer)的使用数据的. 如: 服务端是什么,有多少接口,多少方法, 调用次数, 压力信息等. 客户端有多少, 调用过哪些服务端, 调用了多少次等。

#### 2.介绍一下Dubbo的框架设计分层

Dubbo的框架设计一共划分了10层，各层均为单向依赖，每一层都可以剥离上层被复用，其中，Service和Config层为API，其他各层均为SPI。

接下来分别介绍在框架分层架构中各个层次的设计要点：
- ***服务接口层(Service)：***该层是与实际业务逻辑相关的，根据服务提供者和服务消费者的业务设计对应的接口和实现。
- ***配置层(Config)：***对外的配置接口，以ServiceConfig和ReferenceConfig为中心，可以直接创建(new)一个配置类对象，也可以通过Spring解析配置生成配置类对象。
- ***服务代理层(Proxy)：***服务接口的透明代理，生成服务的客户端Stub和服务器端Skeleton，以ServiceProxy为中心，扩展接口为ProxyFactory。
- ***服务注册层(Registry)：***封装服务地址的注册与发现，以服务URL为中心，扩展接口为RegistryFactory、Registry和RegistryService。可能没有服务注册中心，此时服务提供者直接暴露服务。
- ***集群层(Cluster)：***封装多个提供者的路由及负载均衡，并桥接注册中心，以Invoker为中心，扩展接口为Cluster、Directory、Router和LoadBalance。将多个服务提供者组合为一个服务提供者，实现对服务消费者透明，只需与一个服务提供者进行交互。
- ***监控层(Monitor)：***RPC调用的次数和调用时间的监控，以Statistice为中心，扩展接口为MonitorFactory、Monitor和MonitorService。
- ***远程调用层(Protocol)：***封装RPC调用，以Invocation和Result为中心，扩展接口为Protocol、Invoker和Exporter。Protocol是服务域，是Dubbo的核心模型，其他模型都向它靠拢或转换成它，它代表一个可执行体，可向它发起invoke调用，它有可能是一个本地的实现，也可能是一个远程的实现，也可能是一个集群实现。
- ***信息交换层(Exchange)：***封装请求相应模式，同步转异步，以Request和Response为中心，扩展接口为Exchange、ExchangeChannel、ExchangeClient和ExchangeServer。
- ***网络传输层(Transport)：***抽象mina和netty为统一接口，以Message为中心，扩展接口为Channel、Transporter、Client、Server和Codec。
- ***数据序列化层(Serialize)：***可服用的一些工具，扩展接口为Serialization、ObjectInput、ObjectOutput和ThreadPool。

#### 3.Dubbo支持哪几种协议？

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

#### 4.Dubbo支持哪些序列化方式？

dubbo 支持 hession、Java 二进制序列化、json、SOAP 文本序列化多种序列化协议。但是 hessian 是其默认的序列化协议。

#### 5.Dubbo有几种集群容错模式？

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

#### 6.Dubbo的负载均衡策略

Dubbo框架内置了负载均衡的功能及扩展接口，我们可以透明地扩展一个服务或服务集群，根据需要能非常容易地增加或移除节点，提供服务的可伸缩性。Dubbo内置了4种负载均衡策略：***随机(Random)***、***轮询(RoundRobin)***、***最少活跃调用数(LeastActive)***、***一致性Hash(ConsistentHash)***。

***1)Random LoadBalance***

随机，按权重设置随机概率。在一个截面上碰撞的概率高，但调用量越大分布越均匀，而且按概率使用权重后也比较均匀，有利于动态调整提供者权重。(权重可以在Dubbo管控台配置)

***2)RoundRobin LoadBalance***

轮循，按公约后的权重设置轮循比率。存在慢的提供者累积请求问题，比如：第二台机器很慢，但没挂，当请求调到第二台时就卡在那，久而久之，所有请求都卡在调到第二台上。

***3)LeastActive LoadBalance***

最少活跃调用数，相同活跃数的随机，活跃数指调用前后计数差。使慢的提供者收到更少请求，因为越慢的提供者的调用前后计数差会越大。

***4)ConsistentHash LoadBalance***

一致性Hash，相同参数的请求总是发到同一提供者。当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。缺省只对第一个参数Hash，如果要修改，请配置。

#### 7.Dubbo支持的注册中心有哪些？

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

#### 8.Dubbo中ZK作为注册中心，如果注册中心集群挂掉了，发布者和订阅者之间还能通信吗？

可以的，启动Dubbo时，消费者会从zk拉取注册的生产者的地址接口等数据，缓存在本地。每次调用时，按照本地存储的地址进行调用。

- 注册中心对等集群，任意一台宕掉后，会自动切换到另一台。
- 注册中心全部宕掉，服务提供者和消费者仍可以通过本地缓存通讯。
- 服务提供者无状态，任一台 宕机后，不影响使用。
- 服务提供者全部宕机，服务消费者会无法使用，并无限次重连等待服务者恢复。

#### 9.服务暴露的一些特殊配置

如果服务需要预热的时间，比如初始化缓存、等待相关资源就位等，就可以使用delay属性进行服务延迟暴露：
```
<!-- 延迟5秒暴露服务 -->
<dubbo:service delay="5000" />
<!-- 或者设置为-1，表示延迟到Spring初始化完成后再暴露服务 -->
<dubbo:service delay="-1" />
```

如果一个服务的并发量过大，超出了服务器的承载能力，那么可以使用`executes`属性控制并发。
```
<!-- 服务端限制接口的每个方法，并发不能超过10个 -->
<dubbo:service interface="xxx.IHelloService" executes="10" />
<!-- 服务端限制指定方法的并发 -->
<dubbo:service interface="xxx.IHelloService">
	<dubbo:method name="sayHello" executes="10" />
</dubbo:service>
<!-- 客户端同样也可以实现并发控制(可以在客户端指定Service级别，接口级别或者方法级别) -->
<dubbo:reference interface="xxx.IHelloService" actives="10" />
```

未了保障服务的稳定性，除了限制并发线程数外，还可以限制服务端的连接数：
```
<!-- 限制服务端的连接数不能超过10个 -->
<dubbo:provider protocol="dubbo" accepts="10" />
<!-- 或者 -->
<dubbo:protocol name="dubbo" accepts="10" />
<!-- 同样，也可以限制客户端的使用连接数 -->
<dubbo:reference interface="xxx.IHelloService" connections="10" />
<!-- 或者 -->
<dubbo:service interface="xxx.IHelloService" connections="10" />
<!-- 如果service和reference都配置了connections，则reference优先，由于服务提供者了解自身的承载能力，所以推荐让服务提供者控制连接数 -->
```

除了常用的控制并发、控制连接数、服务隔离也是非常重要的一项措施。服务隔离是为了在系统发生故障时限定传播范围和影响范围，从而保证只有出问题的服务不可用，其他服务还是正常的。隔离一般有线程隔离、进程隔离、读写隔离、集群隔离和机房隔离，而dubbo还提供了分组隔离，即使用group属性分组：
```
<!-- 将服务提供者分组 -->
<dubbo:service group="login_wx" interface="com.bill.login" />
<dubbo:service group="login_fb" interface="com.bill.login" />
<!-- 或者将服务消费者分组 -->
<dubbo:reference id="wxLogin" group="login_wx" interface="com.bill.login" />
<dubbo:reference id="fbLogin" group="login_fb" interface="com.bill.login" />
```

#### 10.服务的异步调用

默认情况下消费者使用同步方式进行远程调用，如果想使用异步方式，则可以设置`async`属性为true，并使用Feture获取返回值：
```
<!-- 在配置中设置异步调用方法 -->
<dubbo:reference id="helloService" interface="xxx.IHelloService">
	<dubbo:method name="sayHello" async="true" />
</dubbo:reference>
```

```
//如何使用
IHelloService helloService = (IHelloService)context.getBean("helloService");
//此调用会立即返回null
helloService.sayHello("Hello World");
//拿到调用的Future引用，在结果返回后，会被通知和设置到此Future中
Future<String> helloFuture = RpcContext.getContext().getFuture();

//如果已返回，则直接拿到返回值，否则线程等待，直到str值返回后，线程才被唤醒
String str = helloFuture.get();
```

在异步调用中还可以设置是否需要等待发送和返回值，设置如下：
- `sent="true"`：等待消息发出，消息发送失败时将抛出异常；
- `sent="true"`：不等待消息发送，将消息放入I/O队列，即可返回；
- `return="false"`：只是想异步，完全忽略返回值，以减少Future对象的创建和管理成本。

在远程调用的过程中如果出现异常或者需要回调，则可以使用Dubbo的事件通知机制，主要有以下三种事件：
- oninvoke：为在发起远程调用之前触发的事件。
- onreturn：为远程调用之后的回调事件。
- onthrow：为在远程调用出现异常时触发的事件，可以在该事件中实现服务的降级，返回一个默认值等操作。

在消费方配置指定的事件通知接口，配置如下：
```
<bean id="notify" class="com.bill.NotifyImpl" />
<dubbo:reference id="helloService" interface="xxx.IHelloService">
	<dubbo:method name="sayHello" async="true" onreturn="notify.onreturn" onthrow="notify.onthrow" />
</dubbo:reference>
```

其中配置有以下几种情况：
>- 异步回调：async=true onreturn="xxx"
>- 同步回调：async=false onreturn="xxx"
>- 异步无回调：async=true
>- 同步无回调：async=false

#### 11.Dubbo服务提供者线程池介绍一下

Dubbo的服务提供者主要有两种线程池类型：一种是I/O处理线程池；另一种是业务调度线程池。Dubbo限制了I/O线程数，默认是核数+1，而服务调用的线程数默认是200。配置如下：
```
<dubbo:protocol name="dubbo" dispatcher="all" threadpool="fixed" threads="100" accepts="100" />
```
在实际项目中需要通过不同的派发策略和线程池配置的组合来应对不同的场景，对相关配置参数说明如下：

***dispatcher参数(6个类型)：***
- all：所有消息都被派发到线程池，包括请求、响应、连接事件、断开事件、心跳等(默认)。
- direct：所有消息都不被派发到线程池，全部在I/O线程上直接执行。
- message：只有请求相应消息派发到线程池，其他比如连接断开事件、心跳等消息直接在I/O线程上执行。
- execution：只有请求消息派发到线程池，不含响应，响应和其他连接断开事件、心跳等消息直接在I/O线程上执行。
- connection：在I/O线程上将连接断开事件放入队列，有序地逐个执行，讲其他消息派发到线程池。

***threadpool参数(4个类型)：***
- fixed：固定大小的线程池，在启动时建立线程，不关闭，一直持有(默认)。
- cached：缓冲线程池，在空闲一分钟时会被自动删除，在需要时重建。
- limited：扩容的线程池，但池中的线程数只会增长，不会收缩。只增长不收缩的目的是避免收缩时突然来了大流量所引起的性能问题。
- eager：当所有核心线程数都处于忙碌状态时，优先创建新线程执行任务。

***CachedThreadPool缓冲线程池，默认配置如下：***
| 配置 | 配置值 |
| --- | --- |
| corePoolSize | 0 |
| maximumPoolSize | Integer.MAX_VALUE |
| keepAliveTime | 60s |
| workQueue | 根据queue决定是SynchronousQueue还是LinkedBlockingQueue，默认queue=0，所以是SynchronousQueue |
| threadFactory | NamedInternalThreadFactory |
| rejectHandler | AbortPolicyWithReport |

就默认配置来看，和Executors创建的差不多，存在内存溢出风险。NamedInternalThreadFactory主要用于修改线程名，方便我们排查问题。AbortPolicyWithReport对拒绝的任务打印日志，也是方便排查问题。

***LimitedThreadPool扩容的线程池，默认配置如下：***
| 配置 | 配置值 |
| --- | --- |
| corePoolSize | 0 |
| maximumPoolSize | 200 |
| keepAliveTime | Long.MAX_VALUE,相当于无限长 |
| workQueue | 根据queue决定是SynchronousQueue还是LinkedBlockingQueue，默认queue=0，所以是SynchronousQueue |
| threadFactory | NamedInternalThreadFactory |
| rejectHandler | AbortPolicyWithReport |

从keepAliveTime的配置可以看出来，LimitedThreadPool线程池的特性是线程数只会增加不会减少。

***FixedThreadPool固定大小线程池(dubbo默认线程池)，默认配置如下：***
| 配置 | 配置值 |
| --- | --- |
| corePoolSize | 200 |
| maximumPoolSize | 200 |
| keepAliveTime | 0 |
| workQueue | 根据queue决定是SynchronousQueue还是LinkedBlockingQueue，默认queue=0，所以是SynchronousQueue |
| threadFactory | NamedInternalThreadFactory |
| rejectHandler | AbortPolicyWithReport |

Dubbo的默认线程池，固定200个线程，就配置来看和LimitedThreadPool基本一致。如果一定要说区别，那就是FixedThreadPool等到创建完200个线程，再往队列放任务。而LimitedThreadPool是先放队列放任务，放满了之后才创建线程。

***EagerThreadPool优先创建线程池，默认配置如下：***
| 配置 | 配置值 |
| --- | --- |
| corePoolSize | 0 |
| maximumPoolSize | Integer.MAX_VALUE |
| keepAliveTime | 60s |
| workQueue | 自定义实现TaskQueue，默认长度为1，使用时要自己配置下 |
| threadFactory | NamedInternalThreadFactory |
| rejectHandler | AbortPolicyWithReport |

我们知道，当线程数量达到corePoolSize之后，只有当workqueue满了之后，才会增加工作线程。
这个线程池就是对这个特性做了优化，首先继承ThreadPoolExecutor实现EagerThreadPoolExecutor，对当前线程池提交的任务数submittedTaskCount进行记录。
其次是通过自定义TaskQueue作为workQueue，它会在提交任务时判断是否`currentPoolSize < submittedTaskCount < maxPoolSize`，然后通过workQueue的offer方法返回false导致增加工作线程。

#### 12.Dubbo的服务降级

服务熔断是一种保护措施，一般用于防止在软件系统中由于某些原因使服务出现了过载现象，从而造成整个系统发生故障，有时也被称为过载保护。服务降级则是在服务器压力剧增的情况下，根据当前的业务情况及流量对一些服务和页面有策略地进行降级，以释放服务器资源并保证核心任务的正常运行。

Dubbo除了可以通过`onthrow`事件来做降级服务外，还可以使用mock配置来实现服务降级。mock在出现非业务异常（比如超时、提供者全部挂掉或网络异常等）时执行，mock支持如下两种配置：
- 一种是配置为boolean值。默认配置为false，如果配置为true，则默认使用mock的类名，即类型+Mock后缀。
- 另一种则是配置为return null，可以很简单地忽略掉异常。

```
<!-- 第一种配置 -->
<dubbo:service interface="xxx.IHelloService" mock="true" />
<!-- 另一种 -->
<dubbo:service interface="xxx.IHelloService" mock="xxx.HelloServiceMock" />
<!-- 如果只是简单地忽略异常，则可以如下设置 -->
<dubbo:service interface="xxx.IHelloService" mock="return null" />
```

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

#### 3.Dubbo线程方面的坑

(1)线程数设置过少的问题。在使用的过程中如果出现以下异常，则可以适当增加`threads`的数量来解决线程不如的问题，Dubbo默认`threads`为200：
```
Caused by: java.util.conncurrent.RejectedExecutionException：Thread pool is EXHAUSTED!
```

(2)线程数设置过多的问题。如果线程数过多，则可能会受Linux用户线程数(Linux默认最大的线程数为1024个)的限制而导致异常，通常可以通过`ulimit -u`来解决，具体的异常信息如下：
```
java.lang.OutOfMemoryError: unable to create new native thread
```

(3)连接不上服务端的问题。在出现以下异常时会连接不上服务器，在大多数情况下可能是因为服务器没有正常启动或者网络无法连接，不过也有可能是因为超过了服务端的最大允许连接数，可以通过调大`accepts`的值解决：
```
com.alibaba.dubbo.remoting.RemotingException: Failed connect to server
```

### 三、故障排除

#### 1.从服务器的CPU，内存和I/O三方面查询故障和问题

查看CPU或内存情况的命令如下：
- `top`: 查看服务器的负载状况。
- `top+1`: 在top视图中按键盘数字"1"查看每个逻辑CPU的使用情况。
- `jstat`: 查看堆中各内存区域的变化及GC的工作状态。
- `top+H`: 查看线程的使用情况。
- `ps -mp pid -o THREAD,tid,time |sort -rn`: 查看指定进程中各个线程占用CPU的状态，选出耗时最多、最繁忙的线程ID。
- `jstack`: 打印进程中的线程堆栈信息。

判断内存溢出(OOM)方法如下：
- 堆外内存溢出：由JNI的调用或NIO中的DirectByteBuffer等使用不当造成。
- 堆内内存溢出：容易由程序中创建的大对象、全局集合、缓存、ClassLoader加载的类或大量的线程消耗等造成。
- 使用`jmap -heap`命令、`jmap -histo`命令或者`jmap -dump:format=b,file=xxx.hprof`等命令查看JVM内存的使用情况。

分析I/O读写问题的方法如下：
- 文件I/O：使用命令`vmstat`、`lsof -c -p pid`等。
- 网络I/O：使用命令`netstat -anp`、`tcpdump -i eth0 'dst host 239.33.11.23' -w raw.pcap`和`wireshark`工具等。
- Mysql数据库：查看慢查询日志、数据库的磁盘空间、排查索引是否缺失，或使用`show processlist`检查具体的SQL语句情况。

### 四、源码和原理

#### 1.Dubbo协议是长链接的，当前A服务多个线程调B服务，是只建立一个链接吗。Dubbo怎么处理的？

`Dubbo`默认情况下消费端与服务端只创建一个长连接。由于底层使用了netty的nio技术，不需要像BIO一样开启一个线程对应一个链接。（这里其实可能会紧跟着问NIO的知识 [传送门 - NIO/BIO/AIO区别](https://github.com/YephetsSkys/LiuAndChen/blob/master/thinking-in-interview/Netty%E9%9D%A2%E8%AF%95%E9%A2%98.md#4bionio%E5%92%8Caio%E7%9A%84%E5%8C%BA%E5%88%AB) ）

`Dubbo`处理粘包和拆包是通过底层的Netty处理的，所以`Dubbo`不需要关注细节。[传送门 - TCP 粘包/拆包的原因及解决方法](https://github.com/YephetsSkys/LiuAndChen/blob/master/thinking-in-interview/Netty%E9%9D%A2%E8%AF%95%E9%A2%98.md#9tcp-%E7%B2%98%E5%8C%85%E6%8B%86%E5%8C%85%E7%9A%84%E5%8E%9F%E5%9B%A0%E5%8F%8A%E8%A7%A3%E5%86%B3%E6%96%B9%E6%B3%95)

dubbo协议会为每个请求数据包设置一个不会重复的id，并且用一个Map存储id对应的Future，让发起调用的线程阻塞等待结果。服务端在响应数据包时，将请求id回写到数据包，客户端的单一长连接在接收到响应数据包时，根据请求id从Map中获取Future并写入值、将阻塞等待的发请调用的线程唤醒。

#### 2.Dubbo客服端同步/异步调用的实现原理

`Dubbo`客户端同步调用的逻辑是在`DefaultFuture`类中，虽然使用了NIO的方式进行长连接通信。但是通过对调用线程的阻塞和唤醒来达到同步调用。实际还是主动使用`CompletableFuture`的`get`机制。真正的代码在`AsyncRpcResult`的`get`方法中。

```
//AsyncToSyncInvoker

public Result invoke(Invocation invocation) throws RpcException {
    Result asyncResult = invoker.invoke(invocation);

    try {
        // 在这里如果是同步方式调用，则主动的执行get
        if (InvokeMode.SYNC == ((RpcInvocation) invocation).getInvokeMode()) {
            asyncResult.get(Integer.MAX_VALUE, TimeUnit.MILLISECONDS);
        }
    } catch (Throwable e) {
        throw new RpcException(e.getMessage(), e);
    }
    return asyncResult;
}
```

`Dubbo`客户端异步调用通过在设置返回值为`CompletableFuture`类或者显示设置了属性`async=true`，来实现异步调用，用户来主动的调用`get`来触发调用线程的阻塞。

```
// InvokerInvocationHandler

@Override
public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    // ...
    // 这里调用了AsyncRpcResult的recreate方法
    return invoker.invoke(rpcInvocation).recreate();
}

public Object recreate() throws Throwable {
    RpcInvocation rpcInvocation = (RpcInvocation) invocation;
    // 如果是Future模式，则返回一个Future对象
    if (InvokeMode.FUTURE == rpcInvocation.getInvokeMode()) {
        return RpcContext.getContext().getFuture();
    }

    return getAppResponse().recreate();
}
```

`Dubbo`客户端当调用的是`Callback`方法时，`Consumer`端发送请求的同时暴露一个回调参数的服务，这样`Provider`返回结果的方式就变成了调用`Consumer`暴露的这个服务，也就是返回结果时`Provider`变成了`Consumer`。

```
// CallbackServiceCodec
public static Object encodeInvocationArgument(Channel channel, RpcInvocation inv, int paraIndex) throws IOException {
    // get URL directly
    URL url = inv.getInvoker() == null ? null : inv.getInvoker().getUrl();
    byte callbackStatus = isCallBack(url, inv.getProtocolServiceKey(), inv.getMethodName(), paraIndex);
    Object[] args = inv.getArguments();
    Class<?>[] pts = inv.getParameterTypes();
    switch (callbackStatus) {
        case CallbackServiceCodec.CALLBACK_CREATE: // 这里exportOrUnexportCallbackService就是暴露方法
            inv.setAttachment(INV_ATT_CALLBACK_KEY + paraIndex, exportOrUnexportCallbackService(channel, url, pts[paraIndex], args[paraIndex], true));
            return null;
        case CallbackServiceCodec.CALLBACK_DESTROY:
            inv.setAttachment(INV_ATT_CALLBACK_KEY + paraIndex, exportOrUnexportCallbackService(channel, url, pts[paraIndex], args[paraIndex], false));
            return null;
        default:
            return args[paraIndex];
    }
}
```

### 五、面试连珠炮解析

未完待续