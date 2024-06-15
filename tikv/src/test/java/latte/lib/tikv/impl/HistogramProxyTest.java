package latte.lib.tikv.impl;

import org.junit.jupiter.api.Test;
import org.mockito.cglib.proxy.Enhancer;
import org.mockito.cglib.proxy.MethodInterceptor;
import org.mockito.cglib.proxy.MethodProxy;
import org.tikv.common.util.BackOffFunction;
import org.tikv.common.util.HistogramUtils;
import org.tikv.shade.io.prometheus.client.Histogram;

import org.tikv.shade.io.prometheus.client.SimpleCollector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

public class HistogramProxyTest {
//    public class HistogramProxy implements java.lang.reflect.InvocationHandler {
//        private Histogram person;
//
//        public HistogramProxy(Histogram person) {
//            this.person = person;
//        }
//        @Override
//        public Object invoke(Object o, Method method, Object[] args) throws Throwable {
//            if (method.getName().equals("labels")) {
//                // 在调用labels方法之前，执行一些额外的逻辑
//                System.out.println("Before calling labels method");
//
//                // 调用原始对象的labels方法，并获取返回值
//                Object result = method.invoke(person, args);
//
//                // 在调用labels方法之后，执行一些额外的逻辑
//                System.out.println("After calling labels method");
//
//                // 将返回值封装在Histogram对象中，并返回
//                return result;
//            } else {
//                return method.invoke(person, args);
//            }
//        }
//    }
//
////    void initHistogramClibc() {
////
////    }
//    public class CglibHistogram implements MethodInterceptor {
//        private Histogram target;
//
//        public CglibHistogram(Histogram target) {
//            this.target = target;
//        }
//        @Override
//        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//            if (method.getName().equals("<init>")) {
//                Constructor<?> constructor = Histogram.class.getConstructor(Histogram.Builder.class);
//                return constructor.newInstance(null);
//            }
//            // 在方法调用前进行一些操作
//            System.out.println("Before method " + method.getName());
//
//            // 调用原始对象的方法
//            Object result = proxy.invoke(target, args);
//
//            // 在方法调用后进行一些操作
//            System.out.println("After method " + method.getName());
//
//            return result;
//        }
//
//        public Object createProxy() {
//            Enhancer enhancer = new Enhancer();
//            enhancer.setSuperclass(target.getClass());
////            enhancer.setCallback(this);
//            enhancer.setCallback(this);
//            return enhancer.create();
//        }
//    }
//
//
//    @Test
//    public void proxy() {
////        Histogram BACKOFF_DURATION =
////                HistogramUtils.buildDuration()
////                        .name("client_java_backoff_duration")
////                        .help("backoff duration.")
////                        .labelNames("type", "cluster")
////                        .register();
////        Histogram histogram = (SimpleCollector) Proxy.newProxyInstance(
////                        Histogram.class.getClassLoader(),
////                        new Class[] {Histogram.class},
////                        new HistogramProxy(BACKOFF_DURATION));
//        // 创建代理对象
////        CglibHistogram cglibHistogram = new CglibHistogram(BACKOFF_DURATION);
////        Histogram histogram = (Histogram) cglibHistogram.createProxy();
//        Histogram histogram = new ProxyHistogramBuilder(HistogramUtils.buildDuration()
//                .name("client_java_backoff_duration")
//                .help("backoff duration.")
//                .labelNames("type", "cluster")).register();
//        String[] labels = new String[] {BackOffFunction.BackOffFuncType.BoTiKVRPC.name(), ""};
//        Histogram.Timer timer =  histogram.labels(labels).startTimer();
//        timer.observeDuration();
//
//    }
}
