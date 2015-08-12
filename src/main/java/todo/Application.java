package todo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootApplication
@Controller
public class Application {

    private final JsComponents js;

    @Autowired
    public Application(JsComponents js) {
        this.js = js;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index(Model model) {
        // get data from data layer
        final Map<String, Object> data = new HashMap<>();
        // get location from request object
        final String location = "";
        model.addAttribute("todo", js.renderTodoApp(data, location));
        return "index";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}
