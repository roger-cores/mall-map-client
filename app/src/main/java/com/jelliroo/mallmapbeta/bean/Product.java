package com.jelliroo.mallmapbeta.bean;

/**
 * Created by roger on 2/21/2017.
 */

public class Product {

    private Integer identifier;

    private String product_name;

    private Integer product_cost;

    private ClassRecord classLabel;

    public Product() {
    }

    public Product(Integer identifier, String product_name, Integer product_cost, ClassRecord classLabel) {
        this.identifier = identifier;
        this.product_name = product_name;
        this.product_cost = product_cost;
        this.classLabel = classLabel;
    }

    public Integer getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Integer identifier) {
        this.identifier = identifier;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public Integer getProduct_cost() {
        return product_cost;
    }

    public void setProduct_cost(Integer product_cost) {
        this.product_cost = product_cost;
    }

    public ClassRecord getClassLabel() {
        return classLabel;
    }

    public void setClassLabel(ClassRecord classLabel) {
        this.classLabel = classLabel;
    }
}
