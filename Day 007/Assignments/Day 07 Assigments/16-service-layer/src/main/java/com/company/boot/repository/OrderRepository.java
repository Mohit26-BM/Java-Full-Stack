package com.company.boot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.boot.entity.PurchaseOrder;

/** REPOSITORY LAYER - persistence only. */
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long> {
}
