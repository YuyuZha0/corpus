package com.github.poetry.pipeline;

import com.github.poetry.entity.GeneralChinesePoetry;
import com.github.poetry.rank.RankingScoreManager;
import lombok.NonNull;

import java.nio.file.Path;

/**
 * @author zhaoyuyu
 * @since 2020/2/4
 */
public final class RankingScorePipeline extends ForwardingPipeline {

  private static final double MIN_SCORE = 0.01;

  private final RankingScoreManager rankingScoreManager;

  public RankingScorePipeline(@NonNull Path root, Pipeline next) {
    super(RankingScorePipeline.class.getSimpleName(), next);
    this.rankingScoreManager = RankingScoreManager.create(root);
  }

  @Override
  public void process(IndexContext ctx, Iterable<GeneralChinesePoetry> poetries) {
    for (GeneralChinesePoetry poetry : poetries) {
      double score = rankingScoreManager.getRankingScore(poetry.getTitle(), poetry.getAuthor());
      poetry.setScore(Math.max(score, MIN_SCORE));
    }
    forward(ctx, poetries);
  }
}
