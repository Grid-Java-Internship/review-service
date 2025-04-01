package com.internship.review_service.model.converter;

import com.internship.review_service.enums.ReviewType;
import com.internship.review_service.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

class EnumConverterTest {

    private EnumConverter enumConverter;

    @BeforeEach
    void beforeEach() {
        enumConverter = new EnumConverter();
    }

    @Test
    void testConvertToDatabaseColumn() {
        ReviewType mockType = mock(ReviewType.class);
        when(mockType.getId()).thenReturn(1);

        Integer result = enumConverter.convertToDatabaseColumn(mockType);
        assertEquals(1, result);
    }

    @Test
    void testConvertToDatabaseColumn_Null() {
        Integer result = enumConverter.convertToDatabaseColumn(null);
        assertNull(result);
    }


    @Test
    void testConvertToEntityAttribute() {
        try (MockedStatic<ReviewType> mockedStatic = mockStatic(ReviewType.class)) {
            ReviewType mockType = mock(ReviewType.class);
            mockedStatic.when(() -> ReviewType.fromId(1)).thenReturn(mockType);

            ReviewType result = enumConverter.convertToEntityAttribute(1);
            assertEquals(mockType, result);
        }
    }

    @Test
    void testConvertToEntityAttribute_Null() {
        ReviewType result = enumConverter.convertToEntityAttribute(null);
        assertNull(result);
    }
}