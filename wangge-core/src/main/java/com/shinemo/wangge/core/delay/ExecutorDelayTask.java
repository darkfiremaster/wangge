package com.shinemo.wangge.core.delay;

import org.springframework.context.ApplicationContext;

public class ExecutorDelayTask implements Runnable {

    private ApplicationContext context;

    private DelayJob delayJob;

    public ExecutorDelayTask(ApplicationContext context, DelayJob delayJob) {
        this.context = context;
        this.delayJob = delayJob;
    }

    @Override
    public void run() {
        DelayJobExecutor service = (DelayJobExecutor) context.getBean(delayJob.getClazz());
        service.execute(delayJob);
    }
}