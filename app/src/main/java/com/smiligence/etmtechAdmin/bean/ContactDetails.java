package com.smiligence.etmtechAdmin.bean;

public class ContactDetails {
    String whatsAppContact;
    String instagramUrl;
    String facebookUrl;
    String youTubeUrl;

    public String getYouTubeUrl() {
        return youTubeUrl;
    }

    public void setYouTubeUrl(String youTubeUrl) {
        this.youTubeUrl = youTubeUrl;
    }

    public String getEmail() {

        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    String email;

    public String getWhatsAppContact() {
        return whatsAppContact;
    }

    public void setWhatsAppContact(String whatsAppContact) {
        this.whatsAppContact = whatsAppContact;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }
}
