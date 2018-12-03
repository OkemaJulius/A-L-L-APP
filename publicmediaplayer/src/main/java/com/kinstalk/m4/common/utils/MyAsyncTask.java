package com.kinstalk.m4.common.utils;

import java.util.concurrent.Executor;

/**
 * @Description:异步任务类
 * @Version
 */
public abstract class MyAsyncTask implements Runnable {
    private static final MoreExecutor MORE_EXECUTOR = new MoreExecutor();
    private static final int CORE_POOL_SIZE = 1;

    /**
     * 异步任务类
     */
    public MyAsyncTask() {
    }

    /**
     * 外部接口，处理请求
     *
     * @param run
     * @param isLTimeOper
     */
    public static void runInBackground(Runnable run, boolean isLTimeOper) {
        if (isLTimeOper) {
            MORE_EXECUTOR.execute(run);
        } else {
            AsyncTaskAssistant.executeOnThreadPool(run);
        }

    }

    /**
     * @Description:并发执行多任务。
     * @Version
     */
    private static class MoreExecutor implements Executor {
        public static int runCount = 0;

        /**
         * @param add 同步执行，维护runCount字段
         */
        private synchronized void editRuncount(boolean add) {
            if (add) {
                runCount++;
            } else {
                runCount--;
            }
        }

        /**
         * 占用线程池CORE_POOL_SIZE-1个,如果满了立即创建thread执行;
         */
        @Override
        public synchronized void execute(final Runnable r) {
            if (runCount >= (CORE_POOL_SIZE - 1)) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        r.run();
                    }
                }).start();
            } else {
                executeTask(new Runnable() {
                    @Override
                    public void run() {
                        editRuncount(true);
                        r.run();
                        editRuncount(false);
                    }
                });
            }

        }

        /**
         * @param runtask
         */
        protected synchronized void executeTask(Runnable runtask) {
            AsyncTaskAssistant.executeOnThreadPool(runtask);
        }
    }

}
