package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.rank.RankingScoreManager;
import lombok.NonNull;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public final class RankingScorePipeline extends ForwardingPipeline {

  private static final double MIN_SCORE = 0.01;

  private final RankingScoreManager rankingScoreManager;

  public RankingScorePipeline(@NonNull String root, Pipeline next) {
    super(RankingScorePipeline.class.getSimpleName(), next);
    this.rankingScoreManager = RankingScoreManager.create(root);
  }

  @Override
  public void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {
    for (GeneralChinesePoetry poetry : poetries) {
      double score = rankingScoreManager.getRankingScore(poetry.getTitle(), poetry.getAuthor());
      score = Math.max(score, MIN_SCORE);
      if ("句".equals(poetry.getTitle())) {
        poetry.setScore(Math.min(score, 0.9));
        continue;
      }
      poetry.setScore(score);
    }
    forward(ctx, poetries);
  }
}
