package com.example.demo.item;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(long id) {
        super("Item with ID " + id + " was not found");
    }
}
