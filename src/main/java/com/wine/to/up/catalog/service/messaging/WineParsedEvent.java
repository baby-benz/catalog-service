package com.wine.to.up.catalog.service.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WineParsedEvent {
    private List<Wine> winesList;
    private String shopLink;
}
