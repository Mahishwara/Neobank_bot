package ru.pathfinder.neobank.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}
