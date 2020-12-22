package ru.javawebinar.topjava.web.converter;

import org.springframework.core.convert.converter.Converter;

public class IntegerConverter implements Converter<String, Integer> {
    @Override
    public Integer convert(String s) {
        if (s.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(s);
    }
}
