package com.jpa.pagesort.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.jpa.pagesort.entity.Product;

public interface ProductService {
	 public List<Product> findAllProducts();
	 public List<Product> findProductsWithSorting(String field);
	 public Page<Product> findProductsWithPagination(int offset,int pageSize);
	 public Page<Product> findProductsWithPaginationAndSorting(int offset,int pageSize,String field);
}
