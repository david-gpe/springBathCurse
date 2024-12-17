package com.springtbatch.listener;

import com.springtbatch.domain.OSProduct;
import com.springtbatch.domain.Product;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;

import java.io.FileWriter;
import java.io.IOException;

public class MySkipListener implements SkipListener<Product, OSProduct> {

    @Override
    public void onSkipInRead(Throwable t) {
        if (t instanceof FlatFileParseException){
            System.out.println("Skipped item:- ");
            System.out.println(((FlatFileParseException)t).getInput());
            writeToFile(((FlatFileParseException)t).getInput());
        }
    }

    @Override
    public void onSkipInWrite(OSProduct item, Throwable t) {
        SkipListener.super.onSkipInWrite(item, t);
    }

    @Override
    public void onSkipInProcess(Product item, Throwable t) {
        System.out.println("Skipped item:- ");
        System.out.println(item);
        writeToFile(item.toString());
    }

    public void writeToFile(String data) {
        try {
            FileWriter fileWriter = new FileWriter("rejected/Project_Details_Rejected.txt", true);
            fileWriter.write(data+ "\n");
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
