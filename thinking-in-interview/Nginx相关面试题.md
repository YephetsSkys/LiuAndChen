### 一、Nginx基础

#### 1.nginx如何配置负载均衡

```
http {
    include             mime.types;
    default_type        application/octet-stream;
    sendfile            on;
    keepalive_timeout   65;
    upstream names_sss {
        #weigth参数表示权值，权值越高被分配到的几率越大
        server 192.168.1.1:8080 weight=1;
        server 192.168.1.2:9090 weight=1;
    }

    server {
        listen          80;
        server_name     www.test.com;
        location / {
            proxy_pass http://names_sss;
        }
    }
}
```

Nginx的负载均衡策略如下：

##### 轮询
```
upstream aaaa {
    # 默认所有服务器权重为 1
    server 127.0.0.1:8080
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

##### 加权轮询
```
upstream aaaa {
    server 127.0.0.1:8080 weight=3
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

##### 最少连接
```
upstream aaaa {
    least_conn;

    server 127.0.0.1:8080
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

##### 加权最少连接
```
upstream aaaa {
    least_conn;

    server 127.0.0.1:8080   weight=3
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

##### IP Hash
```
upstream aaaa {
    ip_hash;

    server 127.0.0.1:8080
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

##### 普通Hash
```
upstream aaaa {
    hash $request_uri;

    server 127.0.0.1:8080
    server 127.0.0.2:8080
    server 127.0.0.3:8080
}
```

#### 2.Nginx反向代理配置

```
#代理配置参数
proxy_connect_timeout 180;
proxy_send_timeout 180;
proxy_read_timeout 180;
proxy_set_header Host $host;
proxy_set_header X-Forwarder-For $remote_addr;

#反向代理的路径（和upstream绑定），location 后面设置映射的路径
location / {
    proxy_pass http://zp_server1;
}
```

#### 3.Location配置讲解

#### 4.Nginx Rewrite语法

#### 5.Nginx 访问认证

### 二、Nginx调优

### 三、Nginx高可用

