package com.springtbatch.domain;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;

import java.util.Arrays;
import java.util.List;

public class ProductValidator implements Validator<Product> {
    List<String> validProductCategories = Arrays.asList("Mobile Phones",
            "Tablets",
            "Televisions",
            "Sports Accessories");

    @Override
    public void validate(Product product) throws ValidationException {
        if (!validProductCategories.contains(product.getProductCategory())){
            throw new ValidationException("Invalid Product Category");
        }

        if (product.getProductPrice()> 100000){
            throw new ValidationException("Invalid Product Price");
        }
    }
}
