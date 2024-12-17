package com.springtbatch.processor;

import com.springtbatch.domain.Product;
import com.springtbatch.exception.MyException;
import org.springframework.batch.item.ItemProcessor;

import java.util.Random;

public class FilterProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(Product product) throws Exception {
        System.out.println("FilterProductItemProcessor() executed for product " + product.getProductId());
        /**if (product.getProductPrice()>100){
            return product;
        }else {
            return null;
        }*/
        Random random = new Random();
        if (product.getProductPrice()== 500 && random.nextInt(3) == 2 ){
            System.out.println("Exception thrown");
            throw new MyException("Test exception");
        }
        return product;
    }
}
