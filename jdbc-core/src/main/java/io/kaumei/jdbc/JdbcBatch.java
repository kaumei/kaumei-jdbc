/*
 * SPDX-FileCopyrightText: 2025 kaumei.io
 * SPDX-License-Identifier: Apache-2.0
 */

package io.kaumei.jdbc;

public interface JdbcBatch extends AutoCloseable {

    /**
     * @return the current batch size, which can not be changed
     */
    int bachSize();

    /**
     * @return the current batch count, the number of statements not send to the database
     */
    int countBatch();

    /**
     * @return the batch count, the number of statements send to the database (not including the current batch count)
     */
    int countAll();

    /**
     * Will call the JDBC function `clearParameters` on the statement
     */
    void clearParameters();

    /**
     * Will call the JDBC function `clearBatch` on the statement
     */
    void clearBatch();

    /**
     * @return Will execute the current batch and return an int array for every item in the batch
     */
    int[] executeBatch();

    void close();

    // ------------------------------------------------------------------------

    static Boolean mapExecuteBatchResultToBoolean(int[] updateCounts) {
        for (int count : updateCounts) {
            if (count != 0) {
                return true;
            }
        }
        return false;
    }

    static Integer mapExecuteBatchResultToToSum(int[] updateCounts) {
        var updateSum = 0;
        for (int count : updateCounts) {
            updateSum += count;
        }
        return updateSum;
    }
}