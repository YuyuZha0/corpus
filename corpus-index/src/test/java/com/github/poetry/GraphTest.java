package com.github.poetry;

import com.google.common.collect.Sets;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public class GraphTest {

  @Test
  @SuppressWarnings("UnstableApiUsage")
  public void test() {

    MutableGraph<Integer> graph =
        GraphBuilder.undirected().allowsSelfLoops(false).expectedNodeCount(1).build();

    graph.addNode(1);
    graph.addNode(2);
    graph.addNode(3);

    graph.putEdge(1, 2);

    Assert.assertEquals(Sets.newHashSet(1), graph.predecessors(2));
    Assert.assertEquals(Sets.newHashSet(2), graph.predecessors(1));
    Assert.assertEquals(Sets.newHashSet(1), graph.successors(2));
    Assert.assertEquals(Sets.newHashSet(2), graph.successors(1));
  }
}
