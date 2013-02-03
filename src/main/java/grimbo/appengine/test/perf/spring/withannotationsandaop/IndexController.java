package grimbo.appengine.test.perf.spring.withannotationsandaop;

import grimbo.appengine.test.perf.TestService;
import grimbo.appengine.test.perf.spring.TimeRecordingController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/index")
public class IndexController extends TimeRecordingController {
    private TestService testService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        long start = System.currentTimeMillis();
        record("aspectServiceCall.start", start);

        String value = testService.getName();

        long end = System.currentTimeMillis();
        record("aspectServiceCall.value=" + value, end);
        record("aspectServiceCall.end", end);

        return super.handleRequestInternal(req, resp);
    }

    @Autowired
    public void setTestService(TestService testService) {
        this.testService = testService;
    }
}
