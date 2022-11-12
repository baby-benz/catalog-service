package com.wine.to.up.catalog.service.repository;

import com.wine.to.up.catalog.service.domain.entities.Color;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ColorRepository extends CrudRepository<Color, String> {
    Color findByColorName(String colorName);
}
