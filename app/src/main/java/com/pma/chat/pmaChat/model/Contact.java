package com.pma.chat.pmaChat.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Bojan on 6/14/2017.
 */
@DatabaseTable(tableName = "contact")
public class Contact {


    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String number;

    public Contact(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
