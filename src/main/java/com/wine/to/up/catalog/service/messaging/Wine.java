package com.wine.to.up.catalog.service.messaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Wine {
    public enum Color {
        WHITE, ROSE, ORANGE, RED, UNRECOGNIZED
    }

    public enum Sugar {
        DRY, MEDIUM_DRY, MEDIUM, SWEET, UNRECOGNIZED
    }

    private String name;

    private String country;

    private String brand;

    private int year;

    private float oldPrice;

    private float newPrice;

    private String manufacturer;

    private String link;

    private String imageLink;

    private String gastronomy;

    private float volume;

    private List<String> regions;

    private List<String> grapeSorts;

    private float strength;

    private Color color;

    private Sugar sugar;

    private String description;
}