package com.github.poetry.transform;

import com.github.poetry.entity.GeneralChinesePoetry;

import java.util.function.Function;

/**
 * @author zhaoyuyu
 * @since 2019/11/27
 */
public interface PoetryTransformer<T> extends Function<T, GeneralChinesePoetry> {}
