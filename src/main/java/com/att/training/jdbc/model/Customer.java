package com.att.training.jdbc.model;

import java.time.LocalDate;

public record Customer(
        long id,
        String email,
        String fullName,
        LocalDate birthDate,
        Integer loyaltyPts
) {}
