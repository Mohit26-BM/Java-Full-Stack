package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.dao.CategoryDao;
import com.company.jdbc.exception.CategoryNotFoundException;
import com.company.jdbc.exception.DuplicateCategoryException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Category;

public class CategoryServiceImpl implements CategoryService {

    private static final int MAX_NAME_LENGTH = 80;

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public int addCategory(String categoryName)
            throws InvalidInputException,
                   DuplicateCategoryException {

        String name = InputValidator.requireText(
                categoryName, "Category name", MAX_NAME_LENGTH);

        return categoryDao.add(new Category(0, name));
    }

    @Override
    public Category getCategoryById(int categoryId)
            throws CategoryNotFoundException {

        Category category = categoryDao.findById(categoryId);

        if (category == null) {
            throw new CategoryNotFoundException(
                    "Category with ID " + categoryId + " was not found."
            );
        }

        return category;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public void updateCategory(int categoryId, String categoryName)
            throws InvalidInputException,
                   CategoryNotFoundException,
                   DuplicateCategoryException {

        String name = InputValidator.requireText(
                categoryName, "Category name", MAX_NAME_LENGTH);

        if (!categoryDao.update(new Category(categoryId, name))) {
            throw new CategoryNotFoundException(
                    "Category with ID " + categoryId + " was not found."
            );
        }
    }

    @Override
    public void deleteCategory(int categoryId)
            throws CategoryNotFoundException,
                   EntityInUseException {

        if (!categoryDao.delete(categoryId)) {
            throw new CategoryNotFoundException(
                    "Category with ID " + categoryId + " was not found."
            );
        }
    }
}
