package ru.javawebinar.topjava.model;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractMeal {
    private final int id;

    public AbstractMeal(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
