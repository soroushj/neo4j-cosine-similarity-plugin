package com.github.soroushj;

import java.lang.Math;
import java.lang.Exception;
import java.lang.RuntimeException;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.io.Closeable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.neo4j.procedure.UserFunction;
import org.neo4j.procedure.Name;

public class CosineSimilarity {
    @UserFunction()
    @Description("Returns the cosine similarity of two strings.")
    public double cosineSimilarity(@Name("text1") String text1, @Name("text2") String text2) {
        if (text1 == null || text2 == null) {
            return 0;
        }
        List<String> terms1 = tokenize(text1);
        if (terms1.size() == 0) {
            return 0;
        }
        List<String> terms2 = tokenize(text2);
        if (terms2.size() == 0) {
            return 0;
        }
        Set<String> allTerms = new HashSet<>();
        for (String t: terms1) {
            allTerms.add(t);
        }
        for (String t: terms2) {
            allTerms.add(t);
        }
        int[] v1 = new int[allTerms.size()];
        int[] v2 = new int[allTerms.size()];
        int i = 0;
        for (String term: allTerms) {
            for (String t: terms1) {
                if (term.equals(t)) {
                    v1[i]++;
                }
            }
            for (String t: terms2) {
                if (term.equals(t)) {
                    v2[i]++;
                }
            }
            i++;
        }
        return dotProduct(v1, v2) / (vectorMagnitude(v1) * vectorMagnitude(v2));
    }

    private static List<String> tokenize(String text) {
        Analyzer analyzer = null;
        TokenStream standardStream = null;
        TokenStream stemmedStream = null;
        try {
            analyzer = new StandardAnalyzer();
            standardStream = analyzer.tokenStream(null, text);
            stemmedStream = new SnowballFilter(standardStream, "English");
            stemmedStream.reset();
            List<String> tokens = new ArrayList<>();
            while (stemmedStream.incrementToken()) {
                tokens.add(stemmedStream.getAttribute(CharTermAttribute.class).toString());
            }
            return tokens;
        } catch (Exception e) {
            throw new RuntimeException("Lucene: " + e.getMessage());
        } finally {
            if (stemmedStream != null) {
                try {
                    stemmedStream.end();
                } catch (Exception e) {}
            }
            closeQuietly(stemmedStream);
            closeQuietly(standardStream);
            closeQuietly(analyzer);
        }
    }

    private static void closeQuietly(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {}
        }
    }

    private static int dotProduct(int[] v1, int[] v2) {
        int product = 0;
        for (int i = 0; i < v1.length; i++) {
            product += v1[i] * v2[i];
        }
        return product;
    }

    private static double vectorMagnitude(int[] v) {
        int product = 0;
        for (int i = 0; i < v.length; i++) {
            product += v[i] * v[i];
        }
        return Math.sqrt(product);
    }
}
