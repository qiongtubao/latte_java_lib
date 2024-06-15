package latte.lib.common.glibc;

//import net.sf.cglib.proxy.Enhancer;
//import net.sf.cglib.proxy.InvocationHandler;
//import net.sf.cglib.proxy.MethodInterceptor;
//import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class GlibcTest {
    public static class Person {
        public void sayHello() {
            System.out.println("Hello, I'm a person.");
        }
    }

//    @Test
//    public void proxy() {
//        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass(Person.class);
//        enhancer.setCallback(new MethodInterceptor() {
//            @Override
//            public Object intercept(Object obj, java.lang.reflect.Method method, Object[] args, MethodProxy proxy) throws Throwable {
//                System.out.println("Before method: " + method.getName());
//                Object result = proxy.invokeSuper(obj, args);
//                System.out.println("After method: " + method.getName());
//                return result;
//            }
//        });
//
//        Person person = (Person) enhancer.create();
//        person.sayHello();
//    }

    public class PersonProxy implements java.lang.reflect.InvocationHandler {
        private Person person;

        public PersonProxy(Person person) {
            this.person = person;
        }
        @Override
        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("sayHello")) {
                // 在调用labels方法之前，执行一些额外的逻辑
                System.out.println("Before calling labels method");

                // 调用原始对象的labels方法，并获取返回值
                Object result = method.invoke(person, args);

                // 在调用labels方法之后，执行一些额外的逻辑
                System.out.println("After calling labels method");

                // 将返回值封装在Histogram对象中，并返回
                return result;
            } else {
                return method.invoke(person, args);
            }
        }
    }
    @Test
    public void proxyObject() {
//        Person person = new Person();
//        Person person2 =  (Person) Proxy.newProxyInstance(
//                        Person.class.getClassLoader(),
//                        new Class[] { Person.class },
//                        new PersonProxy(person));
//        person2.sayHello();
    }
}
