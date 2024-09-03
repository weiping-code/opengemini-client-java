package io.opengemini.client.common;

import io.opengemini.client.api.BaseConfiguration;
import io.opengemini.client.api.OpenGeminiSyncClient;
import io.opengemini.client.api.Point;
import io.opengemini.client.api.Pong;
import io.opengemini.client.api.Query;
import io.opengemini.client.api.QueryResult;
import io.opengemini.client.api.RetentionPolicy;
import io.opengemini.client.api.RpConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.StringJoiner;

public abstract class BaseSyncClient extends BaseClient implements OpenGeminiSyncClient {

    public BaseSyncClient(BaseConfiguration conf) {
        super(conf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase(String database) {
        String command = CommandFactory.createDatabase(database);
        Query query = new Query(command);
        executePostQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropDatabase(String database) {
        String command = CommandFactory.dropDatabase(database);
        Query query = new Query(command);
        executePostQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> showDatabases() {
        String command = CommandFactory.showDatabases();
        Query query = new Query(command);
        return ResultMapper.toDatabases(executeQuery(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createRetentionPolicy(String database, RpConfig rpConfig, boolean isDefault) {
        String command = CommandFactory.createRetentionPolicy(database, rpConfig, isDefault);
        Query query = new Query(command);
        executePostQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RetentionPolicy> showRetentionPolicies(String database) {
        if (StringUtils.isBlank(database)) {
            return null;
        }

        String command = CommandFactory.showRetentionPolicies(database);
        Query query = new Query(command);
        query.setDatabase(database);
        return ResultMapper.toRetentionPolicies(executeQuery(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropRetentionPolicy(String database, String retentionPolicy) {
        String command = CommandFactory.dropRetentionPolicy(database, retentionPolicy);
        Query query = new Query(command);
        executePostQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryResult query(Query query) {
        return executeQuery(query);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String database, Point point) {
        String body = point.lineProtocol();
        executeWrite(database, body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(String database, List<Point> points) {
        if (points.isEmpty()) {
            return;
        }
        StringJoiner sj = new StringJoiner("\n");
        points.forEach(point -> sj.add(point.lineProtocol()));
        executeWrite(database, sj.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pong ping() {
        return executePing();
    }

    /**
     * The implementation class needs to implement this method to execute a GET query call.
     *
     * @param query the query to execute.
     */
    protected abstract QueryResult executeQuery(Query query);

    /**
     * The implementation class needs to implement this method to execute a POST query call.
     *
     * @param query the query to execute.
     */
    protected abstract QueryResult executePostQuery(Query query);

    /**
     * The implementation class needs to implement this method to execute a write call.
     *
     * @param database     the name of the database.
     * @param lineProtocol the line protocol string to write.
     */
    protected abstract void executeWrite(String database, String lineProtocol);

    /**
     * The implementation class needs to implement this method to execute a ping call.
     */
    protected abstract Pong executePing();

}
