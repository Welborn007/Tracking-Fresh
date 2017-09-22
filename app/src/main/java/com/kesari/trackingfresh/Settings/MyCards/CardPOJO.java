package com.kesari.trackingfresh.Settings.MyCards;

/**
 * Created by kesari on 13/09/17.
 */

public class CardPOJO {

    private String cardNumber;
    private String FormattedCardNumber;
    private String cardholderName;
    private String cvv;
    private String expiry;
    private String card_image;
    private boolean isDefault;

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getFormattedCardNumber() {
        return FormattedCardNumber;
    }

    public void setFormattedCardNumber(String formattedCardNumber) {
        FormattedCardNumber = formattedCardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCard_image() {
        return card_image;
    }

    public void setCard_image(String card_image) {
        this.card_image = card_image;
    }
}
