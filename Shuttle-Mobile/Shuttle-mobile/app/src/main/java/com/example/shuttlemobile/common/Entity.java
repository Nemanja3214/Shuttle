package com.example.shuttlemobile.common;

import java.io.Serializable;

public class Entity implements Serializable {
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
