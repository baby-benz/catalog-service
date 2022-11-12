package com.wine.to.up.catalog.service.service;

import com.google.common.primitives.Bytes;
import com.wine.to.up.catalog.service.domain.dto.WinePositionDTO;
import com.wine.to.up.catalog.service.domain.entities.WinePosition;
import com.wine.to.up.catalog.service.domain.request.SettingsRequest;
import com.wine.to.up.catalog.service.domain.request.SortByRequest;
import com.wine.to.up.catalog.service.domain.specifications.WinePositionSpecificationBuilder;
import com.wine.to.up.catalog.service.repository.ShopRepository;
import com.wine.to.up.catalog.service.repository.WinePositionRepository;
import com.wine.to.up.catalog.service.repository.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class WinePositionService implements BaseCrudService<WinePositionDTO> {

    private final WinePositionRepository winePositionRepository;
    private final WineRepository wineRepository;
    private final ShopRepository shopRepository;

    private static final Map<String, String> matrixArguments = new HashMap<>() {
        {
            put("shopSite", "shop.shopSite");
            put("producerName", "wpWine.wineProducer.producerName");
            put("brandName", "wpWine.wineBrand.brandName");
//            put("regionName", "");
//            put("countryName", "");
//            put("grapeName", "");

            put("avg", "wpWine.strength");

            put("color", "wpWine.wineColor.colorName");
            put("sugar", "wpWine.wineSugar.sugarName");

            put("year", "wpWine.production_year");
            put("price", "price");
            put("actual_price", "actualPrice");
            put("volume", "volume");
        }
    };

    public List<WinePositionDTO> readAllWithSettings(SettingsRequest settingsRequest) {
        Sort sort = null;
        if (settingsRequest.getSortBy() != null) {
            for (SortByRequest sortByRequest : settingsRequest.getSortBy()) {
                if (sort == null) {
                    sort = sortByRequest.getOrder().equals("desc") ? Sort.by(matrixArguments.get(sortByRequest.getAttribute())).descending() : Sort.by(matrixArguments.get(sortByRequest.getAttribute())).ascending();
                } else {
                    sort.and(sortByRequest.getOrder().equals("desc") ? Sort.by(matrixArguments.get(sortByRequest.getAttribute())).descending() : Sort.by(matrixArguments.get(sortByRequest.getAttribute())).ascending());
                }
            }
        }
        PageRequest pageRequest;
        if (sort != null) {
            pageRequest = PageRequest.of(settingsRequest.getFrom(), settingsRequest.getTo(), sort);
        } else {
            if (settingsRequest.getTo() < 1) {
                pageRequest = PageRequest.of(settingsRequest.getFrom(), Integer.MAX_VALUE);
            } else {
                pageRequest = PageRequest.of(settingsRequest.getFrom(), settingsRequest.getTo());
            }
        }
        WinePositionSpecificationBuilder wpSpecBuilder = new WinePositionSpecificationBuilder();
        Pattern pattern = Pattern.compile("([\\s\\S]+?)(:|<|>)([\\s\\S]+?);", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher matcher = pattern.matcher(settingsRequest.getSearchParameters() + ";");

        while (matcher.find()) {
            String keyGroup = matcher.group(1);
            String firstChar = keyGroup.substring(0, 1);

            String key;
            if (firstChar.equals("~")) {
                key = "~" + matrixArguments.get(keyGroup.substring(1));
            } else if (firstChar.equals("*")) {
                key = "*" + matrixArguments.get(keyGroup.substring(1));
            } else {
                key = matrixArguments.get(keyGroup);
            }


            wpSpecBuilder.with(key, matcher.group(2), matcher.group(3));
        }

        //unreachable
//        if (settingsRequest.getTo() < 1) {
//            return null;
//        }

        Specification<WinePosition> wpSpecification = wpSpecBuilder.build();
        return winePositionRepository.findAll(wpSpecification, pageRequest)
                .stream()
                .map(this::getWinePositionDTO)
                .collect(Collectors.toList());
    }

    private WinePositionDTO getWinePositionDTO(WinePosition winePosition) {
        WinePositionDTO winePositionDTO = new WinePositionDTO();
        winePositionDTO.setWine_position_id(winePosition.getWpId());
        winePositionDTO.setWine_id(winePosition.getWpWine().getWineID());
        winePositionDTO.setShop_id(winePosition.getShop().getShopID());
        winePositionDTO.setPrice(winePosition.getPrice());
        winePositionDTO.setActual_price(winePosition.getActualPrice());
        winePositionDTO.setLink_to_wine(winePosition.getLinkToWine());
        winePositionDTO.setVolume(winePosition.getVolume());
        winePositionDTO.setDescription(winePosition.getDescription());
        winePositionDTO.setGastronomy(winePosition.getGastronomy());
        winePositionDTO.setImage(Bytes.asList(winePosition.getImage()));
        return winePositionDTO;
    }

    @Override
    public List<WinePositionDTO> readAll() {
        return StreamSupport
                .stream(winePositionRepository.findAll().spliterator(), false)
                .map(this::getWinePositionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void create(WinePositionDTO winePositionDTO) {
        WinePosition winePosition = new WinePosition();
        winePosition.setWpId(UUID.randomUUID().toString());
        winePosition.setWpWine(wineRepository.findWineByWineID(winePositionDTO.getWine_id()));
        winePosition.setShop(shopRepository.findByShopID(winePositionDTO.getShop_id()));
        winePosition.setPrice(winePositionDTO.getPrice());
        winePosition.setActualPrice(winePositionDTO.getActual_price());
        winePosition.setLinkToWine(winePositionDTO.getLink_to_wine());
        winePosition.setVolume(winePositionDTO.getVolume());
        winePosition.setDescription(winePositionDTO.getDescription());
        winePosition.setGastronomy(winePositionDTO.getGastronomy());
        winePosition.setImage(Bytes.toArray(winePositionDTO.getImage()));
        winePositionRepository.save(winePosition);
    }

    @Override
    public WinePositionDTO read(String id) {
        WinePosition byId = winePositionRepository.findById(id).get();
        WinePositionDTO winePositionDTO = new WinePositionDTO();
        winePositionDTO.setWine_position_id(id);
        winePositionDTO.setWine_id(byId.getWpWine().getWineID());
        winePositionDTO.setShop_id(byId.getShop().getShopID());
        winePositionDTO.setPrice(byId.getPrice());
        winePositionDTO.setActual_price(byId.getActualPrice());
        winePositionDTO.setLink_to_wine(byId.getLinkToWine());
        winePositionDTO.setVolume(byId.getVolume());
        winePositionDTO.setDescription(byId.getDescription());
        winePositionDTO.setGastronomy(byId.getGastronomy());
        winePositionDTO.setImage(Bytes.asList(byId.getImage()));
        return winePositionDTO;
    }

    @Override
    public void update(String id, WinePositionDTO winePositionDTO) {

        WinePositionDTO oldWinePositionDTO = read(id);

        WinePosition winePosition = new WinePosition();
        /*
        if (oldWinePositionDTO.getActual_price() != winePositionDTO.getActual_price()) {
            UpdatePriceEvent updatePriceEvent = new UpdatePriceEvent();

            updatePriceEvent.setId(id);
            String wineID = winePositionDTO.getWine_id();
            updatePriceEvent.setName(wineRepository.findWineByWineID(wineID).getWineName());
            updatePriceEvent.setPrice(winePositionDTO.getActual_price());

            KafkaMessageSentEvent event;
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                event = KafkaMessageSentEvent.newBuilder()
                        .setMessage(objectMapper.writeValueAsString(updatePriceEvent))
                        .build();

                kafkaSendMessageService.sendMessage(event);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        */
        winePosition.setWpId(id);
        winePosition.setWpWine(wineRepository.findWineByWineID(winePositionDTO.getWine_id()));
        winePosition.setShop(shopRepository.findByShopID(winePositionDTO.getShop_id()));
        winePosition.setPrice(winePositionDTO.getPrice());
        winePosition.setActualPrice(winePositionDTO.getActual_price());
        winePosition.setLinkToWine(winePositionDTO.getLink_to_wine());
        winePosition.setVolume(winePositionDTO.getVolume());
        winePosition.setDescription(winePositionDTO.getDescription());
        winePosition.setGastronomy(winePositionDTO.getGastronomy());
        winePosition.setImage(Bytes.toArray(winePositionDTO.getImage()));
        winePositionRepository.save(winePosition);
    }

    @Override
    public void delete(String id) {
        winePositionRepository.deleteById(id);
    }
}
