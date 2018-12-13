# Neo4j Cosine Similarity Plugin
This is a user-defined function for Neo4j which returns the cosine similarity of two strings. Internally, it uses Apache Lucene.
## Test & Build
```shell
gradle build
```
## Deploy
Copy the built jar file found in `./build/libs/` to the Neo4j `plugins` directory. For the default Neo4j `plugins` path on Ubuntu 16.04+:
```shell
sudo cp ./build/libs/neo4j-cosine-similarity-plugin.jar /var/lib/neo4j/plugins/
```
After copying the jar file, you need to restart the Neo4j service. On Ubuntu 16.04+:
```shell
sudo service neo4j restart
```
## Usage
Now you can use the function `com.github.soroushj.cosineSimilarity` in Cypher queries:
```cypher
RETURN com.github.soroushj.cosineSimilarity("String A", "String B")
```
