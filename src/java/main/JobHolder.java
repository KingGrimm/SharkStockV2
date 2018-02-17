/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import jobs.resetDailyProbeNumberJob;
import jobs.testJob;
import jobs.timeToEarnJob;
import jobs.updateSharesJob;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 *
 * @author Kingu
 */
public class JobHolder {
    private Scheduler scheduler=null;
    
    private JobHolder(){
        
    }
    
    public JobHolder(Scheduler scheduler){
        this.scheduler=scheduler;
        testJobTrigger();
        //resetDailyprobeNumberTrigger();
        //updateSharesTrigger();
        timeToEarnTrigger();
    }
    
     private void testJobTrigger() {
        try {
            JobDetail job = newJob(testJob.class).withIdentity("TestJob", "TestJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("TestJobTrigger", "TestJob")
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(120)
                            .repeatForever())
                    .forJob(job)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            System.out.println("Error in testJobTrigger: " + e.getMessage());
        }
    }

    private void updateSharesTrigger() {
        try {
            JobDetail job = newJob(updateSharesJob.class).withIdentity("updateSharesJob", "updateSharesJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("updateSharesTrigger", "updateSharesJob")
                    .withSchedule(cronSchedule("0 0/5 9-16 ? * MON-FRI"))
                    .forJob(job)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println("Error in updateSharesTrigger: " + e.getMessage());
        }
    }

    private void resetDailyprobeNumberTrigger() {
        try {
            JobDetail job = newJob(resetDailyProbeNumberJob.class).withIdentity("resetDailyProbeNumberJob", "resetDailyProbeNumberJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("resetDailyProbeNumberTrigger", "resetDailyProbeNumberJob")
                    .withSchedule(cronSchedule("50 59 8 ? * MON-FRI"))
                    .forJob(job)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println("Error in resetDailyProbeNumberTrigger: " + e.getMessage());
        }
    }
    
    private void timeToEarnTrigger() {
        try {
            JobDetail job = newJob(timeToEarnJob.class).withIdentity("timeToEarnJob", "timeToEarnJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("timeToEarnJob", "timeToEarnJob")
                    .withSchedule(cronSchedule("0 0/5 9-16 ? * MON-FRI"))
                    .forJob(job)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            System.out.println("Error in resetDailyProbeNumberTrigger: " + e.getMessage());
        }
    }
}
