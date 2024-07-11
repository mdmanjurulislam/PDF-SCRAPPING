package com.PDFScrapApplication.repository;

import com.PDFScrapApplication.model.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScrappingRepo extends JpaRepository<TransactionEntity,Long> {
}
