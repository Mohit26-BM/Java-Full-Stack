package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.exception.AuthorNotFoundException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Author;

public interface AuthorService {

    int addAuthor(String authorName)
            throws InvalidInputException;

    Author getAuthorById(int authorId)
            throws AuthorNotFoundException;

    List<Author> getAllAuthors();

    void updateAuthor(int authorId, String authorName)
            throws InvalidInputException,
                   AuthorNotFoundException;

    void deleteAuthor(int authorId)
            throws AuthorNotFoundException,
                   EntityInUseException;
}
