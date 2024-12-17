package com.springtbatch.processor;

import com.springtbatch.domain.OSProduct;
import com.springtbatch.domain.Product;
import org.springframework.batch.item.ItemProcessor;

public class TransformProductItemProcessor implements ItemProcessor<Product, OSProduct> {
    @Override
    public OSProduct process(Product product) throws Exception {
        System.out.println("TransformProductItemProcessor() executed for product " + product.getProductId());
        /**Integer price = product.getProductPrice();
        product.setProductPrice((int) (price - ((0.1) * price)));*/
        OSProduct osProduct = new OSProduct();
        osProduct.setProductId(product.getProductId());
        osProduct.setProductName(product.getProductName());
        osProduct.setProductCategory(product.getProductCategory());
        osProduct.setProductPrice(product.getProductPrice());
        osProduct.setTaxPercent(product.getProductCategory().equals("Sports Accessories")? 5:18);
        osProduct.setSku(product.getProductCategory().substring(0,3) + product.getProductId());
        osProduct.setShippingRate(product.getProductPrice()<1000 ? 75 : 0);
        /**if (product.getProductPrice() == 500){
            throw new Exception("Test Exception");
        }*/
        return osProduct;
    }
}
