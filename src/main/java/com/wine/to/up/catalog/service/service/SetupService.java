package com.wine.to.up.catalog.service.service;

import com.wine.to.up.catalog.service.messaging.Wine;
import com.wine.to.up.catalog.service.messaging.WineParsedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SetupService {
    private final static String SETTINGS_PATH = "src/main/resources/init/settings.csv";
    private final WineSaveService saveService;

//    @EventListener(ApplicationReadyEvent.class)
    public void setUpDataForService() {
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of(SETTINGS_PATH));
            Stream<String> lines = reader.lines();

            lines.forEach(s -> {
                if (!s.contains("URL")) {
                    parseStringToDestination(s);
                }
            });

        } catch (IOException e) {
            System.err.println("Can't find resource file");
            e.printStackTrace();
        }
    }

    private void parseStringToDestination(String inputSting) {
        List<String> items = getSubstringsFromString(inputSting);
        WineParsedEvent event = convertArrayToWineEvent(items);
        saveService.save(event);
    }

    private List<String> getSubstringsFromString(String string) {
        StringBuilder builder = new StringBuilder();
        List<String> result = new ArrayList<>(20);
        int currentPos = 0;
        int left_bound = -1;

        while (currentPos < string.length()) {
            if (string.charAt(currentPos) == '\"') {
                if (left_bound == -1) {
                    left_bound = currentPos;
                } else {
                    if (currentPos + 1 == string.length() ||
                            (string.charAt(currentPos + 1) == ',' && string.charAt(currentPos + 2) == '"')) {
                        result.add(builder.toString());
                        builder.setLength(0);
                        left_bound = -1;
                    }
                }
            } else {
                if (string.charAt(currentPos) != ','
                        || (string.charAt(currentPos) == ',' && left_bound != -1)) {
                    builder.append(string.charAt(currentPos));
                }
            }
            currentPos++;
        }
        return result;
    }

    private WineParsedEvent convertArrayToWineEvent(List<String> wineInputList) {
        Wine wine = initNewWine(wineInputList);
        WineParsedEvent wineParsedEvent = new WineParsedEvent();
        wineParsedEvent.setWinesList(Arrays.asList(wine));
        wineParsedEvent.setShopLink(getShopLinkFromImageLink(wineInputList.get(9)));
        return wineParsedEvent;
    }

    private String getShopLinkFromImageLink(String s) {
        int pos = s.indexOf("/", 10);
        return s.substring(0, pos + 1);
    }

    private Wine initNewWine(List<String> wineStrings) {
        Wine wine = new Wine();

        wine.setCountry(wineStrings.get(3)); //COUNTRY
        wine.setNewPrice(parseFloatOrZero(wineStrings.get(12))); //PRICE
        wine.setOldPrice(parseFloatOrZero(wineStrings.get(12))); //PRICE
        wine.setImageLink(wineStrings.get(9)); //IMAGEURL
        wine.setDescription(wineStrings.get(5)); //DESCRIPTION
        wine.setGastronomy(wineStrings.get(6)); //FOODPAIRING
        wine.setLink(wineStrings.get(18)); // URL
        wine.setYear(parseIntOrZero(wineStrings.get(4))); //YEAR
        wine.setBrand(wineStrings.get(1)); //BRAND
        wine.setManufacturer(wineStrings.get(10)); //MANUFACTURER
        wine.setVolume(parseFloatOrZero(wineStrings.get(19))); //VOLUME
        wine.setName(wineStrings.get(11)); //NAME
        wine.setStrength(getStrength(wineStrings.get(15))); // STRENGTH

        wine.setColor(getColor(wineStrings.get(2))); //COLOR
        wine.setSugar(getSugar(wineStrings.get(16))); //SUGAR

        wine.setGrapeSorts(getGrapeSorts(wineStrings.get(7))); //GRAPE;
        wine.setRegions(getRegions(wineStrings.get(14))); //REGION

        return wine;
    }

    private List<String> getRegions(String str) {
        return Arrays.asList(str.split("\\s*,\\s*"));
    }

    private List<String> getGrapeSorts(String str) {
        return List.of(str);
    }

    private Wine.Sugar getSugar(String s) {
        switch (s) {
            case ("Сухое"): {
                return Wine.Sugar.DRY;
            }
            case ("Полусухое"): {
                return Wine.Sugar.MEDIUM_DRY;
            }
            case ("Полусладкое"): {
                return Wine.Sugar.MEDIUM;
            }
            case ("Сладкое"): {
                return Wine.Sugar.SWEET;
            }
            default: {
                return Wine.Sugar.UNRECOGNIZED;
            }
        }
    }

    private Wine.Color getColor(String s) {
        switch (s) {
            case ("Красное"): {
                return Wine.Color.RED;
            }
            case ("Оранжевое"): {
                return Wine.Color.ORANGE;
            }
            case ("Розовое"): {
                return Wine.Color.ROSE;
            }
            case ("Белое"): {
                return Wine.Color.WHITE;
            }
            default: {
                return Wine.Color.UNRECOGNIZED;
            }
        }
    }

    private float getStrength(String percentString) {
        if (!percentString.isEmpty()) {
            percentString = percentString.trim();
            if (percentString.contains("Безалкогольное")) {
                return 0.0f;
            } else {
                return Float.parseFloat(percentString.replace("%", ""));
            }
        } else {
            return 0.0f;
        }
    }

    private float parseFloatOrZero(String floatString) {
        if (!floatString.isEmpty()) {
            if (floatString.equals("Безалкогольное - 0.0")) {
                return 0.0f;
            } else {
                return Float.parseFloat(floatString);
            }

        } else {
            return 0.0f;
        }
    }

    private int parseIntOrZero(String intString) {
        if (!intString.isEmpty()) {
            return Integer.parseInt(intString);
        } else {
            return 0;
        }
    }
}
