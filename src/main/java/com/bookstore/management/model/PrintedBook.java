package com.bookstore.management.model;

public class PrintedBook extends Book {

    private int pageCount;
    private String shelfCode;

    public PrintedBook() {
    }

    public PrintedBook(String id, String title, String author, String category, String isbn, String description,
                       double price, int stock, double rating, boolean featured, String accentColor, String coverTone,
                       int pageCount, String shelfCode) {
        super(id, title, author, category, isbn, description, price, stock, rating, featured, accentColor, coverTone);
        this.pageCount = pageCount;
        this.shelfCode = shelfCode;
    }

    @Override
    public String getFormatLabel() {
        return "Printed Edition";
    }

    @Override
    public String getSecondaryMetadata() {
        return pageCount + " pages • Shelf " + shelfCode;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getShelfCode() {
        return shelfCode;
    }

    public void setShelfCode(String shelfCode) {
        this.shelfCode = shelfCode;
    }
}
