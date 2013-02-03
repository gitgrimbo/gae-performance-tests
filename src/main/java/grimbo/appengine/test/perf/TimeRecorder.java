package grimbo.appengine.test.perf;

import java.util.ArrayList;
import java.util.List;

public class TimeRecorder {
    private static final int MAX_ITEMS = 5000;
    private List<Recording> recordings = new ArrayList<Recording>();

    public void add(long time, String msg) {
        checkMax();
        Recording r = new Recording();
        r.time = time;
        r.msg = msg;
        recordings.add(r);
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    private synchronized void checkMax() {
        // really basic max items cap
        if (recordings.size() > MAX_ITEMS) {
            recordings = new ArrayList<Recording>();
        }
    }

    public static class Recording {
        public long time;
        public String msg;
    }
}
