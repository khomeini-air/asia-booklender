package com.asia.booklender.shared.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EntitySortField {
    ID("id"),
    CREATED_AT("createdAt"),
    UPDATED_AT("updatedAt");

    private final String fieldName;

    EntitySortField(String fieldName) {
        this.fieldName = fieldName;
    }

    @JsonValue
    public String toJson() {
        return this.fieldName;
    }

    @JsonCreator
    public static EntitySortField fromString(String value) {
        for (EntitySortField field : EntitySortField.values()) {
            if (field.fieldName.equalsIgnoreCase(value)) {
                return field;
            }
        }
        throw new IllegalArgumentException("Invalid sort field: " + value);
    }
}
