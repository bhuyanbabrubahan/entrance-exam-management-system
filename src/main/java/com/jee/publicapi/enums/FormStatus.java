package com.jee.publicapi.enums;

import java.util.Arrays;

public enum FormStatus {

    NOT_STARTED,
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    REOPENED,
    APPROVED,
    REJECTED,
    UNKNOWN;   // always keep last

    /**
     * Safe conversion from String (DB/API) to Enum
     */
    public static FormStatus fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return NOT_STARTED;   // better default than UNKNOWN
        }

        return Arrays.stream(FormStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(UNKNOWN);
    }
}