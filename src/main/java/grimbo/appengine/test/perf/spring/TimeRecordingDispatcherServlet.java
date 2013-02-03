package grimbo.appengine.test.perf.spring;

import grimbo.appengine.test.perf.Log;
import grimbo.appengine.test.perf.TimeRecorder;
import grimbo.appengine.test.perf.TimeRecordingListener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class TimeRecordingDispatcherServlet extends DispatcherServlet {
    private static int nextId = 0;
    private long creationTime;
    private String name;
    private String logPrefix;
    private int id;
    TimeRecorder timeRecorder;

    public TimeRecordingDispatcherServlet() {
        super();
        create();
    }

    public TimeRecordingDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        create();
    }

    private static synchronized int getNextId() {
        return ++nextId;
    }

    private void create() {
        creationTime = System.currentTimeMillis();

        id = getNextId();

        name = getClass().getSimpleName();
        logPrefix = name + "." + id + ". ";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        long start = System.currentTimeMillis();

        timeRecorder = TimeRecordingListener.getTimeRecorder(config.getServletContext());

        super.init(config);

        long end = System.currentTimeMillis();

        record(logPrefix + "creation", creationTime);
        record(logPrefix + "init.start", start);
        record(logPrefix + "init.end", end);
    }

    @Override
    protected void initStrategies(ApplicationContext context) {
        long start = System.currentTimeMillis();

        super.initStrategies(context);

        long end = System.currentTimeMillis();

        record(logPrefix + "initStrategies.start", start);
        record(logPrefix + "initStrategies.end", end);
    }

    @Override
    protected WebApplicationContext initWebApplicationContext() {
        long start = System.currentTimeMillis();

        WebApplicationContext wac = super.initWebApplicationContext();

        long end = System.currentTimeMillis();

        record(logPrefix + "initWebApplicationContext.start", start);
        record(logPrefix + "initWebApplicationContext.end", end);

        return wac;
    }

    @Override
    protected void initFrameworkServlet() throws ServletException {
        long start = System.currentTimeMillis();

        super.initFrameworkServlet();

        long end = System.currentTimeMillis();

        record(logPrefix + "initFrameworkServlet.start", start);
        record(logPrefix + "initFrameworkServlet.end", end);
    }

    @Override
    public void destroy() {
        long destroyTime = System.currentTimeMillis();

        record(logPrefix + "destroy.start=", destroyTime);

        timeRecorder = null;

        super.destroy();
    }

    private void record(String msg, long time) {
        timeRecorder.add(time, msg);
        Log.log(msg, time);
    }
}
