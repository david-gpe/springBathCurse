package com.springtbatch.domain;

public class OSProduct extends Product{

    private int taxPercent;
    private String sku;
    private int shippingRate;


    public int getTaxPercent() {return taxPercent;}

    public void setTaxPercent(int taxPercent) {this.taxPercent = taxPercent;}

    public String getSku() {return sku;}

    public void setSku(String sku) {this.sku = sku;}

    public int getShippingRate() {return shippingRate;}

    public void setShippingRate(int shippingRate) {this.shippingRate = shippingRate;}

    @Override
    public String toString() {
        return "OSProduct{" +
                "taxPercent=" + taxPercent +
                ", sku='" + sku + '\'' +
                ", shippingRate=" + shippingRate +
                '}';
    }
}
