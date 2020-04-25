package com.messio.demo

import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.io.Writer
import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import org.springframework.stereotype.Controller


@Controller
@RequestMapping("/")
class MustacheController {
    @GetMapping
    fun apiIndex(model: Model): String {
        return "index"
    }
}

class MapLambda : HashMap<String, Any>(), Mustache.Lambda {
    override fun execute(fragment: Template.Fragment, writer: Writer) {
        put("content", fragment.execute())
    }
}

@ControllerAdvice // injects stuff into all models
class MustacheAdvice {
    @ModelAttribute("plain")
    fun plain() = Mustache.Lambda { fragment, writer -> writer.write(fragment.execute())}

    @ModelAttribute("skeleton")
    fun skeleton() = MapLambda()

    @ModelAttribute("title")
    fun title(@ModelAttribute("skeleton") skeleton: MapLambda)
            = Mustache.Lambda { fragment, _ -> skeleton["title"] = fragment.execute() }

    @ModelAttribute("name")
    fun name(@ModelAttribute("skeleton") skeleton: MapLambda)
            = Mustache.Lambda { fragment, _ -> skeleton["name"] = fragment.execute() }
}