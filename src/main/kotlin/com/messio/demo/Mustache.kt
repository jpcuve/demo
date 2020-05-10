package com.messio.demo

import com.samskivert.mustache.Mustache
import com.samskivert.mustache.Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute
import java.io.*


class MapLambda : HashMap<String, Any>(), Mustache.Lambda {
    override fun execute(fragment: Template.Fragment, writer: Writer) {
        put("content", fragment.execute())
    }
}

@ControllerAdvice // injects stuff into all models
class MustacheAdvice {
    @ModelAttribute("plain")
    fun plain() = Mustache.Lambda { fragment, writer -> writer.write(fragment.execute()) }

    @ModelAttribute("skeleton")
    fun skeleton() = MapLambda()

    @ModelAttribute("title")
    fun title(@ModelAttribute("skeleton") skeleton: MapLambda) = Mustache.Lambda { fragment, _ -> skeleton["title"] = fragment.execute() }

    @ModelAttribute("name")
    fun name(@ModelAttribute("skeleton") skeleton: MapLambda) = Mustache.Lambda { fragment, _ -> skeleton["name"] = fragment.execute() }
}

@Service
class MustacheService {
    private val compiler = Mustache.compiler()

    @Value("classpath:/templates/")
    private val templateFolder: Resource? = null

    @Value(".mustache")
    private val suffix = ".mustache"

    @Value("UTF-8")
    private val charset = "UTF-8"

    @Throws(IOException::class)
    fun assemble(templateName: String, model: Map<String?, Any?>?): String {
        FileInputStream(File(templateFolder!!.file, templateName + suffix)).use { `is` ->
            val template = compiler.compile(InputStreamReader(`is`, charset))
            return template.execute(model)
        }
    }
}
