package com.company.boot.dto;

/** The experiments - each maps to one TransferService method. */
public enum TransferMode {

    /** @Transactional, unchecked exception           -> expected: ROLLBACK */
    WITH_TRANSACTION,

    /** no @Transactional, unchecked exception        -> expected: debit stays, money lost */
    WITHOUT_TRANSACTION,

    /** @Transactional, CHECKED exception              -> expected: COMMIT (default rule) */
    CHECKED_DEFAULT,

    /** @Transactional(rollbackFor = ...), CHECKED     -> expected: ROLLBACK */
    CHECKED_ROLLBACK_FOR,

    /** non-transactional method calls this.@Transactional method -> proxy bypassed */
    SELF_INVOCATION,

    /** @Transactional but the exception is caught inside -> COMMIT of the half-done work */
    SWALLOWED_EXCEPTION
}
