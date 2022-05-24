package com.github.poetry;

import org.ansj.splitWord.analysis.IndexAnalysis;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2022/5/24
 **/
public class AnsjTest {

    @Test
    public void test(){
        String input = "准确率 * 其实这和召回本身是具有一定矛盾性的Ansj的强大之处是很巧妙的避开了这两个的冲突 。比如我们常见的歧义句“旅游和服务”->对于一般保证召回 。大家会给出的结果是“旅游 和服 服务” 对于ansj不存在跨term的分词。意思就是。召回的词只是针对精准分词之后的结果的一个细分。比较好的解决了这个问题";
        IndexAnalysis.parse(input).getTerms().forEach(System.out::println);
    }
}
