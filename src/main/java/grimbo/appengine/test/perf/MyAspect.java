package grimbo.appengine.test.perf;

import org.aspectj.lang.ProceedingJoinPoint;

public class MyAspect {

    public Object myMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed() + " (with aspect)";
    }

}
