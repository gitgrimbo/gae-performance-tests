package grimbo.appengine.test.perf.spring.withannotations;

import grimbo.appengine.test.perf.spring.TimeRecordingController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class IndexControllerWithAnnotations extends TimeRecordingController {
}
