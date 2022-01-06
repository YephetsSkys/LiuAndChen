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

`location`块在`server`中使用，它的作用是根据客户端请求的URL去定位不同的服务器端资源。匹配规则说明如下：

| 配置个是 | 作用 |
| --- | --- |
| location = /uri | =号表示精确匹配 |
| location ^~ /uri | ^~匹配以某个URL前缀开头的请求，不支持正则表达式 |
| location ~ | ~区分大小写的匹配，属于正则表达式 |
| location ~* | ~*不区分大小写的匹配，属于正则表达式 |
| location /uri | 表示前缀匹配，不带修饰符，但是优先级没有正则表达式高 |
| location / | 通用匹配，默认找不到其他匹配时，会进行通用匹配 |
| location @ | 命令空间，不提供常规的请求匹配 |

`=`优先级最高，如果`=`匹配不到，则会和`^~`进行匹配，之后是`~`，如果有多个`~`则按照其在配置文件里的先后顺序进行匹配；如果还是匹配不到，则与`/uri`进行匹配；通用匹配`/`优先级最低，只有找不到其他的配置的时候，才会进行通用匹配。

有一些指令只能在`location`块中执行，主要有如下3个：
- `internal`：标识该`location`块只支持`Nginx`内部的请求访问，如支持`rewrite`、`error_page`等重定向，但不能通过外部的`HTTP`直接访问；
- `limit_except`：限定该`location`块可以执行的`HTTP`方法，如`GET`；
- `alias`：定义指定位置的替换，如`location /a/ {alias /c/x/a/;}`，则如果匹配到`/a/test.json`，在进入`location`块之后会被替换为`/c/x/a/test.json`；

#### 4.Nginx Rewrite语法

`rewrite`是`ngx_http_rewrite_module`模块下的指定，使用的频率很高。`rewrite`支持的配置环境有`server`、`location`，`if`，它通过`break`和`last`来完成内部重定向功能。内部重定向是在`Nginx`内部发送请求的操作，它可以将请求转发到其他的`location`或对`URL`进行修改，而不必通过`HTTP`连接请求，整个操作非常高效。

```
# 匹配以/a结尾的URI，匹配成功后将其修改为/b的URI，即后端服务器看到的URI会是/b并停止rewrite阶段，执行下一个阶段，即proxy_pass
rewrite /a$ /b break;

# 匹配以/a开头的URI，匹配成功后将其修改为/b的URI并停止rewrite阶段，执行下一个阶段，即proxy_pass
rewrite ^/a /b break;

# 匹配/a的URI，成功后将其修改成/b的URL并停止rewrite阶段，执行下一个阶段，即proxy_pass
rewrite ^/a$ /b break;

# 匹配包含/a的URI，匹配成功后将其修改成/b的URL并停止rewrite阶段，执行下一个阶段，即proxy_pass
rewrite /a /b break;

# 匹配以/a/开头的请求，并将/a/后面的URI全部捕获，(.*)的作用就是捕获全部URI，然后重定向成 /b/$1
# 其中$1iushi前面捕获到的URI。匹配成功后将其修改为/b的URI并停止rewrite阶段，执行下一个阶段，即proxy_pass
rewrite ^/a/(.*) /b/$1 break;

# last的匹配规则额break完全一样，只是当它匹配并修改完URL后，会将请求从当前的location中跳出来，找到对应的location
rewrite /a /b last;

proxy_pass http://test_servers;
```

当需要将内部重定向记录到日志中的时候，可以使用`rewrite_log`。在`rewrite`后面跟随的参数始终是正则表达式，并且当内部重定向时`URL`的参数是不会丢失的；

通过`rewrite`恶意实现域名间的跳转：
```
# permanent 参数标识永久重定向，将所有的请求全部跳转熬指定域名上
# 通过(.*)将URL保留下来，跳转过程中参数不会丢失。HTTP状态码为301
rewrite ^/(.*)$ http://www.test.com/$1 permanent;

# redirect 参数表示临时重定向，将所有的请求全部跳转到指定域名上。HTTP状态码为302
rewrite ^/(.*)$ http://www.test.com/$1 redirect;
```

`301`和`302`的跳转并不适合`POST`请求，如果是`POST`被跳转的话，会先被转化为`GET`请求，且请求体的内容会丢失。为此，`HTTP 1.1`提供了新的状态码，`307`的意义和`302`一样，而`308`和`301`的意义一样。但如果要求在跳转过程中保持客户端的请求方法不变，需要使用`return`指令。
```
# 但是使用这两个有个问题，就是缺少Content_Type响应头，被跳转的请求体会被浏览器提示保存为文本下载到本地
return 307 http://www.test.com/$request_uri;
return 308 http://www.test.com/$request_uri;
```

可以通过输出变量值来返回请求信息：
```
locaton / {
    set $a '1';
    set $b '2';
    set $ab $a$b;       # 合并两个变量的值
    return 200 $ab;     # 输出为12，状态码为200
}
```

#### 5.Nginx 访问认证

可以通过配置`auth_basic`来设置用户输入指定的用户名和密码才能访问相关的资源。可以配置在`http`、`server`、`location`、`limit_except`环境中。

```
server {
    listen 80;
    server_name localhost;
    location / {
        auth_basic  "Please Input UserName And Password";
        auth_basic_user_file conf/htpasswd; # 密码存放的文件地址
    }
}
```

其中存放文件中的保存个是为：`test:asdasdadasdaczadqasdad`。密码不是明文的，可以使用`htpasswd`或`openssl`工具生成。如执行如下命令：`echo "test:`openssl passwd -1 123456`"`生成密码并写入到指定的密码文件中即可。

但是上述配置麻烦就在于没有一个统一的管理支出，而且人员流动的话都需要运维去服务器上删除或者新增用户和密码，非常的不方便。可以使用`LDAP`认证来让每个用户都有一个单独的用户名和密码。

也可以配置比如在公司不需要登录，而在家里需要登录：
```
satisfy any;
auth-ldap           "Forbidden";
auth_ldap_servers   testldap;

allow               192.168.0.0/16;
```

上述配置的作用是当请求地址在`192.168.0.0/16`时，不需要使用LDAP认证即可直接访问。如果IP地址不在的话，则需要通过LDAP认证进行登录。

### 二、Nginx调优

未完待续

### 三、Nginx高可用

未完待续
