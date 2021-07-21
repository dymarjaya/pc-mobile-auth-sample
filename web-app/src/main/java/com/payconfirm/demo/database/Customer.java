package com.payconfirm.demo.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class Customer {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(nullable = false, unique = true)
    private String alias;

    @Column
    private String activation_code;

    @Column
    private String user_id;

    public Customer() {
    }

    @Override
    public String toString() {
        return "Customer Database: [" +
                "id=" + id +
                ", alias='" + alias + '\'' +
                ", activation_code='" + activation_code + '\'' +
                ", user_id='" + user_id + '\'' +
                ']';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getActivation_code() {
        return activation_code;
    }

    public void setActivation_code(String activation_code) {
        this.activation_code = activation_code;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

