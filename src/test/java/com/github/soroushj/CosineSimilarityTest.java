package com.github.soroushj;

import org.junit.Test;
import org.junit.Rule;
import org.junit.Assert;
import org.neo4j.harness.junit.Neo4jRule;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Values;

public class CosineSimilarityTest {
    @Rule
    public Neo4jRule neo4j = new Neo4jRule().withFunction(CosineSimilarity.class);

    @Test
    public void testCosineSimilarity() {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
            Session session = driver.session();
            double similarity = session.run("RETURN com.github.soroushj.cosineSimilarity($text1, $text2) AS sim", Values.parameters(
                    "text1", "Bayesian BOLD and perfusion source separation and deconvolution from functional ASL imaging",
                    "text2", "Bayesian separation of spectral sources under non-negativity and full additivity constraints"))
                    .single().get("sim").asDouble();
            Assert.assertEquals(0.3, similarity, 1e-16);
        }
    }

    @Test
    public void testNullCosineSimilarity() {
        try (Driver driver = GraphDatabase.driver(neo4j.boltURI(), Config.build().withoutEncryption().toConfig())) {
            Session session = driver.session();
            double similarity = session.run("RETURN com.github.soroushj.cosineSimilarity($text1, $text2) AS sim", Values.parameters(
                    "text1", null,
                    "text2", null))
                    .single().get("sim").asDouble();
            Assert.assertEquals(0, similarity, 1e-16);
        }
    }
}
