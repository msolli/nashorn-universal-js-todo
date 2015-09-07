package no.smallinternet.universaljstodo.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.smallinternet.universaljstodo.service.JsRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TodoController {

    private final JsRenderer js;

    @Autowired
    public TodoController(JsRenderer js) {
        this.js = js;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String index(Model model) {
        // get data from data layer
        final Map<String, Object> data = new HashMap<>();
        data.put("todos", new ArrayList<String>());
        model.addAttribute("todo", js.renderTodoApp(toJson(data)));
        return "index";
    }

    private static String toJson(Object o) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(o);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
