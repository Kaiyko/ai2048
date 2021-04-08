package com.kaykio.action;

import com.kaykio.model.Candidate;
import com.kaykio.model.GameStatus;
import com.kaykio.model.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Blue
 */
public class AiAction {

    private GameStatus grid;

    public AiAction(GameStatus grid) {
        this.grid = new GameStatus(grid.getCellMatrix());
    }

    public int getBestMove() {
        return this.iterativeDeep(50);
    }

    /**
     * 基于alpha-beta的MIN MAX搜索，进行深层迭代，搜索时间设定为0.2秒，即决策得到思考时间为0.2秒
     */
    private int iterativeDeep(long minSearchTime) {
        long start = System.currentTimeMillis();
        int depth = 0;
        int best = -1;
        do {
            SearchResult newBest = this.search(depth, -10000, 10000, 0, 0);
            if (newBest.move == -1) {
                break;
            } else {
                best = newBest.move;
            }
            depth++;
        } while (System.currentTimeMillis() - start < minSearchTime);
        System.out.println("depth -----> " + depth);
        return best;
    }

    public SearchResult search(int depth, double alpha, double beta, int positions, int cutoffs) {
        double bestScore;
        int bestMove = -1;
        SearchResult result = new SearchResult();
        int[] directions = {0, 1, 2, 3};
        //  MAX层
        if (this.grid.playerTurn) {
            bestScore = alpha;
            //  玩家遍历四个滑动方向，找出一个最好的
            for (int direction : directions) {
                GameStatus newGrid = new GameStatus(this.grid.getCellMatrix());
                if (newGrid.couldMove(direction)) {
                    positions++;
                    AiAction newAi = new AiAction(newGrid);
                    newAi.grid.playerTurn = false;
                    // 如果depth=0, 搜索到改层后不再向下搜索
                    if (depth == 0) {
                        result.move = direction;
                        result.score = newAi.evaluate();
//                        System.out.println("当前评分 -----> " + result.score);
                    } else {
                        //  如果depth>0,则继续搜索下一层，下一层为电脑做出决策的层
                        result = newAi.search(depth - 1, bestScore, beta, positions, cutoffs);
//                        if (result.score > 9900) {
//                            //  win
//                            result.score--;
//                        }
                        positions = result.positions;
                        cutoffs = result.cutoffs;
                    }

                    //  如果当前搜索分支的格局分数要好于之前得到的分数，则更新决策，同时更新bestScore，也即alpha的值
                    if (result.score > bestScore) {
                        bestScore = result.score;
                        bestMove = direction;
                    }
                    //  如果当前bestScore也即alpha>beta时，表明这个节点下不会再有更好的解，于是剪枝
                    if (bestScore > beta) {
                        cutoffs++;
                        return new SearchResult(bestMove, beta, positions, cutoffs);
                    }
                }
            }
        } else {
            //  MIN层，该层为电脑层（也就是对手）这里我们假设对手足够聪明，总是能做出使格局变到最坏的决策
            bestScore = beta;

            //  尝试给每个空闲块填入2或4， 然后计算格局的评估值
            List<Candidate> candidates = new ArrayList<Candidate>();
            List<int[]> cells = this.grid.getAvailableCells();
            int[] fill = {2, 4};
            List<Double> scores2 = new ArrayList<Double>();
            List<Double> scores4 = new ArrayList<Double>();
            for (int value : fill) {
                for (int i = 0; i < cells.size(); i++) {
                    this.grid.insertTitle(cells.get(i)[0], cells.get(i)[1], value);
                    if (value == 2) {
                        scores2.add(i, -this.grid.smoothness() + this.grid.islands());
                    }
                    if (value == 4) {
                        scores4.add(i, -this.grid.smoothness() + this.grid.islands());
                    }
                    this.grid.removeTitle(cells.get(i)[0], cells.get(i)[1]);
                }
            }

            //  找出使格局变得最坏的所有可能操作
            if (!scores2.isEmpty() && !scores4.isEmpty()) {
                double maxScore = Math.max(Collections.max(scores2), Collections.max(scores4));
                for (int value : fill) {
                    if (value == 2) {
                        for (Double fitness : scores2) {
                            if (fitness == maxScore) {
                                int index = scores2.indexOf(fitness);
                                candidates.add(new Candidate(cells.get(index)[0], cells.get(index)[1], value));
                            }
                        }
                    }
                    if (value == 4) {
                        for (Double fitness : scores4) {
                            if (fitness == maxScore) {
                                int index = scores4.indexOf(fitness);
                                candidates.add(new Candidate(cells.get(index)[0], cells.get(index)[1], value));
                            }
                        }
                    }
                }
            }

            //  然后遍历这些操作，基于这些操作向下搜索，找到使格局最坏的分支
            for (Candidate candidate : candidates) {
                int posX = candidate.x;
                int posY = candidate.y;
                int value = candidate.value;
                GameStatus newGrid = new GameStatus(this.grid.getCellMatrix());
                //  电脑即对手做出一个可能的对于电脑来说最好的（对玩家来说最坏的）决策
                newGrid.insertTitle(posX, posY, value);
                positions++;
                AiAction newAi = new AiAction(newGrid);
                //  向下搜索，下一层为MAX层，轮到玩家进行决策
                newAi.grid.playerTurn = true;
                //  这里depth没有减1是为了保证搜索到最深的层为MAX层（即玩家行动）
                result = newAi.search(depth, alpha, bestScore, positions, cutoffs);
                positions = result.positions;
                cutoffs = result.cutoffs;

                //  该层为MIN层，哪个分支的局势最不好，就选哪个分支，这里的bestScore代表beta
                if (result.score < bestScore) {
                    bestScore = result.score;
                }
                //  如果当前bestScore也即beta<alpha时，表明这个节点下不会再有更好解，于是剪枝
                if (bestScore < alpha) {
                    cutoffs++;
                    return new SearchResult(-1, alpha, positions, cutoffs);
                }
            }
        }

        return new SearchResult(bestMove, bestScore, positions, cutoffs);
    }

    /**
     * 格局评估函数
     */
    private double evaluate() {
        //  平滑性权重系数
        double smoothWeight = 0.1;
        //  单调性权重系数
        double monoWeight = 1.45;
        //  空格数权重系数
        double emptyWeight = 2.8;
        //  最大数权重系数
        double maxWeight = 1.1;
        //  最大数顶边系数
        double maxValueAroundWeight = 1.2;

        return grid.smoothness() * smoothWeight
                + grid.monotonicity() * monoWeight
                + (Math.log(this.getEmptyNum(grid.getCellMatrix()))) * emptyWeight
                + grid.maxValue() * maxWeight;
    }

    private int getEmptyNum(int[][] matrix) {
        int sum = 0;
        for (int[] ints : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (ints[j] == 0) {
                    sum++;
                }
            }
        }
        return sum;
    }


}
