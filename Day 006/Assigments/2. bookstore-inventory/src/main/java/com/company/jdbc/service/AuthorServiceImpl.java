package com.company.jdbc.service;

import java.util.List;

import com.company.jdbc.dao.AuthorDao;
import com.company.jdbc.exception.AuthorNotFoundException;
import com.company.jdbc.exception.EntityInUseException;
import com.company.jdbc.exception.InvalidInputException;
import com.company.jdbc.model.Author;

public class AuthorServiceImpl implements AuthorService {

    private static final int MAX_NAME_LENGTH = 120;

    private final AuthorDao authorDao;

    public AuthorServiceImpl(AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Override
    public int addAuthor(String authorName)
            throws InvalidInputException {

        String name = InputValidator.requireText(
                authorName, "Author name", MAX_NAME_LENGTH);

        return authorDao.add(new Author(0, name));
    }

    @Override
    public Author getAuthorById(int authorId)
            throws AuthorNotFoundException {

        Author author = authorDao.findById(authorId);

        if (author == null) {
            throw new AuthorNotFoundException(
                    "Author with ID " + authorId + " was not found."
            );
        }

        return author;
    }

    @Override
    public List<Author> getAllAuthors() {
        return authorDao.findAll();
    }

    @Override
    public void updateAuthor(int authorId, String authorName)
            throws InvalidInputException,
                   AuthorNotFoundException {

        String name = InputValidator.requireText(
                authorName, "Author name", MAX_NAME_LENGTH);

        if (!authorDao.update(new Author(authorId, name))) {
            throw new AuthorNotFoundException(
                    "Author with ID " + authorId + " was not found."
            );
        }
    }

    @Override
    public void deleteAuthor(int authorId)
            throws AuthorNotFoundException,
                   EntityInUseException {

        if (!authorDao.delete(authorId)) {
            throw new AuthorNotFoundException(
                    "Author with ID " + authorId + " was not found."
            );
        }
    }
}
