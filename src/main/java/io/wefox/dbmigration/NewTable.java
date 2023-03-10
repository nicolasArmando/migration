package io.wefox.dbmigration;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record NewTable(UUID id,
                       String profileId,
                       String appMobileBuildVersion,
                       String appVersion,
                       String mobileOS,
                       String mobileOSVersion,
                       String deviceType,
                       LocalDateTime creationDate,
                       LocalDateTime updateDate
) {}
