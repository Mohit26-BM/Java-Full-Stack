package com.company.jdbc.dao;

import java.util.List;

import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.model.Author;

public interface AuthorDao {

    /*
    Returns the generated author_id.
    */
    int add(Author author);

    /*
    Returns null when no author has this ID.
    */
    Author findById(int authorId);

    List<Author> findAll();

    /*
    Returns true when exactly one row was updated.
    */
    boolean update(Author author);

    /*
    Returns true when exactly one row was deleted.
    */
    boolean delete(int authorId)
            throws EntityInUseException;
}
