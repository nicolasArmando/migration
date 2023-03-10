package io.wefox.dbmigration;

import io.quarkus.runtime.StartupEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.util.List;
import java.util.stream.Collectors;

import io.smallrye.mutiny.Multi;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MigrationService {

    private static final Logger LOGGER = Logger.getLogger("MigrationService");

    private final OldDatabase oldDatabase;
    private final NewDatabase newDatabase;
    private final LocalDatabase localDatabase;


    public MigrationService(OldDatabase oldDatabase, NewDatabase newDatabase, LocalDatabase localDatabase) {
        this.oldDatabase = oldDatabase;
        this.newDatabase = newDatabase;
        this.localDatabase = localDatabase;
    }

    void onStart(@Observes StartupEvent ev) {
        this.migrate();
    }


    public void migrate() {

//        List<OldTable> listOldValue = oldDatabase.getOldTableValues().subscribe().asStream().collect(Collectors.toList());
//        LOGGER.info("Size of listOldValue:" + listOldValue.size());

        Multi<String> profileIdsInTable = newDatabase.getProfileIdsInTable();
//
//        LOGGER.info("Size of profileIdsInTable:" + profileIdsInTable.size());

        Multi<OldTable> oldTableValuesNotInProfilesIds = oldDatabase.getOldTableValuesNotInProfilesIds(profileIdsInTable);

//        List<OldTable> listOldValueWithoutProfileIds = oldTableValuesNotInProfilesIds.subscribe().asStream().collect(Collectors.toList());
//        LOGGER.info("Size of listOldValueWithoutProfileIds:" + listOldValueWithoutProfileIds.size());


        localDatabase
                .saveOldTableValueInNewTable(oldTableValuesNotInProfilesIds)
                .onFailure()
                .invoke(throwable -> LOGGER.error("Error insert", throwable))
                .subscribe().asCompletionStage();



    }

}
