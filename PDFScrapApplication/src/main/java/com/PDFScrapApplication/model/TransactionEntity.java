package com.PDFScrapApplication.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Habib_American_Bank")
//@AllArgsConstructor
//@NoArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id_transaction;

    private String trans_date;
    private String trans_no;
    private String contra_br;
    private String particulars;

    private String debit;
    private String credit;
    private String ex_rating;

    private String debit_amt_fc;
    private String credit_amt_fc;
    private String balance_fc;

    private String debit_amt_lc;
    private String credit_amt_lc;
    private String balance_lc;

    private String source_file;
}
