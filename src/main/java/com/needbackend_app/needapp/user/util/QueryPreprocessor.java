package com.needbackend_app.needapp.user.util;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QueryPreprocessor {

    private static final CharArraySet STOP_WORDS = EnglishAnalyzer.getDefaultStopSet();

    public String preprocessor(String input) {
        if (input == null || input.isBlank()) return "";

        String[] words = input.toLowerCase().split("\\s+");

        List<String> filtered = Arrays.stream(words)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toList());

        return String.join(" ", filtered);
    }
}
