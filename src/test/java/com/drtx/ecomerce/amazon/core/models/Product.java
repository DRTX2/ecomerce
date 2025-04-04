package com.drtx.ecomerce.amazon.core.models;

import java.util.List;

public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;// creo q podria ponerse un dato mas bajito
//    private Category category;
    private Double averageGrade;
    private List<String> pics;
}
