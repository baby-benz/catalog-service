package com.wine.to.up.catalog.service.controller;

import com.itextpdf.text.DocumentException;
import com.wine.to.up.catalog.service.domain.entities.Wine;
import com.wine.to.up.catalog.service.domain.response.WinePositionResponse;
import com.wine.to.up.catalog.service.mapper.controller2service.WinePositionControllerToWinePositionService;
import com.wine.to.up.catalog.service.repository.WineRepository;
import com.wine.to.up.catalog.service.service.WinePositionService;
import com.wine.to.up.catalog.service.utils.PdfGenerator;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
@Validated
@Api(value = "PdfController", description = "Pdf controller")
public class PdfController {
    private final WinePositionService winePositionService;
    private final WineRepository wineRepository;
    private final WinePositionControllerToWinePositionService converter;
    private final PdfGenerator pdfGenerator;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> getWineById(@Valid @PathVariable(name = "id") String wpId) throws DocumentException {
        WinePositionResponse winePositionResponse = converter.convert(winePositionService.read(wpId));
        Wine wine = wineRepository.findWineByWineID(winePositionResponse.getWine_id());

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(wine.getWineName())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        byte[] pdfBytes = pdfGenerator.generateWinePdf(winePositionResponse, wine);

        return ResponseEntity
                .ok()
                .contentLength(pdfBytes.length)
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdfBytes));
    }
}
