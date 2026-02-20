package com.sfedu.touragency.service;

import java.io.*;
import java.util.*;

public interface CrudService<T, PK extends Serializable> {
    void create(T t);
    T read(PK id);
    void update(T t);
    void delete(PK id);
    List<T> findAll();
}
