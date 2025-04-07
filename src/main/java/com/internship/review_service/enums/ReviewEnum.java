package com.internship.review_service.enums;

import java.util.Arrays;
import java.util.Objects;

public interface ReviewEnum {

    /**
     * Gets the id of the enum value.
     *
     * @return the id of the enum value
     */
    int getId();

    /**
     * Finds the enum value of the given type that matches the given id.
     *
     * @param enumClass the type of the enum
     * @param id the id to search for
     * @return the enum value with the matching id
     * @throws IllegalArgumentException if no enum value with the given id is found
     */
    static <E extends Enum<E> & ReviewEnum> E fromId(Class<E> enumClass, Integer id) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(enumValue -> Objects.equals(enumValue.getId(), id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid " + enumClass.getSimpleName() + " id: " + id));
    }
}
