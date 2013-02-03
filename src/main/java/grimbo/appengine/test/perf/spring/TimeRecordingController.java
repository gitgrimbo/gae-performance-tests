package grimbo.appengine.test.perf.spring;

import grimbo.appengine.test.perf.Log;
import grimbo.appengine.test.perf.TimeRecorder;
import grimbo.appengine.test.perf.TimeRecordingListener;
import grimbo.appengine.test.perf.TimeRecordingServlet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class TimeRecordingController extends AbstractController {
    private static int nextId = 0;

    private String name;
    private int id;
    private long creationTime;
    private String logPrefix;
    private TimeRecorder timeRecorder;

    public TimeRecordingController() {
        super();

        creationTime = System.currentTimeMillis();

        id = getNextId();

        name = getClass().getSimpleName();
        logPrefix = name + "." + id + ". ";
    }

    private static synchronized int getNextId() {
        return ++nextId;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        long start = System.currentTimeMillis();

        super.initServletContext(servletContext);

        long end = System.currentTimeMillis();

        timeRecorder = TimeRecordingListener.getTimeRecorder(servletContext);

        record("creation.start", creationTime);
        record("init.start", start);
        record("init.end", end);
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long start = System.currentTimeMillis();
        record("doGet.start", start);

        TimeRecordingServlet.outputTimeRecordingsJson(req, resp, timeRecorder.getRecordings().iterator());

        long end = System.currentTimeMillis();
        record("doGet.end", end);

        return null;
    }

    protected void record(String msg, long time) {
        timeRecorder.add(time, logPrefix + msg);
        Log.log(logPrefix + msg, time);
    }

}
