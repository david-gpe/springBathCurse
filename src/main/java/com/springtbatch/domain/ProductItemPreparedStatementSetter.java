package com.springtbatch.domain;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductItemPreparedStatementSetter implements ItemPreparedStatementSetter<Product> {

    @Override
    public void setValues(Product product, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(1, product.getProductId());
        preparedStatement.setString(2,product.getProductName());
        preparedStatement.setString(3,product.getProductCategory());
        preparedStatement.setInt(4,product.getProductPrice());

    }
}
