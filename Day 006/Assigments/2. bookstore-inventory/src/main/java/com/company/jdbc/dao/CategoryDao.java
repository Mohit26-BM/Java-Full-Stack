package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.exception.DuplicateCategoryException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.model.Category;

public interface CategoryDao {

    /*
    Returns the generated category_id.
    */
    int add(Category category)
            throws DuplicateCategoryException;

    /*
    Returns null when no category has this ID.
    */
    Category findById(int categoryId);

    List<Category> findAll();

    /*
    Returns true when exactly one row was updated.
    */
    boolean update(Category category)
            throws DuplicateCategoryException;

    /*
    Returns true when exactly one row was deleted.
    */
    boolean delete(int categoryId)
            throws EntityInUseException;
}
