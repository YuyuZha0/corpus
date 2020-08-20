package com.github.poetry.source;

import com.github.poetry.entity.GeneralChinesePoetry;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author zhaoyuyu
 * @since 2019/11/19
 */
public interface PoetrySource extends Supplier<List<GeneralChinesePoetry>> {}
