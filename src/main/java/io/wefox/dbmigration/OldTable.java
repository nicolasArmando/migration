package io.wefox.dbmigration;

import java.time.LocalDateTime;
import java.util.Date;

public record OldTable(
        String externalId,
        String mobileOsOne,
        String appMobileBuild,
        String mobileOsVersion,
        String appMobileVersion,
        String deviceTypeOne,
        LocalDateTime createdDate,
        LocalDateTime modificationDate
        ) {}
