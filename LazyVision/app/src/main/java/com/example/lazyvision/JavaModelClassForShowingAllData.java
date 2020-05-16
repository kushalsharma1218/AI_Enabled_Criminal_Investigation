package com.example.lazyvision;

public class JavaModelClassForShowingAllData {

    private String imageURL;
    private String label;

    public JavaModelClassForShowingAllData(){}
    public JavaModelClassForShowingAllData(String imageURL, String label) {
        this.imageURL = imageURL;
        this.label = label;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
