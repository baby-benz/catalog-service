package com.wine.to.up.catalog.service.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.wine.to.up.catalog.service.domain.entities.Wine;
import com.wine.to.up.catalog.service.domain.entities.WinePosition;
import com.wine.to.up.catalog.service.domain.response.WinePositionResponse;
import com.wine.to.up.catalog.service.domain.response.WineResponse;
import com.wine.to.up.catalog.service.mapper.controller2service.WinePositionControllerToWinePositionService;
import com.wine.to.up.catalog.service.repository.WinePositionRepository;
import com.wine.to.up.catalog.service.repository.WineRepository;
import com.wine.to.up.catalog.service.service.WinePositionService;
import com.wine.to.up.catalog.service.service.WineService;
import com.wine.to.up.catalog.service.utils.PdfGenerator;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
@Validated
@Slf4j
@Api(value = "PdfController", description = "Pdf controller")
public class PdfController {
    private final WinePositionService winePositionService;
    private final WinePositionRepository winePositionRepository;
    private final WineRepository wineRepository;
    private final WineService wineService;
    private final WinePositionControllerToWinePositionService converter;
    private final PdfGenerator pdfGenerator;

    @GetMapping("/{id}")
    public byte[] getWineById(@Valid @PathVariable(name = "id") String wpId) throws IOException, DocumentException {
        WinePositionResponse winePositionResponse = converter.convert(winePositionService.read(wpId));
        Wine wine = wineRepository.findWineByWineID(winePositionResponse.getWine_id());
        return pdfGenerator.generateWinePdf(winePositionResponse, wine);
    }
}
