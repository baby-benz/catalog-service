package com.wine.to.up.catalog.service.service;

import com.wine.to.up.catalog.service.domain.dto.WineDTO;
import com.wine.to.up.catalog.service.domain.entities.Grape;
import com.wine.to.up.catalog.service.domain.entities.Region;
import com.wine.to.up.catalog.service.domain.entities.Wine;
import com.wine.to.up.catalog.service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WineService implements BaseCrudService<WineDTO> {

    private final WineRepository wineRepository;
    private final ProducerRepository producerRepository;
    private final BrandRepository brandRepository;
    private final RegionRepository regionRepository;
    private final GrapeRepository grapeRepository;
    private final ColorRepository colorRepository;
    private final SugarRepository sugarRepository;

    @Override
    public List<WineDTO> readAll() {
        return StreamSupport
                .stream(wineRepository.findAll().spliterator(), false)
                .map(wine -> {
                    WineDTO wineDTO = new WineDTO();
                    wineDTO.setId(wine.getWineID());
                    wineDTO.setName(wine.getWineName());
                    wineDTO.setProducer_id(wine.getWineProducer().getProducerID());
                    wineDTO.setBrand_id(wine.getWineBrand().getBrandID());
                    wineDTO.setRegion_id(wine.getWineRegion().stream().map(Region::getRegionID).collect(Collectors.toList()));
                    wineDTO.setGrape_id(wine.getWineGrape().stream().map(Grape::getGrapeID).collect(Collectors.toList()));
                    wineDTO.setAvg(wine.getStrength());
                    wineDTO.setYear(wine.getProduction_year());
                    wineDTO.setColor(wine.getWineColor().getColorName().toUpperCase());
                    wineDTO.setSugar(wine.getWineSugar().getSugarName().toUpperCase());
                    return wineDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void create(WineDTO wineDTO) {
        Wine wine = new Wine();
        wine.setWineID(UUID.randomUUID().toString());
        wine.setWineName(wineDTO.getName());
        wine.setWineProducer(producerRepository.findByProducerID(wineDTO.getProducer_id()));
        wine.setWineBrand(brandRepository.findBrandsByBrandID(wineDTO.getBrand_id()));

        wine.setWineRegion(wineDTO.getRegion_id().stream().map(regionRepository::findByRegionID).collect(Collectors.toList()));
        wine.setWineGrape(wineDTO.getGrape_id().stream().map(grapeRepository::findByGrapeID).collect(Collectors.toList()));
        wine.setStrength(wineDTO.getAvg());
        wine.setProduction_year(wineDTO.getYear());
        wine.setWineColor(colorRepository.findByColorName(wineDTO.getColor()));
        wine.setWineSugar(sugarRepository.findBySugarName(wineDTO.getSugar()));
        wineRepository.save(wine);
    }

    @Override
    public WineDTO read(String id) {
        Wine wineByWineID = wineRepository.findWineByWineID(id);
        WineDTO wineDTO = new WineDTO();
        wineDTO.setId(wineByWineID.getWineID());
        wineDTO.setName(wineByWineID.getWineName());
        wineDTO.setProducer_id(wineByWineID.getWineProducer().getProducerID());
        wineDTO.setBrand_id(wineByWineID.getWineBrand().getBrandID());
        wineDTO.setRegion_id(wineByWineID.getWineRegion().stream().map(Region::getRegionID).collect(Collectors.toList()));
        wineDTO.setGrape_id(wineByWineID.getWineGrape().stream().map(Grape::getGrapeID).collect(Collectors.toList()));
        wineDTO.setAvg(wineByWineID.getStrength());
        wineDTO.setYear(wineByWineID.getProduction_year());
        wineDTO.setColor(wineByWineID.getWineColor().getColorName().toUpperCase());
        wineDTO.setSugar(wineByWineID.getWineSugar().getSugarName().toUpperCase());
        return wineDTO;
    }

    @Override
    public void update(String id, WineDTO wineDTO) {
        Wine wine = new Wine();
        wine.setWineID(id);
        wine.setWineName(wineDTO.getName());
        wine.setWineProducer(producerRepository.findByProducerID(wineDTO.getProducer_id()));
        wine.setWineBrand(brandRepository.findBrandsByBrandID(wineDTO.getBrand_id()));
        wine.setWineRegion(wineDTO.getRegion_id().stream().map(regionRepository::findByRegionID).collect(Collectors.toList()));
        wine.setWineGrape(wineDTO.getGrape_id().stream().map(grapeRepository::findByGrapeID).collect(Collectors.toList()));
        wine.setStrength(wineDTO.getAvg());
        wine.setProduction_year(wineDTO.getYear());
        wine.setWineColor(colorRepository.findByColorName(wineDTO.getColor()));
        wine.setWineSugar(sugarRepository.findBySugarName(wineDTO.getSugar()));
        wineRepository.save(wine);
    }

    @Override
    public void delete(String id) {
        wineRepository.deleteById(id);
    }
}
