package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.CategoryNotFoundException;
import com.company.jdbc.exception.DuplicateCategoryException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Category;

public interface CategoryService {

    int addCategory(String categoryName)
            throws InvalidInputException,
                   DuplicateCategoryException;

    Category getCategoryById(int categoryId)
            throws CategoryNotFoundException;

    List<Category> getAllCategories();

    void updateCategory(int categoryId, String categoryName)
            throws InvalidInputException,
                   CategoryNotFoundException,
                   DuplicateCategoryException;

    void deleteCategory(int categoryId)
            throws CategoryNotFoundException,
                   EntityInUseException;
}
