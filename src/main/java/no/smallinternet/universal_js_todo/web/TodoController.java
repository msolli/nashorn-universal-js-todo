package no.smallinternet.universal_js_todo.web;

import java.util.HashMap;
import java.util.Map;

import no.smallinternet.universal_js_todo.service.JsComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TodoController {

    private final JsComponents js;

    @Autowired
    public TodoController(JsComponents js) {
        this.js = js;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index(Model model) {
        // get data from data layer
        final Map<String, Object> data = new HashMap<>();
        data.put("yo", "man");
        // get location from request object
        final String location = "";
        model.addAttribute("todo", js.renderTodoApp(data, location));
        return "index";
    }

}
