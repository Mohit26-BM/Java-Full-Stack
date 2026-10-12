package com.company.jdbc.dao;

/*
MySQL error codes used to turn constraint violations
into clear business exceptions.
*/
final class MySqlErrorCodes {

    /*
    Duplicate entry 'x' for key '...'  (UNIQUE / PRIMARY KEY)
    */
    static final int DUPLICATE_KEY = 1062;

    /*
    Cannot delete or update a parent row: a foreign key constraint fails
    (e.g. deleting an author who still has books)
    */
    static final int ROW_IS_REFERENCED = 1451;

    private MySqlErrorCodes() {
    }
}
