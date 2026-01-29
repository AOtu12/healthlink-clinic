package com.healthlink.clinicsystem.repository;

import com.healthlink.clinicsystem.entity.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Long> {
    // Basic CRUD provided by JpaRepository
}
