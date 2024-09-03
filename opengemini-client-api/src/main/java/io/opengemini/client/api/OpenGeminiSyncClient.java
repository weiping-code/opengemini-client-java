package io.opengemini.client.api;

import java.util.List;

/**
 * Interface to access a OpenGemini database provides a set of blocking methods.
 */
public interface OpenGeminiSyncClient extends AutoCloseable {

    /**
     * Create a new database.
     *
     * @param database the name of the new database.
     */
    void createDatabase(String database);

    /**
     * Drop a database.
     *
     * @param database the name of the database to drop.
     */
    void dropDatabase(String database);

    /**
     * Show all available databases.
     */
    List<String> showDatabases();

    /**
     * Create a retention policy.
     *
     * @param database  the name of the database.
     * @param rpConfig  the config of the retention policy
     * @param isDefault if the retention policy is the default retention policy for the database or not
     */
    void createRetentionPolicy(String database, RpConfig rpConfig, boolean isDefault);

    /**
     * Show all available retention policies.
     *
     * @param database the name of the database.
     */
    List<RetentionPolicy> showRetentionPolicies(String database);

    /**
     * Drop a retention policy.
     *
     * @param database        the name of the database.
     * @param retentionPolicy the name of the retention policy to drop.
     */
    void dropRetentionPolicy(String database, String retentionPolicy);

    /**
     * Execute a query against a database.
     *
     * @param query the query to execute.
     */
    QueryResult query(Query query);

    /**
     * Write a single point to the database.
     *
     * @param database the name of the database.
     * @param point    the point to write.
     */
    void write(String database, Point point);

    /**
     * Write points to the database.
     *
     * @param database the name of the database.
     * @param points   the points to write.
     */
    void write(String database, List<Point> points);

    /**
     * Ping the OpenGemini server
     */
    Pong ping();
}
