package com.kaykio.model;

import com.kaykio.constant.Constants;
import com.kaykio.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Blue
 */
public class GameStatus {

    private int score;
    private int[][] cellMatrix;

    public boolean playerTurn = true;

    //  上右下左
    private int[][] vectors = {
            {0, -1},
            {1, 0},
            {0, 1},
            {-1, 0}
    };

    private boolean[][] marked;

    public GameStatus(int[][] cellMatrix){
        this.cellMatrix = new int[cellMatrix.length][cellMatrix[0].length];
        ArrayUtil.copyMatrix(cellMatrix, this.cellMatrix, cellMatrix.length, cellMatrix[0].length);
    }

    public GameStatus(int score, int[][] cellMatrix) {
        this.score = score;
        this.cellMatrix = new int[cellMatrix.length][cellMatrix[0].length];
        ArrayUtil.copyMatrix(cellMatrix, this.cellMatrix, cellMatrix.length, cellMatrix[0].length);
    }

    public int getScore() {
        return score;
    }

    public int[][] getCellMatrix() {
        return cellMatrix;
    }

    private boolean isCellAvailable(int cntX, int cntY) {
        return cellMatrix[cntX][cntY] == 0;
    }

    private boolean isInBounds(int cntX, int cntY) {
        return cntX >= 0 && cntX < 4 && cntY >=0 && cntY < 4;
    }

    /**
     * 测量网格的平滑程度
     */
    public double smoothness() {
        int smoothness = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y ++) {
                if (this.cellMatrix[x][y] != 0) {
                    double value = Math.log(this.cellMatrix[x][y] / Math.log(2));
                    //  计算水平方向和垂直方向的平滑性评估值
                    for (int direction = 1; direction <= 2; direction++) {
                        int[] vector = this.vectors[direction];
                        int cntX = x, cntY = y;
                        do {
                            cntX += vector[0];
                            cntY += vector[1];
                        } while (isInBounds(cntX, cntY) && isCellAvailable(cntX, cntY));
                        if (isInBounds(cntX, cntY)) {
                            if (cellMatrix[cntX][cntY] != 0) {
                                double targetValue = Math.log(cellMatrix[cntX][cntY]) / Math.log(2);
                                smoothness -= Math.abs(value - targetValue);
                            }
                        }
                    }
                }
            }
        }
        return smoothness;
    }

    /**
     * 测量网格的单调性
     */
    public double monotonicity() {
        //  保存四个方向格局单调性的评估值
        int[] totals = {0, 0, 0, 0};

        //  左/右
        for (int x = 0; x < 4; x++) {
            int current = 0;
            int next = current + 1;
            while (next < 4) {
                while (next < 4 && this.cellMatrix[x][next] == 0) {
                    next++;
                }
                if (next >= 4) {
                    next--;
                }
                double currentValue = (this.cellMatrix[x][current] != 0) ? Math.log(this.cellMatrix[x][current]) / Math.log(2) : 0;
                double nextValue = (this.cellMatrix[x][next] != 0) ? Math.log(this.cellMatrix[x][next]) / Math.log(2) : 0;
                if (currentValue > nextValue) {
                    totals[0] += nextValue - currentValue;
                } else if(nextValue > currentValue) {
                    totals[1] += currentValue - nextValue;
                }
//                if (currentValue > nextValue) {
//                    totals[0] += currentValue - nextValue;
//                } else if (nextValue > currentValue) {
//                    totals[1] += currentValue - nextValue;
//                }
                current = next;
                next++;
            }
        }

        //  上/下
        for (int y = 0; y < 4; y++) {
            int current = 0;
            int next = current + 1;
            while (next < 4) {
                while (next < 4 && this.cellMatrix[next][y] == 0) {
                    next++;
                }
                if (next >= 4) {
                    next--;
                }
                double currentValue = (this.cellMatrix[current][y] != 0) ? Math.log(this.cellMatrix[current][y]) / Math.log(2) : 0;
                double nextValue = (this.cellMatrix[next][y] != 0) ? Math.log(this.cellMatrix[next][y]) / Math.log(2) : 0;
                if (currentValue > next) {
                    totals[2] += nextValue - currentValue;
                } else if (currentValue < next) {
                    totals[3] += currentValue - nextValue;
                }
                current = next;
                next++;
            }
        }

        //  取四个方向中最大的值为当前格局单调性的评估值
        return Math.max(totals[0], totals[1]) + Math.max(totals[2], totals[3]);
    }

    /**
     * 取最大数
     */
    public double maxValue() {
        return Math.log(ArrayUtil.getMax(cellMatrix)) / Math.log(2);
    }

    /**
     * 顶边数值
     */
    public double maxValueIndex() {
        int[] maxValueIndex = ArrayUtil.getMaxValueIndex(cellMatrix);
//        System.out.println("最大值 -----> " + maxValueIndex[0] + ", " + maxValueIndex[1] + ", " + maxValueIndex[2]);
        boolean flag = (maxValueIndex[0] == 0 && maxValueIndex[1] == 0)
                || (maxValueIndex[0] == 3 && maxValueIndex[1] == 3)
                || (maxValueIndex[0] == 0 && maxValueIndex[1] == 3)
                || (maxValueIndex[0] == 3 && maxValueIndex[1] == 0);
        // {0,0}，{3,3},{0,3},{3,0}
        if (flag) {
//            System.out.println("顶级");
            return (Math.log(maxValueIndex[2]) / Math.log(2)) + 0.5;
        }
        if (maxValueIndex[0] == 0 || maxValueIndex[0] == 3
            || maxValueIndex[1] ==0 || maxValueIndex[1] == 3) {
//            System.out.println("次级");
            return Math.log(maxValueIndex[2]) / Math.log(2);
        }
//        System.out.println("最菜");
        return  - (Math.log(maxValueIndex[2]) / Math.log(2)) / 2;
    }

    /**
     * 判断能否移动
     */
    public boolean couldMove(int direction) {
        int[][] preMatrix = new int[cellMatrix.length][cellMatrix[0].length];
        ArrayUtil.copyMatrix(cellMatrix, preMatrix, 4, 4);

        boolean moved = false;

        switch (direction) {
            case Constants.ACTION_UP:
                ArrayUtil.antiClockwiseRotate90(cellMatrix, 4);
                for (int i = 0; i < 4; i++) {
                    merge(cellMatrix[i], Constants.ACTION_UP);
                }
                ArrayUtil.clockwiseRotate90(cellMatrix, 4);
                break;
            case Constants.ACTION_RIGHT:
                for (int i = 0; i < 4; i++) {
                    merge(cellMatrix[i], Constants.ACTION_RIGHT);
                }
                break;
            case Constants.ACTION_DOWN:
                ArrayUtil.antiClockwiseRotate90(cellMatrix, 4);
                for (int i = 0; i < 4; i++) {
                    merge(cellMatrix[i], Constants.ACTION_DOWN);
                }
                ArrayUtil.clockwiseRotate90(cellMatrix, 4);
                break;
            case Constants.ACTION_LEFT:
                for (int i = 0; i < 4; i ++) {
                    merge(cellMatrix[i], Constants.ACTION_LEFT);
                }
            default:break;
        }
        if (!ArrayUtil.isMatrixEquals(preMatrix, cellMatrix)) {
            moved = true;
            this.playerTurn = false;
        }
        return moved;
    }

    /**
     * 合并相同的数字
     */
    private void merge(int[] row, int action) {
        int[] mergeRow = new int[row.length];
        System.arraycopy(row, 0, mergeRow, 0, row.length);

        int[] moveRow = new int[row.length];
        if (action == Constants.ACTION_LEFT || action == Constants.ACTION_UP) {
            //  进行合并，如 2 2 4 4，合并后未 4 0 8 0
            for (int i = 0; i < mergeRow.length - 1; i++) {
                if (mergeRow[i] == 0) {
                    continue;
                }
                for (int j = i + 1; j < mergeRow.length; j++) {
                    if (mergeRow[j] == 0) {
                        continue;
                    }
                    if (mergeRow[i] == mergeRow[j]) {
                        mergeRow[i] *= 2;
                        mergeRow[j] = 0;
                    }
                    break;
                }
            }
            int k = 0;
            // 移动，如 4 0 8 0，移动后为 4 8 0 0
            for (int j : mergeRow) {
                if (j != 0) {
                    moveRow[k++] = j;
                }
            }
        }
        if (action == Constants.ACTION_RIGHT || action == Constants.ACTION_DOWN) {
            //  进行合并，如 2 2 4 4，合并后未 4 0 8 0
            for (int i = mergeRow.length - 1; i > 0; i--) {
                if (mergeRow[i] == 0) {
                    continue;
                }
                for (int j = i - 1; j > 0; j--) {
                    if (mergeRow[j] == 0) {
                        continue;
                    }
                    if (mergeRow[i] == mergeRow[j]) {
                        mergeRow[i] *= 2;
                        mergeRow[j] = 0;
                    }
                    break;
                }
            }
            int k = row.length - 1;
            // 移动，如 4 0 8 0，移动后为 0 0 4 8
            for (int i = k; i >= 0; i--) {
                if (mergeRow[i] != 0) {
                    moveRow[k--] = mergeRow[i];
                }
            }
        }

        System.arraycopy(moveRow, 0, row, 0, moveRow.length);
    }

    public List<int[]> getAvailableCells() {
        List<int[]> cells = new ArrayList<int[]>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (cellMatrix[x][y] == 0) {
                    int[] tmp = new int[]{x, y};
                    cells.add(tmp);
                }
            }
        }
        return cells;
    }

    public void insertTitle(int x, int y, int value) {
        this.cellMatrix[x][y] = value;
    }

    /**
     * 递归调用计算当前格局连通块个数
     */
    public int islands() {
        int islands = 0;

        marked = new boolean[4][4];
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (this.cellMatrix[x][y] != 0) {
                    this.marked[x][y] = false;
                }
            }
        }
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (this.cellMatrix[x][y] != 0 && !this.marked[x][y]) {
                    islands++;
                    mark(x, y, this.cellMatrix[x][y]);
                }
            }
        }
        return islands;
    }

    private void mark(int x, int y, int value) {
        if (x >= 0 && x <= 3 && y >= 0 && y <= 3 && (this.cellMatrix[x][y] != 0)
        && (this.cellMatrix[x][y] == value) && (!this.marked[x][y])) {
            this.marked[x][y] = true;
            for (int direction = 0; direction < 4; direction++) {
                int[] vector = this.vectors[direction];
                mark(x + vector[0], y + vector[1], value);
            }
        }
    }

    public void removeTitle(int x, int y) {
        this.cellMatrix[x][y] = 0;
    }

}
