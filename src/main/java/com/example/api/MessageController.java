package com.example.api;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Controller
public class MessageController {
    private final static String GREET_FILE_PATH = "src/main/resources/hello.txt";

    @Get("greet")
    @Produces(MediaType.TEXT_PLAIN)
    public String greet() {
        String greet = "";
        try {
            greet = Files
                    .lines(Paths
                            .get(GREET_FILE_PATH)
                    ).collect(Collectors.joining("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return greet;
    }


    @Get("/polyglot")
    @Produces(MediaType.TEXT_PLAIN)
    public String polyglot() {
        StringBuilder text = new StringBuilder();

        try (Context context = Context.create()) {
            context.eval("js",
                    "function greet(param) {" +
                            "return 'Hello ' + param;" +
                            "}");
            Value js = context.eval("js", "greet");
            text.append(js.execute("with JavaScript"));
        }

        text.append("\n");
        try (Context context = Context.create()) {
            context.eval("python",
                    "def greet(param1, param2):" +
                           "   return f'Hello {param1} ' + str(param2)");
            Value python = context.eval("python", "greet");
            text.append(python.execute("with Python", 3));
        }

        return text.toString();
    }
}
