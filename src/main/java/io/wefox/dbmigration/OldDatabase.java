package io.wefox.dbmigration;

import io.quarkus.reactive.datasource.ReactiveDataSource;
import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class OldDatabase {

    private final PgPool oldDatabaseClient;

    public OldDatabase(@ReactiveDataSource("oldDatabaseClient") PgPool oldDatabaseClient) {
        this.oldDatabaseClient = oldDatabaseClient;
    }


//    public Multi<OldTable> getOldTableValues() {
//        return oldDatabaseClient.query("SELECT" +
//                        " pkexternalid__c," +
//                        " mobileosone__c," +
//                        " appmobilebuildversionone__c," +
//                        " mobileosversionone__c," +
//                        " appmobileversionone__c," +
//                        " appversionone__c," +
//                        " createddate," +
//                        " lastmodifieddate" +
//                        " FROM salesforce.account").execute().onItem().transformToMulti(set -> Multi.createFrom().iterable(set))
//                .onItem().transform(toOldTable());
//
//
//    }

    public Multi<OldTable> getOldTableValuesNotInProfilesIds(Multi<String> profileIds) {

        String notIn = profileIds.subscribe().asStream().map(s -> "'" + s + "'").collect(Collectors.joining(","));

        return oldDatabaseClient.preparedQuery("SELECT" +
                        " pkexternalid__c," +
                        " mobileosone__c," +
                        " appmobilebuildversionone__c," +
                        " mobileosversionone__c," +
                        " appmobileversionone__c," +
                        " devicetypeone__c," +
                        " createddate," +
                        " lastmodifieddate" +
                        "  FROM salesforce.account" +
                        " where " +
                        "1=1 " +
                        "AND mobileosone__c is not null " +
                        "AND appmobilebuildversionone__c is not null " +
                        "AND mobileosversionone__c is not null " +
                        "AND appmobileversionone__c is not null " +
                        "AND devicetypeone__c is not null " +
                        "AND createddate is not null " +
                        "AND pkexternalid__c not in (" + notIn + ")")
                .execute()
                .onItem()
                .transformToMulti(set -> Multi.createFrom().iterable(set))
                .onItem()
                .transform(toOldTable());


    }


    private Function<Row, OldTable> toOldTable() {
        return row -> new OldTable(
                row.getString("pkexternalid__c"),
                row.getString("mobileosone__c"),
                row.getString("appmobilebuildversionone__c"),
                row.getString("mobileosversionone__c"),
                row.getString("appmobileversionone__c"),
                row.getString("devicetypeone__c"),
                row.getLocalDateTime("createddate"),
                row.getLocalDateTime("lastmodifieddate")
        );
    }


}
