package com.payconfirm.demo.database;

import javax.persistence.*;

@Entity
@Table
public class Transaction {
    @Id
    @GeneratedValue
    private Integer id;

    @Column
    private String userid;

    @Column
    private String transactionid;

    @Column
    private String status;

    public Transaction() {
    }

    @Override
    public String toString() {
        return "Transaction Database: [" +
                "id=" + id +
                ", user_id='" + userid + '\'' +
                ", transaction_id='" + transactionid + '\'' +
                ", status='" + status + '\'' +
                ']';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
