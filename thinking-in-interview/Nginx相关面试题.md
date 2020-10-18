### 一、Nginx基础

#### 1.nginx如何配置负载均衡

```
http {
    include             mime.types;
    default_type        application/octet-stream;
    sendfile            on;
    keepalive_timeout   65;
    upstream names_sss {
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

#### 2.Location配置讲解

#### 3.Nginx Rewrite语法

#### 4.Nginx 访问认证

### 二、Nginx调优

### 三、Nginx高可用

