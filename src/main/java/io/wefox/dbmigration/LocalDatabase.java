package io.wefox.dbmigration;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.PreparedQuery;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class LocalDatabase {

    private final PgPool localDatabaseClient;

    private static final Logger LOGGER = Logger.getLogger("LocalDatabase");

    public LocalDatabase(@ReactiveDataSource("localDatabaseClient") PgPool localDatabaseClient) {
        this.localDatabaseClient = localDatabaseClient;
    }


    // TODO: migrate code to newDatabase, only done to test the logic
    public Uni<Void> saveOldTableValueInNewTable(Multi<OldTable> oldTableValues) {


        List<Tuple> tuples = oldTableValues.subscribe().asStream().map(oldTable -> Tuple.from(
                Stream.of(
                        UUID.randomUUID().toString(),
                        oldTable.externalId(),
                        emptyIfValue(oldTable.appMobileBuild()),
                        emptyIfValue(oldTable.appVersionOne()),
                        emptyIfValue(oldTable.mobileOsOne()),
                        emptyIfValue(oldTable.mobileOsVersion()),
                        emptyIfValue(oldTable.appMobileBuild()),
                        todayIfValue(oldTable.createdDate()),
                        todayIfValue(oldTable.modificationDate())
                ).toList()
        )).collect(Collectors.toList());


        LOGGER.info("saveOldTableValueInNewTable started");
        return localDatabaseClient.withTransaction(conn -> {


            PreparedQuery<RowSet<Row>> preparedQuery = localDatabaseClient.preparedQuery("INSERT INTO public.device (" +
                    "id," +
                    " profile_id," +
                    " app_mobile_build_version," +
                    " app_version," +
                    " mobile_os," +
                    " mobile_os_version," +
                    " device_type," +
                    " create_date," +
                    " update_date) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)");

            Uni<RowSet<Row>> rowSet = preparedQuery.executeBatch(tuples);

            LOGGER.info("added");

            return rowSet.replaceWithVoid();
        });


    }


    private LocalDateTime todayIfValue(LocalDateTime value) {

        if (value == null) {
            return LocalDateTime.now();
        }

        return value;
    }


    private String emptyIfValue(String value) {

        if (value == null) {
            return "";
        }

        if(value.length() > 20) {
            LOGGER.errorf("To big %s", value);
            return value.substring(0, 19);
        }

        return value;
    }

}
