package com.wine.to.up.catalog.service.service;

import com.wine.to.up.catalog.service.domain.entities.*;
import com.wine.to.up.catalog.service.repository.*;
import com.wine.to.up.catalog.service.messaging.WineParsedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
public class WineSaveService {
    private final ShopRepository shopRepository;
    private final WineRepository wineRepository;
    private final WinePositionRepository winePositionRepository;
    private final BrandRepository brandRepository;
    private final GrapeRepository grapeRepository;
    private final ProducerRepository producerRepository;
    private final RegionRepository regionRepository;
    private final ColorRepository colorRepository;
    private final SugarRepository sugarRepository;

    private final String PRODUCER_NOT_PRESENTED = "PRODUCER_NOT_PRESENTED";
    private final String BRAND_NOT_PRESENTED = "BRAND_NOT_PRESENTED";
    private final String COLOR_NOT_PRESENTED = "COLOR_NOT_PRESENTED";
    private final String SUGAR_NOT_PRESENTED = "SUGAR_NOT_PRESENTED";
    private final String GRAPE_NOT_PRESENTED = "GRAPE_NOT_PRESENTED";
    private final String REGION_NOT_PRESENTED = "REGION_NOT_PRESENTED";
    private final String COUNTRY_NOT_PRESENTED = "COUNTRY_NOT_PRESENTED";
    private final String SHOP_NOT_PRESENTED = "SHOP_NOT_PRESENTED";
    private final String SHOP_LINK_NOT_PRESENTED = "SHOP_LINK_NOT_PRESENTED";
    private final byte[] IMAGE_NOT_PRESENTED = {7, 7, 7};
    private final String GASTRONOMY_NOT_PRESENTED = "GASTRONOMY_NOT_PRESENTED";
    private final String DESCRIPTION_NOT_PRESENTED = "DESCRIPTION_NOT_PRESENTED";
    private final float CAPACITY_NOT_PRESENTED = (float) -1.0;

    public void save(WineParsedEvent wineParsedEvent) {
        wineParsedEvent.getWinesList()
                .forEach(parserWine -> {
                    try {
                        Wine byWineName = wineRepository.findByWineName(parserWine.getName());
                        log.info(byWineName == null ? "Wine {}\n not exists" : "Wine {}\n exists", parserWine);
                        if (byWineName == null) {
                            String brandName = parserWine.getBrand();
                            if (brandName == null || brandName.equals("")) {
                                brandName = BRAND_NOT_PRESENTED;
                            }
                            Brand brandByBrandName = brandRepository.findBrandByBrandName(brandName);
                            if (brandByBrandName == null) {
                                Brand brand = new Brand();
                                brand.setBrandName(brandName);
                                brand.setBrandID(UUID.randomUUID().toString());
                                brand.setBrandWines(new ArrayList<>());
                                brandRepository.save(brand);
                                brandByBrandName = brand;
                                log.info("New brand with name {} created", brandName);
                            }

                            String colorName = parserWine.getColor().name();
                            if (colorName.isEmpty()) {
                                colorName = COLOR_NOT_PRESENTED;
                            }
                            Color colorByColorName = colorRepository.findByColorName(colorName);
                            if (colorByColorName == null) {
                                Color color = new Color();
                                color.setColorName(colorName);
                                color.setColorID(UUID.randomUUID().toString());
                                color.setColorWines(new ArrayList<>());
                                colorRepository.save(color);
                                colorByColorName = color;
                                log.info("New color with name {} created", colorName);
                            }

                            String producerName = parserWine.getManufacturer();
                            if (producerName == null || producerName.equals("")) {
                                producerName = PRODUCER_NOT_PRESENTED;
                            }
                            Producer producerByProducerName = producerRepository.findByProducerName(producerName);
                            if (producerByProducerName == null) {
                                Producer producer = new Producer();
                                producer.setProducerName(producerName);
                                producer.setProducerID(UUID.randomUUID().toString());
                                producer.setProducerWines(new ArrayList<>());
                                producerRepository.save(producer);
                                producerByProducerName = producer;
                                log.info("New producer with name {} created", producerName);
                            }

                            String sugarName = parserWine.getSugar().name();
                            if (sugarName.isEmpty()) {
                                sugarName = SUGAR_NOT_PRESENTED;
                            }
                            Sugar sugarBySugarName = sugarRepository.findBySugarName(sugarName);
                            if (sugarBySugarName == null) {
                                Sugar sugar = new Sugar();
                                sugar.setSugarName(sugarName);
                                sugar.setSugarID(UUID.randomUUID().toString());
                                sugar.setSugarWines(new ArrayList<>());
                                sugarRepository.save(sugar);
                                sugarBySugarName = sugar;
                                log.info("New sugar with name {} created", sugarName);
                            }

                            List<Region> regionsOfWine = new ArrayList<>();
                            parserWine.getRegions().forEach(region -> {
                                String regionTrue = (region == null || region.isEmpty()) ? REGION_NOT_PRESENTED : region;
                                Region byRegionName = regionRepository.findByRegionName(regionTrue);
                                if (byRegionName == null) {
                                    Region reg = new Region();
                                    reg.setRegionID(UUID.randomUUID().toString());
                                    String country = parserWine.getCountry();
                                    if (country == null || country.isEmpty()) {
                                        country = COUNTRY_NOT_PRESENTED;
                                    }
                                    reg.setRegionCountry(country);
                                    reg.setRegionName(regionTrue);
                                    reg.setRegionWines(new ArrayList<>());
                                    regionRepository.save(reg);
                                    byRegionName = reg;
                                    log.info("New region with name {} created", region);
                                }
                                regionsOfWine.add(byRegionName);
                            });

                            List<Grape> grapesOfWine = new ArrayList<>();
                            parserWine.getGrapeSorts().forEach(grape -> {
                                String grapeTrue = (grape == null || grape.isEmpty()) ? GRAPE_NOT_PRESENTED : grape;
                                Grape byGrapeName = grapeRepository.findByGrapeName(grapeTrue);
                                if (byGrapeName == null) {
                                    Grape gr = new Grape();
                                    gr.setGrapeID(UUID.randomUUID().toString());
                                    gr.setGrapeName(grapeTrue);
                                    gr.setGrapeWines(new ArrayList<>());
                                    grapeRepository.save(gr);
                                    byGrapeName = gr;
                                    log.info("New grape with name {} created", grape);
                                }
                                grapesOfWine.add(byGrapeName);
                            });

                            Wine wine = new Wine();
                            wine.setWineID(UUID.randomUUID().toString());
                            wine.setWineName(parserWine.getName());
                            wine.setProduction_year(parserWine.getYear());
                            wine.setStrength(parserWine.getStrength());

                            wine.setWineBrand(brandByBrandName);
                            wine.setWineColor(colorByColorName);
                            wine.setWineSugar(sugarBySugarName);
                            wine.setWineProducer(producerByProducerName);

                            wine.setWineRegion(regionsOfWine);
                            wine.setWineGrape(grapesOfWine);

                            wine.setWinePositions(new ArrayList<>());

                            brandByBrandName.getBrandWines().add(wine);

                            colorByColorName.getColorWines().add(wine);

                            sugarBySugarName.getSugarWines().add(wine);

                            producerByProducerName.getProducerWines().add(wine);

                            regionsOfWine.forEach(region -> region.getRegionWines().add(wine));

                            grapesOfWine.forEach(grape -> grape.getGrapeWines().add(wine));

                            byWineName = wine;
                            wineRepository.save(wine);

                        }
                        String shopLink = wineParsedEvent.getShopLink();
                        if (shopLink == null || shopLink.equals("")) {
                            shopLink = SHOP_NOT_PRESENTED;
                        }
                        Shop byShopSite = shopRepository.findByShopSite(shopLink);
                        if (byShopSite == null) {
                            Shop shop = new Shop();
                            shop.setShopID(UUID.randomUUID().toString());
                            shop.setShopSite(shopLink);
                            shop.setWinePositions(new ArrayList<>());
                            shopRepository.save(shop);
                            byShopSite = shop;
                            log.info("New shop with link {} created", shopLink);
                        }


                        List<WinePosition> allByShopAndWpWine = winePositionRepository.findAllByShopAndWpWine(byShopSite, byWineName);

                        if (!allByShopAndWpWine.isEmpty()) {
                            for (WinePosition winePosition : allByShopAndWpWine) {
                                if (parserWine.getLink().isEmpty()){
                                    winePosition.setLinkToWine(SHOP_LINK_NOT_PRESENTED);
                                } else {
                                    winePosition.setLinkToWine(parserWine.getLink());
                                }

                                if (parserWine.getImageLink().isEmpty()){
                                    winePosition.setImage(IMAGE_NOT_PRESENTED);
                                }else {
                                    winePosition.setImage(parserWine.getImageLink().getBytes());
                                }

                                if (parserWine.getGastronomy().isEmpty()){
                                    winePosition.setGastronomy(GASTRONOMY_NOT_PRESENTED);
                                }else {
                                    winePosition.setGastronomy(parserWine.getGastronomy());
                                }

                                if (parserWine.getDescription().isEmpty()){
                                    winePosition.setGastronomy(DESCRIPTION_NOT_PRESENTED);
                                }else {
                                    winePosition.setDescription(parserWine.getDescription());
                                }

                                if (parserWine.getVolume() == 0f){
                                    winePosition.setVolume(CAPACITY_NOT_PRESENTED);
                                }else {
                                    winePosition.setVolume(parserWine.getVolume());
                                }

                                winePosition.setPrice(parserWine.getOldPrice());
                                winePosition.setActualPrice(parserWine.getNewPrice());

                                winePositionRepository.save(winePosition);
                            }
                        } else {

                            WinePosition winePosition = new WinePosition();
                            winePosition.setWpId(UUID.randomUUID().toString());

                            winePosition.setShop(byShopSite);
                            winePosition.setWpWine(byWineName);

                            winePosition.setLinkToWine(parserWine.getLink());
                            winePosition.setImage(parserWine.getImageLink().getBytes());
                            winePosition.setGastronomy(parserWine.getGastronomy());
                            winePosition.setDescription(parserWine.getDescription());
                            winePosition.setVolume(parserWine.getVolume());
                            winePosition.setPrice(parserWine.getOldPrice());
                            winePosition.setActualPrice(parserWine.getNewPrice());

                            log.info("Wine position of " + winePosition.getWpWine().getWineName()
                                    + " of " + winePosition.getShop().getShopSite() + " shop saved");

                            winePositionRepository.save(winePosition);
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage());
                        log.error(e.toString());
                        log.error((Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).reduce((x, y) -> x + "\n" + y).get()));
                    }

                });
        log.info("Processing wine finished");
    }

    public void associateWineWithProducer(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isProducerPresented = parserWine.getManufacturer() != null;
        Producer producer;
        if (isProducerPresented) {
            producer = getProducerAssociatedWithWine(parserWine.getManufacturer(), wine);
        } else {
            producer = getProducerAssociatedWithWine(PRODUCER_NOT_PRESENTED, wine);
        }
        wine.setWineProducer(producer);
    }

    public void associateWineWithBrand(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isBrandPresented = parserWine.getBrand() != null;
        Brand brand;
        if (isBrandPresented) {
            brand = getBrandAssociatedWithWine(parserWine.getBrand(), wine);
        } else {
            brand = getBrandAssociatedWithWine(BRAND_NOT_PRESENTED, wine);
        }
        wine.setWineBrand(brand);
    }

    public void associateWineWithColor(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isColorPresented = parserWine.getColor() != null;
        Color color;
        if (isColorPresented) {
            color = getColorAssociatedWithWine(parserWine.getColor().name(), wine);
        } else {
            color = getColorAssociatedWithWine(COLOR_NOT_PRESENTED, wine);
        }
        wine.setWineColor(color);
    }

    public void associateWineWithSugar(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isSugarPresented = parserWine.getSugar() != null;
        Sugar sugar;
        if (isSugarPresented) {
            sugar = getSugarAssociatedWithWine(parserWine.getSugar().name(), wine);
        } else {
            sugar = getSugarAssociatedWithWine(SUGAR_NOT_PRESENTED, wine);
        }
        wine.setWineSugar(sugar);
    }

    public void associateWineWithGrapes(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isGrapeListPresented = parserWine.getGrapeSorts() != null;
        List<Grape> grapes = new ArrayList<>();
        if (isGrapeListPresented) {
            boolean isGrapeListEmpty = parserWine.getGrapeSorts().isEmpty();
            if (!isGrapeListEmpty) {
                parserWine.getGrapeSorts().forEach(x -> grapes.add(getGrapeAssociatedWithWine(x, wine)));
            }
        } else {
            grapes.add(getGrapeAssociatedWithWine(GRAPE_NOT_PRESENTED, wine));
        }
        wine.setWineGrape(grapes);
    }

    public void associateWineWithRegions(Wine wine, com.wine.to.up.catalog.service.messaging.Wine parserWine) {
        boolean isRegionListPresented = parserWine.getRegions() != null;
        List<Region> regions = new ArrayList<>();
        if (isRegionListPresented) {
            boolean isRegionListEmpty = parserWine.getRegions().isEmpty();
            if (!isRegionListEmpty) {
                parserWine.getRegions().forEach(x -> regions.add(getRegionAssociatedWithWine(x, parserWine.getCountry(), wine)));
            }
        } else {
            regions.add(getRegionAssociatedWithWine(REGION_NOT_PRESENTED, COUNTRY_NOT_PRESENTED, wine));
        }
        wine.setWineRegion(regions);
    }

    public void associateWinePositionWithShop(WinePosition winePosition, com.wine.to.up.catalog.service.messaging.WineParsedEvent wineParsedEvent) {
        boolean isShopPresented = wineParsedEvent.getShopLink() != null;
        Shop shop;
        if (isShopPresented) {
            shop = getShopAssociatedWithWinePosition(wineParsedEvent.getShopLink(), winePosition);
        } else {
            shop = getShopAssociatedWithWinePosition(SHOP_NOT_PRESENTED, winePosition);
        }
        winePosition.setShop(shop);
    }

    public boolean isWineExists(String wineName) {
        return wineRepository.findByWineName(wineName) != null;
    }

    public Wine createWine(String wineName) {
        Wine wine = new Wine();
        wine.setWineID(UUID.randomUUID().toString());
        wine.setWineName(wineName);
        wine.setWinePositions(new ArrayList<>());
        log.info("Вино с названием {} создан", wineName);
        return wine;
    }

    public Wine getWineAssociatedWithWinePosition(String wineName, WinePosition winePosition) {
        boolean isWineExists = isWineExists(wineName);
        log.info("Вино с названием " + wineName + (isWineExists ? " " : " не ") + "существует");
        Wine byWineName = isWineExists ? wineRepository.findByWineName(wineName) : createWine(wineName);
        winePosition.setWpWine(byWineName);
        wineRepository.save(byWineName);
        byWineName.getWinePositions().add(winePosition);
        wineRepository.save(byWineName);
        winePositionRepository.save(winePosition);
        return byWineName;
    }

    public boolean isShopExists(String shopSite) {
        return shopRepository.findByShopSite(shopSite) != null;
    }

    public Shop createShop(String shopSite) {
        Shop shop = new Shop();
        shop.setShopSite(shopSite);
        shop.setShopID(UUID.randomUUID().toString());
        shop.setWinePositions(new ArrayList<>());
        log.info("Магазин с ссылкой {} создан", shopSite);
        return shop;
    }

    public Shop getShopAssociatedWithWinePosition(String shopSite, WinePosition winePosition) {
        boolean isShopExists = isShopExists(shopSite);
        log.info("Магазин с ссылкой " + shopSite + (isShopExists ? " " : " не ") + "существует");
        Shop byShopSite = isShopExists ? shopRepository.findByShopSite(shopSite) : createShop(shopSite);
        byShopSite.getWinePositions().add(winePosition);
        shopRepository.save(byShopSite);
        return byShopSite;
    }

    public boolean isBrandExists(String brandName) {
        return brandRepository.findBrandByBrandName(brandName) != null;
    }

    public Brand createBrand(String brandName) {
        Brand brand = new Brand();
        brand.setBrandName(brandName);
        brand.setBrandID(UUID.randomUUID().toString());
        brand.setBrandWines(new ArrayList<>());
        log.info("Бренд с названием {} создан", brandName);
        return brand;
    }

    public Brand getBrandAssociatedWithWine(String brandName, Wine wine) {
        boolean isBrandExists = isBrandExists(brandName);
        log.info("Бренд с названием " + brandName + (isBrandExists ? " " : " не ") + "существует");
        Brand byBrandName = isBrandExists ? brandRepository.findBrandByBrandName(brandName) : createBrand(brandName);
        byBrandName.getBrandWines().add(wine);
        brandRepository.save(byBrandName);
        return byBrandName;
    }

    public boolean isGrapeExists(String grapeName) {
        return grapeRepository.findByGrapeName(grapeName) != null;
    }

    public Grape createGrape(String grapeName) {
        Grape grape = new Grape();
        grape.setGrapeName(grapeName);
        grape.setGrapeID(UUID.randomUUID().toString());
        grape.setGrapeWines(new ArrayList<>());
        log.info("Сорт с названием {} создан", grapeName);
        return grape;
    }

    public Grape getGrapeAssociatedWithWine(String grapeName, Wine wine) {
        boolean isGrapeExists = isGrapeExists(grapeName);
        log.info("Сорт с названием " + grapeName + (isGrapeExists ? " " : " не ") + "существует");
        Grape byGrapeName = isGrapeExists ? grapeRepository.findByGrapeName(grapeName) : createGrape(grapeName);
        byGrapeName.getGrapeWines().add(wine);
        grapeRepository.save(byGrapeName);
        return byGrapeName;
    }

    public boolean isProducerExists(String producerName) {
        return producerRepository.findByProducerName(producerName) != null;
    }

    public Producer createProducer(String producerName) {
        Producer producer = new Producer();
        producer.setProducerName(producerName);
        producer.setProducerID(UUID.randomUUID().toString());
        producer.setProducerWines(new ArrayList<>());
        log.info("Производитель с названием {} создан", producerName);
        return producer;
    }

    public Producer getProducerAssociatedWithWine(String producerName, Wine wine) {
        boolean isProducerExists = isProducerExists(producerName);
        log.info("Производитель с названием " + producerName + (isProducerExists ? " " : " не ") + "существует");
        Producer byProducerName = isProducerExists ? producerRepository.findByProducerName(producerName) : createProducer(producerName);
        byProducerName.getProducerWines().add(wine);
        producerRepository.save(byProducerName);
        return byProducerName;
    }

    public boolean isRegionExists(String regionName) {
        return regionRepository.findByRegionName(regionName) != null;
    }

    public Region createRegion(String regionName, String countryName) {
        Region region = new Region();
        region.setRegionName(regionName);
        region.setRegionID(UUID.randomUUID().toString());
        region.setRegionCountry(countryName);
        region.setRegionWines(new ArrayList<>());
        log.info("Регион с названием {} создан", regionName);
        return region;
    }

    public Region getRegionAssociatedWithWine(String regionName, String countryName, Wine wine) {
        boolean isRegionExists = isRegionExists(regionName);
        log.info("Регион с названием " + regionName + (isRegionExists ? " " : " не ") + "существует");
        Region byRegionName = isRegionExists ? regionRepository.findByRegionName(regionName) : createRegion(regionName, countryName);
        byRegionName.getRegionWines().add(wine);
        regionRepository.save(byRegionName);
        return byRegionName;
    }

    public boolean isColorExists(String colorName) {
        return colorRepository.findByColorName(colorName) != null;
    }

    public Color createColor(String colorName) {
        Color color = new Color();
        color.setColorName(colorName);
        color.setColorID(UUID.randomUUID().toString());
        color.setColorWines(new ArrayList<>());
        log.info("Цвет с названием {} создан", colorName);
        return color;
    }

    public Color getColorAssociatedWithWine(String colorName, Wine wine) {
        boolean isColorExists = isColorExists(colorName);
        log.info("Цвета с названием " + colorName + (isColorExists ? " " : " не ") + "существует");
        Color byColorName = isColorExists ? colorRepository.findByColorName(colorName) : createColor(colorName);
        byColorName.getColorWines().add(wine);
        colorRepository.save(byColorName);
        return byColorName;
    }

    public boolean isSugarExists(String sugarName) {
        return sugarRepository.findBySugarName(sugarName) != null;
    }

    public Sugar createSugar(String sugarName) {
        Sugar sugar = new Sugar();
        sugar.setSugarName(sugarName);
        sugar.setSugarID(UUID.randomUUID().toString());
        sugar.setSugarWines(new ArrayList<>());
        log.info("Сахар с названием {} создан", sugarName);
        return sugar;
    }

    public Sugar getSugarAssociatedWithWine(String sugarName, Wine wine) {
        boolean isSugarExists = isSugarExists(sugarName);
        log.info("Сахар с названием " + sugarName + (isSugarExists ? " " : " не ") + "существует");
        Sugar bySugarName = isSugarExists ? sugarRepository.findBySugarName(sugarName) : createSugar(sugarName);
        bySugarName.getSugarWines().add(wine);
        sugarRepository.save(bySugarName);
        return bySugarName;
    }
}
