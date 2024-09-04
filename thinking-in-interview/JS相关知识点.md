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

