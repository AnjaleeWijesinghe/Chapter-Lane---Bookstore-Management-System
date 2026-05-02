package com.bookstore.management.model;

public class DigitalBook extends Book {

    private String fileFormat;
    private double fileSizeMb;

    public DigitalBook() {
    }

    public DigitalBook(String id, String title, String author, String category, String isbn, String description,
                       double price, int stock, double rating, boolean featured, String accentColor, String coverTone,
                       String fileFormat, double fileSizeMb) {
        super(id, title, author, category, isbn, description, price, stock, rating, featured, accentColor, coverTone);
        this.fileFormat = fileFormat;
        this.fileSizeMb = fileSizeMb;
    }

    @Override
    public String getFormatLabel() {
        return "Digital Edition";
    }

    @Override
    public String getSecondaryMetadata() {
        return fileFormat.toUpperCase() + " • " + fileSizeMb + " MB";
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public void setFileSizeMb(double fileSizeMb) {
        this.fileSizeMb = fileSizeMb;
    }
}
