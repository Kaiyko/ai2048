package com.kaykio.util;

/**
 * @author Blue
 */
public class ArrayUtil {

    /**
     * 打印矩阵
     */
    public static void printMatrix(int[][] matrix) {
        System.out.print("{");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (i == matrix.length - 1 && j == matrix[0].length - 1) {
                    System.out.print(" " + matrix[i][j] + "}");
                } else if (i == 0 && j == 0) {
                    System.out.print(matrix[i][j]);
                } else if (j == matrix[0].length - 1) {
                    System.out.println(" " + matrix[i][j]);
                } else {
                    System.out.print(" " + matrix[i][j]);
                }
            }
        }
        System.out.println();
    }

    /**
     * 初始化所有元素为0
     */
    public static void intiMatrixToZero(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = 0;
            }
        }
    }

    public static int getMax(int[][] matrix) {
        int max = 0;
        for (int[] aMatrix : matrix){
            for (int j = 0; j < matrix[0].length; j++){
                if(aMatrix[j] > max){
                    max = aMatrix[j];
                }
            }
        }
        return max;
    }

    public static int[] getMaxValueIndex(int[][] matrix) {
        int[] maxIndex = new int[3];
        int max = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[] aMatrix = matrix[i];
            for (int j = 0; j < matrix[0].length; j++){
                boolean flag = aMatrix[j] > max
                        || (aMatrix[j] == max && i > maxIndex[0] && (j == 0 || j == 3 || i == 3));
                if(flag){
                    if (aMatrix[j] == max && ((maxIndex[0] == 0 && maxIndex[1] == 0)
                            || (maxIndex[0] == 0 && maxIndex[1] == 3))) {
                        continue;
                    }
                    max = aMatrix[j];
                    maxIndex[0] = i;
                    maxIndex[1] = j;
                    maxIndex[2] = max;
                }
            }
        }
        return maxIndex;
    }

    public static double getMax(double[][] matrix) {
        double max = 0;
        for (double[] aMatrix : matrix){
            for (int j = 0; j < matrix[0].length; j++){
                if(aMatrix[j] > max){
                    max = aMatrix[j];
                }
            }
        }
        return max;
    }

    /**
     * 复制矩阵内容
     */
    public static void copyMatrix(int[][] srcMatrix, int[][] destMatrix, int row, int col) {
        for (int i = 0; i < row; i++) {
            System.arraycopy(srcMatrix[i], 0, destMatrix[i], 0, col);
        }
    }

    /**
     * 判断两个数组的内容是否相等
     */
    public static boolean isEquals(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length){
            return false;
        }
        for (int i = 0; i < arr1.length; i++) {
            if (arr1[i] != arr2[i]){
                return false;
            }
        }
        return true;
    }

    /**
     * 判断两个矩阵的内容是否相等
     * 要求传入矩阵的规模一致
     */
    public static boolean isMatrixEquals(int[][] matrix1, int[][] matrix2){
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix1[0].length; j++) {
                if (matrix1[i][j] != matrix2[i][j]){
                    return false;
                }
            }
        }
        return true;
    }

    public static void reverseArray(int[] array) {
        int[] newArray = new int[array.length];
        int k = 0;
        for (int i = array.length - 1; i >= 0; i--) {
            newArray[k++] = array[i];
        }
        System.arraycopy(newArray, 0, array, 0, newArray.length);
    }

    /**
     * 将矩阵逆时针旋转90度
     */
    public static void antiClockwiseRotate90(int[][] matrix, int scale) {
        int newMatrix[][] = new int[scale][scale];
        for (int p = scale - 1, i = 0; i < scale; p--, i++) {
            for (int q = 0, j = 0; j < scale; q++, j++) {
                newMatrix[p][q] = matrix[j][i];
            }
        }
        for (int i = 0; i < scale; i++) {
            System.arraycopy(newMatrix[i], 0, matrix[i], 0, scale);
        }
    }

    /**
     * 将矩阵顺时针旋转90度
     */
    public static void clockwiseRotate90(int[][] matrix, int scale) {
        int[][] newMatrix = new int[scale][scale];
        for(int p = 0, i = 0; i < scale; p++, i++){
            for (int q = scale - 1, j = 0; j < scale; q--, j++) {
                newMatrix[p][q] = matrix[j][i];
            }
        }
        for (int i = 0; i < scale; i++) {
            System.arraycopy(newMatrix[i], 0, matrix[i], 0, scale);
        }
    }

    /**
     * 将矩阵逆时针旋转90度
     */
    public static int[][] antiClockwiseRotate90(int[][] matrix, int row, int col) {
        int[][] newMatrix = new int[col][row];
        for (int p = col - 1, i = 0; i < col; p--, i++) {
            for (int q = 0, j = 0; j < row; q++, j++) {
                newMatrix[p][q] = matrix[j][i];
            }
        }
        return newMatrix;
    }

    /**
     * 将矩阵顺时针旋转90度
     */
    public static int[][] clockwiseRotate90(int[][] matrix, int row, int col) {
        int[][] newMatrix = new int[col][row];
        for (int p = 0, i = 0; i < col; p++, i++) {
            for (int q = row - 1, j = 0; j < row; q--, j++) {
                newMatrix[p][q] = matrix[j][i];
            }
        }
        return newMatrix;
    }

}
