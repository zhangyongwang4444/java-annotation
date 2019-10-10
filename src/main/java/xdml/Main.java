package xdml;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    // 把 MyService 中的所有带 @Log 注解的方法都过滤出来
    static List<String> methodsWithLog = Stream.of(MyService.class.getMethods())
            .filter(Main::isAnnotationWithLog)
            .map(Method::getName)
            .collect(Collectors.toList());


    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        xdml.MyService service = enhanceByAnnotation();

        service.queryDatabase(1);
        service.provideHttpResponse("abc");
        service.noLog();
    }

    private static xdml.MyService enhanceByAnnotation() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return new ByteBuddy()
                .subclass(MyService.class)
                .method((method) -> methodsWithLog.contains(method.getName()))
                .intercept(MethodDelegation.to(LoggerInterceptor.class))
                .make()
                .load(Main.class.getClassLoader())
                .getLoaded()
                .getConstructor()
                .newInstance();
    }

    private static boolean isAnnotationWithLog(Method method) {
        return method.getAnnotation(Log.class) != null;
    }


    public static class LoggerInterceptor {
        public static void log(@SuperCall Callable<Void> zuper)
                throws Exception {
            System.out.println("--Start--");
            try {
                zuper.call();
            } finally {
                System.out.println("--End--");
            }
        }
    }
}
