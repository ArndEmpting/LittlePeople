package com.littlepeople.core.model;

/*
 * CONFIDENTIAL Privileged Business Information - Do Not Release.
 *   Copyright by Körber Supply Chain  Logistics GmbH   2024.
 *   <p>
 *   All rights reserved. This computer program is protected by copyright laws and
 *   international treaties. This file contains Trade Secrets of Körber Supply Chain  Logistics GmbH.
 *   With the receipt of this file, the receiver  agrees to keep strictly confidential, all
 *   Trade Secrets of Körber Supply Chain  Logistics GmbH contained herein. Copying this file, giving
 *   it to others, and the use or communication of the contents thereof, is
 *   forbidden without express authority. Offenders are liable to the payment of
 *   damages. All rights are reserved especially in the event of the grant of a
 *   patent or the registration of a utility model or design.
 *
 */
public class ProcessorStats {
    private long totalTime = 0;
    private long maxTime = 0;
    private int count = 0;

    public synchronized void addTime(long duration) {
        totalTime += duration;
        count++;
        if (duration > maxTime) {
            maxTime = duration;
        }
    }

    public synchronized double getAverageTimeMs() {
        return count == 0 ? 0 : (totalTime / 1_000_000.0) / count;
    }

    public synchronized long getMaxTimeMs() {
        return maxTime / 1_000_000;
    }

    public synchronized int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "ProcessorStats{" +
                "count=" + count +
                ", totalTime=" + totalTime +
                ", maxTime=" + maxTime +
                '}';
    }
}