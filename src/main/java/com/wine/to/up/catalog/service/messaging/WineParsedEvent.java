package com.wine.to.up.catalog.service.messaging;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WineParsedEvent {
    private List<Wine> winesList;
    private String shopLink;
}
