package com.ariel.guardian;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mikalackis on 31.5.16..
 */
public class MissionControl {

    private static MissionControl sInstance;

    private static int NUMBER_OF_CORES =
            Runtime.getRuntime().availableProcessors();

    private BlockingQueue<Runnable> mArielWorkQueue;

    // Sets the amount of time an idle thread waits before terminating
    private static final int KEEP_ALIVE_TIME = 60;
    // Sets the Time Unit to seconds
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    private ThreadPoolExecutor mArielThreadPool;

    static {
        sInstance = new MissionControl();
    }

    private MissionControl(){
        mArielWorkQueue = new LinkedBlockingQueue<Runnable>();

        mArielThreadPool = new ThreadPoolExecutor(
                NUMBER_OF_CORES,       // Initial pool size
                NUMBER_OF_CORES*2,       // Max pool size
                KEEP_ALIVE_TIME,
                KEEP_ALIVE_TIME_UNIT,
                mArielWorkQueue);
    }

    public void executeTask(final Runnable command){
        mArielThreadPool.execute(command);
    }

}
