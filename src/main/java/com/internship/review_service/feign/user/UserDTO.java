package com.internship.review_service.feign.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

        private Long id;

        private String name;

        private String surname;

        private String email;
}
