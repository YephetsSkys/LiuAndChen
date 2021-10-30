package com.interview.test;

// 桥接方法是 JDK 1.5 引入泛型后，为了使Java的泛型方法生成的字节码和 1.5 版本前的字节码相兼容，由编译器自动生成的方法。
// 一个子类在继承（或实现）一个父类（或接口）的泛型方法时，在子类中明确指定了泛型类型，那么在编译时编译器会自动生成桥接方法，桥接方法调用了实际的泛型方法。
// 如果不生成桥接方法，那么S就没有实现接口中声明的方法，语义就不正确了，所以编译器才会自动生成桥接方法，来保证兼容性。
public class BridgeMethodTest {

    public static void main(String[] args) {
        P<String> p = new S();
        String o = p.test("111");
        System.out.println(o);
    }

    static class P<T> {
        public T test (T t){
            return t;
        }
    }

    static class S extends P<String> {
        @Override
        public String test(String t) {
            return t;
        }
    }

}
