package grimbo.appengine.test.perf;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

@SuppressWarnings("serial")
public class TimeRecordingListener implements ServletContextListener {
    private final static String TIME_RECORDER_KEY = "TIME_RECORDER";

    long creationTime;
    long initTime;
    long destroyTime;
    TimeRecorder timeRecorder;

    public TimeRecordingListener() {
        super();

        creationTime = System.currentTimeMillis();
    }

    @Override
    public void contextInitialized(ServletContextEvent evt) {
        initTime = System.currentTimeMillis();

        timeRecorder = new TimeRecorder();

        ServletContext context = evt.getServletContext();
        context.setAttribute(TIME_RECORDER_KEY, timeRecorder);
        Log.setContext(context);

        record("TimeRecordingListener.creation", creationTime);
        record("TimeRecordingListener.init", initTime);
    }

    @Override
    public void contextDestroyed(ServletContextEvent evt) {
        destroyTime = System.currentTimeMillis();

        record("TimeRecordingListener.destroy", destroyTime);

        ServletContext context = evt.getServletContext();
        context.removeAttribute(TIME_RECORDER_KEY);
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getInitTime() {
        return initTime;
    }

    private void record(String msg, long time) {
        timeRecorder.add(time, msg);
        Log.log(msg, time);
    }

    public static TimeRecorder getTimeRecorder(ServletContext context) {
        return (TimeRecorder) context.getAttribute(TIME_RECORDER_KEY);
    }
}
