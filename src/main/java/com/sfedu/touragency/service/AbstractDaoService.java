package com.sfedu.touragency.service;

import com.sfedu.touragency.persistence.Dao;

import java.io.*;
import java.util.*;

/**
 * Abstract CrudService implementation that delegates invocations to the dao
 * @param <T> Entity type parameter
 * @param <PK> Primary key type
 */
public abstract class AbstractDaoService<T, PK extends Serializable> implements CrudService<T, PK> {
    @Override
    public void delete(PK id) {
        getBackingDao().delete(id);
    }

    @Override
    public List<T> findAll() {
        return getBackingDao().findAll();
    }

    @Override
    public T read(PK id) {
        return getBackingDao().read(id);
    }

    @Override
    public void update(T t) {
        getBackingDao().update(t);
    }

    public abstract Dao<T, PK> getBackingDao();

}
