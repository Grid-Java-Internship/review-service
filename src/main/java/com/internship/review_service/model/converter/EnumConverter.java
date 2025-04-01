package com.internship.review_service.model.converter;

import com.internship.review_service.enums.ReviewType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EnumConverter implements AttributeConverter<ReviewType, Integer> {

    /**
     * Converts the given ReviewType enum to its corresponding integer ID.
     *
     * @param reviewType the ReviewType enum to be converted
     * @return the integer ID corresponding to the given ReviewType, or null if the ReviewType is null
     */
    @Override
    public Integer convertToDatabaseColumn(ReviewType reviewType) {
        if (reviewType == null) {
            return null;
        }
        return reviewType.getId();
    }

    /**
     * Converts the given integer ID to its corresponding ReviewType enum.
     *
     * @param id the integer ID representing a ReviewType
     * @return the ReviewType corresponding to the given ID, or null if the ID is null
     * @throws IllegalArgumentException if the ID does not correspond to any ReviewType
     */
    @Override
    public ReviewType convertToEntityAttribute(Integer id) {
        if (id == null) {
            return null;
        }
        return ReviewType.fromId(id);
    }
}
