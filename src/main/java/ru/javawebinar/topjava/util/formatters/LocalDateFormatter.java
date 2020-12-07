package ru.javawebinar.topjava.util.formatters;

import org.springframework.format.Formatter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;

public class LocalDateFormatter implements Formatter<LocalDate> {
    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        return StringUtils.isEmpty(text) ? null : LocalDate.parse(text);
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        return null;
    }
}
