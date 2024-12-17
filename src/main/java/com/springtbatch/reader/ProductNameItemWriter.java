package com.springtbatch.reader;


/**Clase comentada tras el upgrade a springBatch 5.0.0*/
public class ProductNameItemWriter /**implements ItemWriter<Product> */{
    /**@Override
    public void write(List<? extends Product> list) throws Exception {
        System.out.println("Chunk-processing Started");
        list.forEach(System.out::println);
        System.out.println("Chunk-processing Ended");
    }**/
}
