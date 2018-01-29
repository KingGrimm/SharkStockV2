/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import controllers.CompanyController;
import controllers.CounterController;
import controllers.ShareController;
import controllers.StockController;
import data.downloadData;
import entitites.Company;
import entitites.Counter;
import entitites.Share;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import static org.quartz.CronScheduleBuilder.*;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;
import jobs.*;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 *
 * @author Kingu
 */
@WebListener
public class Overlord implements ServletContextListener {

    private static final CounterController counterController = new CounterController();
    private static final ShareController shareController = new ShareController();
    private static final CompanyController companyController = new CompanyController();
    private static final StockController stockController = new StockController();

    private static List<Company> avaibleCompanies = companyController.findCompanyEntities();

    private final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private Scheduler scheduler = null;

    public static CounterController getCounterController() {
        return counterController;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("Starting up!");
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            testJobTrigger();
            updateSharesTrigger();
            resetDailyprobeNumberTrigger();

            System.out.println("Starting suceeded.");
        } catch (Exception e) {
            System.out.println("Error in startup: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {

            scheduler.shutdown(true);
            System.out.println("Shutting down!");

        } catch (Exception e) {
            System.out.println("Error in shutdown: " + e.getMessage());
        }
    }

    private static Company searchForGivenCompany(Company searchedCompany) {
        for (Company company : avaibleCompanies) {
            if (company.getSymbol().equals(searchedCompany.getSymbol())
                    || company.getName().equals(searchedCompany.getName())) {
                return company;
            }
        }
        return null;
    }

    private static void sendSharesToDatabase(List<Share> shares) {
        try {
            Counter dailyProbeNumber = counterController.getCounter();

            for (Share share : shares) {
                share.setProbeNumber(dailyProbeNumber.getValue());
                share.setCompany(searchForGivenCompany(share.getCompany()));
                shareController.create(share);
            }

            Date date = new Date();
            System.out.println("SharkStock: Shares updated. Probe number: " + dailyProbeNumber.getValue() + ". Date: " + date);

            dailyProbeNumber.setValue(dailyProbeNumber.getValue() + 1);
            counterController.edit(dailyProbeNumber);

        } catch (Exception e) {
            System.out.println("Error in sendSharesToDatabase: " + e.getMessage());
        }

    }

    public static void sendSharesToDatabase() {
        downloadData data = new downloadData();
        data.analyze();
        sendSharesToDatabase(data.getDownloadedShares());
    }

    private void testJobTrigger() {
        try {
            JobDetail job = newJob(testJob.class).withIdentity("TestJob", "TestJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("TestJobTrigger", "TestJob")
                    .withSchedule(simpleSchedule()
                            .withIntervalInMinutes(15)
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
        } catch (Exception e) {
            System.out.println("Error in updateSharesTrigger: " + e.getMessage());
        }
    }
    
    private void resetDailyprobeNumberTrigger() {
        try {
            JobDetail job = newJob(resetDailyProbeNumberJob.class).withIdentity("resetDailyProbeNumberJob", "resetDailyProbeNumberJob").build();
            Trigger trigger = newTrigger()
                    .withIdentity("resetDailyProbeNumberTrigger", "resetDailyProbeNumberJob")
                    .withSchedule(cronSchedule("0 0 20 ? * MON-FRI"))
                    .forJob(job)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            System.out.println("Error in resetDailyProbeNumberTrigger: " + e.getMessage());
        }
    }
    
    

}
