package com.github.poetry.json;

import com.google.gson.Gson;

import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 **/
public final class GsonFactory implements Supplier<Gson> {

    private static final Gson GSON = new Gson();

    @Override
    public Gson get() {
        return GSON;
    }
}
