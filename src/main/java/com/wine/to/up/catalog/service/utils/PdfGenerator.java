package com.wine.to.up.catalog.service.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.wine.to.up.catalog.service.domain.entities.Wine;
import com.wine.to.up.catalog.service.domain.response.WinePositionResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGenerator {
    private final String FONT = "/assets/fonts/arial.ttf";
    private final BaseFont bf = BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    private final Font font1 = new Font(bf,15,Font.NORMAL);
    private final Font bold = new Font(bf,20,Font.BOLD);

    public PdfGenerator() throws IOException, DocumentException {
    }

    public byte[] generateWinePdf(WinePositionResponse winePositionResponse, Wine wine) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 0.75F, 0.75F, 0.75F, 0.75F);
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();
        addMetaData(document);
        addTitlePage(document, winePositionResponse, wine);
        document.close();
        return byteArrayOutputStream.toByteArray();
    }
    private void addMetaData(Document document) {
        document.addTitle("Wine details");
        document.addSubject("From our cool app");
        document.addKeywords("Wine, Alcohol, Shopping");
        document.addAuthor("Cool App Inc.");
        document.addCreator("Cool App Inc.");
    }

    private void addTitlePage(Document document, WinePositionResponse winePositionResponse, Wine wine)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        addEmptyLine(preface, 1);

        Paragraph prefaceLine1 = new Paragraph("Это вино подходит вам на 1%", font1);
        prefaceLine1.setAlignment(Element.ALIGN_CENTER);
        preface.add(prefaceLine1);

        addEmptyLine(preface, 1);
        Paragraph prefaceLine2 = new Paragraph(wine.getWineName(), bold);
        prefaceLine2.setAlignment(Element.ALIGN_CENTER);
        preface.add(prefaceLine2);

        addEmptyLine(preface, 1);
        Paragraph prefaceLine3 = new Paragraph(
                wine.getWineGrape().get(0).getGrapeName() + " " + wine.getProduction_year() + " г.",
                bold);
        prefaceLine3.setAlignment(Element.ALIGN_CENTER);
        preface.add(prefaceLine3);

        addEmptyLine(preface, 6);
        String region = wine.getWineRegion().get(0).getRegionCountry();
        String resString = region + ", " + wine.getWineSugar().getSugarName() + ", " + wine.getWineColor().getColorName() + ", "+ wine.getStrength() + "%";
        float actual_price = winePositionResponse.getActual_price();
        float old_price = winePositionResponse.getPrice();
        String wineDescription = winePositionResponse.getDescription();
        String wineDescription2 = winePositionResponse.getGastronomy();
        document.add(preface);

        Paragraph descriptionParagraph = new Paragraph(wineDescription, font1);
        descriptionParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
        descriptionParagraph.setIndentationLeft(30);
        descriptionParagraph.setIndentationRight(30);
        document.add(descriptionParagraph);

        Paragraph lastPreface = new Paragraph();
        lastPreface.setAlignment(Element.ALIGN_JUSTIFIED);
        lastPreface.setIndentationLeft(30);
        lastPreface.setIndentationRight(30);
        addEmptyLine(lastPreface, 3);
        lastPreface.add(new Paragraph(wineDescription2, font1));

        addEmptyLine(lastPreface, 3);
        lastPreface.add(new Paragraph(resString, bold));
        Paragraph price_paragraph = new Paragraph();
        price_paragraph.setFont(font1);
        if (actual_price == old_price) {
            price_paragraph.add(old_price + " рублей");
        } else {
            price_paragraph.add("Старая цена: " + old_price + "\nЦена со скидкой: " + actual_price);
        }
        addEmptyLine(lastPreface, 4);
        lastPreface.add(price_paragraph);

        document.add(lastPreface);
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

}