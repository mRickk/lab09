package it.unibo.oop.workers02;

import java.util.LinkedList;
import java.util.List;

public class MultiThreadedMatrix implements SumMatrix {

    private int numThread;

    public MultiThreadedMatrix(final int n) {
        this.numThread = n;
    }

    static class Worker extends Thread {

        final private double[][] matrix;
        final private int rows;
        final private int cols;
        final private int startLine;
        final private int nRowstoRead;        
        private double sum; 

        /**
         * Build a new Worker
         * 
         * @param matrix
         *          the matrix to sum
         * @param startLine
         *          the row where the Worker starts to sum
         * @param nRowstoRead
         *          the number of rows the Worker has to sum
         */
        public Worker(final double[][] matrix, final int startLine, final int nRowstoRead) {
            this.matrix = matrix;
            this.rows = matrix.length;
            this.cols = matrix[0].length;
            this.startLine = startLine;
            this.nRowstoRead = nRowstoRead;
            this.sum = 0;
        }

        @Override
        public void run() {
            System.out.println("Working form row " + this.startLine
                    + " to row " + (this.startLine + this.nRowstoRead));
            for (int m = this.startLine; m < this.rows && m < this.startLine+this.nRowstoRead; m++) {
                for (int n = 0; n < this.cols; n++) {
                    this.sum += matrix[m][n];
                }
            }
        }

        public double getResult() {
            return this.sum;
        }
        
    }

    @Override
    public double sum(double[][] matrix) {

        final int rows = matrix.length;
        final int nRowstoRead = (int)(rows / this.numThread) + (rows % this.numThread);
        final List<Worker> workerList = new LinkedList<>();

        for (int m = 0; m < rows; m += nRowstoRead) {
            workerList.add(new Worker(matrix, m, nRowstoRead));
        }

        for (final Worker w : workerList) {
            w.start();          
        }

        double sum = 0;
        for (final Worker w : workerList) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return sum;
    }
    
}
