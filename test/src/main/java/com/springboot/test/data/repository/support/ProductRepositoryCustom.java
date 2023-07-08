package com.springboot.test.data.repository.support;

import com.springboot.test.data.entity.Product;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findByName(String name);
}
