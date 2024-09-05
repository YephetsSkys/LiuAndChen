### 一、基础知识点

#### 1.关于Object.defineProperty 的基础知识

`Object.defineProperty`可以对对象的属性进行添加或修改的操作。即可以进行数据劫持。`vue`就是通过这个方法来劫持数据的。在`JavaScript`中不能直接访问它们，可以通过`Object.getOwnPropertyDescriptor(对象名，属性名）`来获取属性描述符的默认值。

`ECMAScript`中有两种属性：数据属性和访问器属性。

a）数据属性（数据描述符）：
- `[[Configurable]]`：能否通过`delete`删除属性，能否修改属性的特性，或者能否把属性修改为访问器属性。
- `[[Enumerable]]`：能否通过`for ·· in`或者`Object.keys()`枚举。
- `[[Writable]]`：属性的值能否被修改。
- `[[Value]]`：属性的值，可以是任何有效的`JavaScript`值（数值，对象，函数等）

b）访问器属性（存取描述符）： 
- `[[Configurable]]`：能否通过`delete`删除属性，能否修改属性的特性，或者能否把属性修改为访问器属性。
- `[[Enumerable]]`：能否通过`for ·· in`或者`Object.keys()`枚举。
- `[[Get]]`：在读取属性时调用的函数。
- `[[Set]]`：在写入属性时调用的函数。

属性描述符的默认值：有两种情况

- `1）`当使用对象字面量或者构造函数的形式创建属性的时候，`enumerable`、`configurable`、`writable`都为`true`，`value`、`get`、`set`都为`undefined`。所以平时定义对象的时候，我们可以随意增删改查。
- `2）`当使用`Object.defineProperty`、`Object.defineProperties`或`Object.create`函数的情况下添加的属性。`enumerable`、`configurable`、`writable`都为`false`；`value`、`get`、`set`都为`undefined`。

修改默认属性默认值就需要用到`Object.defineProperty`这个方法了。以前可以使用非标准的方式：`对象.__defineGetter__("属性",function(){})`或者`对象.__defineSetter__("属性",function(){})`。不过这方法已经被废弃了，虽然有些浏览器还支持，但是不建议使用。

语法：`Object.defineProperty（obj，prop，descriptor）`
- `obj`，即需要修改属性的对象。必填。
- `prop`，需要修改的属性。必填。
- `descriptor`，属性修饰符配置项，是个对象。属性修饰符不填的情况下，这个参数也不能少，最少也要是一个`{ }`空对象。
- 最终返回处理后的`obj`对象

`descriptor`也是分数据描述符和存取描述符。功能也是一样

a）数据描述符：
- `configurable` 
- `enumerable`
- `writable`
- `value`

b） 存取描述符
- `configurable`
- `enumerable`
- `get`
- `set`

数据描述符和存取描述符用法都很简单。不过需要注意的是：
- 数据属性符的`writable`或`value`与存取描述符的`get`或`set`不能同时存在，不然会报错。
- 存取描述符的`get`与`set`也可以不同时存在，如果只指定`get`表示属性不能写（意思进行赋值操作，最后属性还是为`undefined`，即使最初属性定义了初始值），只指定`set`表示属性不能读（意思是获取属性的时候是`undefined`，整个对象都为`{ }`。即使最初定义了一些属性的）。 
- 存取描述符的`get`与`set`是个函数，函数里的`this`指向的是需要修改属性的对象即`obj`

还有个`Object.defineProperties()`可以劫持多个属性。

如果对象的属性中还有对象，那么这时候需要深层遍历，一般的方法是：
```javascript
var obj = {
    name:"zjj",
    sex:'male',
    money:100,
    info:{
        face:'smart'
    }
}

observe(obj)
console.log(obj)

obj.sex = 'female'
obj.info.face = 20;
obj.info.hobit = 'girl';

console.log(obj)

function observe(target){
    if (!target || typeof target !== 'object') return;

    Object.keys(target).forEach(function(val){
        defineProp(target,target[val],val)
    })
}
        
function defineProp(curObj,curVal,curKey){
    observe(curVal) //再次遍历子属性
    Object.defineProperty(curObj,curKey,{
        enumerable:true,
        configurable:true,
        get:function(){
            console.log('获取了属性',curVal)
            return curVal
        },
        set:function(newData){
            console.log('设置了属性',newData)
            curObj = newData;
        }
    })
}
```

`Object.defineProperty`的缺点：
- 无法监控到数组下标的变化，导致直接通过数组的下标给数组设置值，不能实时响应。所以`vue`才设置了`7`个变异数组`（push、pop、shift、unshift、splice、sort、reverse）`的`hack`方法来解决问题。
- 只能劫持对象的属性,因此我们需要对每个对象的每个属性进行遍历。如果能直接劫持一个对象，就不需要递归 + 遍历了。所以`vue3.0`会使用`Proxy`来替代`Object.defineProperty`。

#### 2.关于`Proxy`的基础知识点

`Proxy`用于修改某些操作的默认行为，等同于在语言层面做出修改，所以属于一种“元编程”（`meta programming`），即对编程语言进行编程。

```javascript
var obj = new Proxy({}, {
  get: function (target, propKey, receiver) {
    console.log(`getting ${propKey}!`);
    return Reflect.get(target, propKey, receiver);
  },
  set: function (target, propKey, value, receiver) {
    console.log(`setting ${propKey}!`);
    return Reflect.set(target, propKey, value, receiver);
  }
});
```

上面代码对一个空对象架设了一层拦截，重定义了属性的读取（`get`）和设置（`set`）行为。这里暂时先不解释具体的语法，只看运行结果。对设置了拦截行为的对象`obj`，去读写它的属性，就会得到下面的结果。

```javascript
obj.count = 1
//  setting count!
++obj.count
//  getting count!
//  setting count!
//  2
```

上面代码说明，`Proxy`实际上重载（`overload`）了点运算符，即用自己的定义覆盖了语言的原始定义。

如果`handler`没有设置任何拦截，那就等同于直接通向原对象。

```javascript
var target = {};
var handler = {};
var proxy = new Proxy(target, handler);
proxy.a = 'b';
target.a // "b"
```

上面代码中，`handler`是一个空对象，没有任何拦截效果，访问`proxy`就等同于访问`target`。一个技巧是将`Proxy`对象，设置到`object.proxy`属性，从而可以在`object`对象上调用。

```javascript
var object = { proxy: new Proxy(target, handler) };

var proxy = new Proxy({}, {
  get: function(target, propKey) {
    return 35;
  }
});

let obj = Object.create(proxy);
obj.time // 35
```

上面代码中，`proxy`对象是`obj`对象的原型，`obj`对象本身并没有`time`属性，所以根据原型链，会在`proxy`对象上读取该属性，导致被拦截。

同一个拦截器函数，可以设置拦截多个操作。

```javascript
var handler = {
  get: function(target, name) {
    if (name === 'prototype') {
      return Object.prototype;
    }
    return 'Hello, ' + name;
  },

  apply: function(target, thisBinding, args) {
    return args[0];
  },

  construct: function(target, args) {
    return {value: args[1]};
  }
};

var fproxy = new Proxy(function(x, y) {
  return x + y;
}, handler);

fproxy(1, 2) // 1
new fproxy(1, 2) // {value: 2}
fproxy.prototype === Object.prototype // true
fproxy.foo === "Hello, foo" // true
```

下面是`Proxy`支持的拦截操作一览，一共`13`种。
- `get(target, propKey, receiver)`：拦截对象属性的读取，比如`proxy.foo`和`proxy['foo']`。
- `set(target, propKey, value, receiver)`：拦截对象属性的设置，比如`proxy.foo = v`或`proxy['foo'] = v`，返回一个布尔值。
- `has(target, propKey)`：拦截`propKey in proxy`的操作，返回一个布尔值。
- `deleteProperty(target, propKey)`：拦截`delete proxy[propKey]`的操作，返回一个布尔值。
- `ownKeys(target)`：拦截`Object.getOwnPropertyNames(proxy)`、`Object.getOwnPropertySymbols(proxy)`、`Object.keys(proxy)`、`for...in`循环，返回一个数组。该方法返回目标对象所有自身的属性的属性名，而`Object.keys()`的返回结果仅包括目标对象自身的可遍历属性。
- `getOwnPropertyDescriptor(target, propKey)`：拦截`Object.getOwnPropertyDescriptor(proxy, propKey)`，返回属性的描述对象。
- `defineProperty(target, propKey, propDesc)`：拦截`Object.defineProperty(proxy, propKey, propDesc）`、`Object.defineProperties(proxy, propDesc)`，返回一个布尔值。
- `preventExtensions(target)`：拦截`Object.preventExtensions(proxy)`，返回一个布尔值。
- `getPrototypeOf(target)`：拦截`Object.getPrototypeOf(proxy)`，返回一个对象。
- `isExtensible(target)`：拦截`Object.isExtensible(proxy)`，返回一个布尔值。
- `setPrototypeOf(target, proto)`：拦截`Object.setPrototypeOf(proxy, proto)`，返回一个布尔值。如果目标对象是函数，那么还有两种额外操作可以拦截。
- `apply(target, object, args)`：拦截`Proxy`实例作为函数调用的操作，比如`proxy(...args)`、`proxy.call(object, ...args)`、`proxy.apply(...)`。
- `construct(target, args)`：拦截`Proxy`实例作为构造函数调用的操作，比如`new proxy(...args)`。

