package io.wefox.dbmigration;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.PreparedQuery;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class NewDatabase {

    private final PgPool newDatabaseClient;

    public NewDatabase(@ReactiveDataSource("newDatabaseClient") PgPool newDatabaseClient) {
        this.newDatabaseClient = newDatabaseClient;
    }


    public Multi<String> getProfileIdsInTable() {

        return newDatabaseClient.query("SELECT" +
                        " profile_id " +
                        "  FROM public.device").execute().onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem().transform(row -> row.getString("profile_id"));
    }


}
