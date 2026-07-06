package com.WW.fileManager;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final SpringTemplateEngine templateEngine;

    public String templateRender(String templateName, Map<String, Object> variables) {
        if (!StringUtils.hasText(templateName)) {
            throw new IllegalArgumentException("Template name cannot be empty");
        }

        Context context = new Context(Locale.ITALIAN);
        context.setVariables(variables != null ? variables : Collections.emptyMap());

        return templateEngine.process(templateName, context);
    }
}
