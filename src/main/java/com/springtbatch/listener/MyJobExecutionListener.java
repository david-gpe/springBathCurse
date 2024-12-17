package com.springtbatch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.item.ExecutionContext;

public class MyJobExecutionListener {

    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters: " + jobExecution.getJobParameters());
        System.out.println("Job Start Time: " + jobExecution.getStartTime());
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        jobExecutionContext.put("jk1","XYZ");
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        System.out.println("Job Name: " + jobExecution.getJobInstance().getJobName());
        System.out.println("Job Parameters: " + jobExecution.getJobParameters());
        System.out.println("Job End Time: " + jobExecution.getEndTime());
    }

}
