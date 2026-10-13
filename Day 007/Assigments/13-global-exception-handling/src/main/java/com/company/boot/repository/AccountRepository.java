package com.company.boot.repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.company.boot.model.Account;

/** Data access for a13_accounts. No exception handling here - errors propagate. */
@Repository
public class AccountRepository {

    private static final String SELECT_COLUMNS =
            "SELECT account_id, holder_name, email, balance, created_at FROM a13_accounts";

    private static final RowMapper<Account> ROW_MAPPER = (rs, rowNum) -> new Account(
            rs.getInt("account_id"),
            rs.getString("holder_name"),
            rs.getString("email"),
            rs.getBigDecimal("balance"),
            rs.getTimestamp("created_at").toLocalDateTime());

    private final JdbcTemplate jdbcTemplate;

    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Account> findAll() {
        return jdbcTemplate.query(SELECT_COLUMNS + " ORDER BY account_id", ROW_MAPPER);
    }

    public Optional<Account> findById(int accountId) {
        return jdbcTemplate.query(SELECT_COLUMNS + " WHERE account_id = ?", ROW_MAPPER, accountId)
                .stream().findFirst();
    }

    /** Locks the row for the rest of the transaction (used by withdraw / transfer). */
    public Optional<Account> findByIdForUpdate(int accountId) {
        return jdbcTemplate.query(SELECT_COLUMNS + " WHERE account_id = ? FOR UPDATE", ROW_MAPPER, accountId)
                .stream().findFirst();
    }

    public int insert(String holderName, String email, BigDecimal balance) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO a13_accounts (holder_name, email, balance) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, holderName);
            ps.setString(2, email);
            ps.setBigDecimal(3, balance);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void updateBalance(int accountId, BigDecimal newBalance) {
        jdbcTemplate.update("UPDATE a13_accounts SET balance = ? WHERE account_id = ?", newBalance, accountId);
    }
}
