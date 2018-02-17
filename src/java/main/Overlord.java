/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import analyzeTools.TimeToEarn;
import controllers.CompanyController;
import controllers.CounterController;
import controllers.ShareController;
import controllers.StockController;
import data.downloadData;
import entitites.Company;
import entitites.Counter;
import entitites.Share;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.SchedulerException;

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

    //TODO co w przypadku jesli pobrana lista jest inna? W sumie to chyba nic,
    //bo spolki odnajdywane i tak są po nazwie, wiec wszystko powinno dzialac
    private static List<Company> avaibleCompanies = companyController.findCompanyEntities();

    private static HashMap<Company, TimeToEarn> oracleTool = new HashMap<>();

    private final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    private Scheduler scheduler = null;

    public static CounterController getCounterController() {
        return counterController;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            System.out.println("Starting up!");

            for (Company company : avaibleCompanies) {

                oracleTool.put(company, new TimeToEarn(companyController.getLastShare(company), constant.Constants.PROFIT_FOR_ORACLE));
            }

            operateOracle();

            scheduler = schedulerFactory.getScheduler();
            scheduler.start();

            JobHolder jobHolder = new JobHolder(scheduler);

            //  companyController.getTwoToCompare(avaibleCompanies.get(4), 128);
            //   System.out.println("test: " + companyController.getLastShare(avaibleCompanies.get(4)).getProbeDate());
            checkForGivenChangeinStock(96, 1.0);

            //       updateSharesTrigger();
            //        resetDailyprobeNumberTrigger();
            System.out.println("Starting suceeded.");
        } catch (SchedulerException e) {
            System.out.println("Error in startup: " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {

            scheduler.shutdown(true);
            System.out.println("Shutting down!");

        } catch (SchedulerException e) {
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
        data.saveToCorrespondingLists();
        sendSharesToDatabase(data.getDownloadedShares());
    }

    private List<Company> checkForGivenChangeinStock(Integer probePeriod, Double percentChange) {
        List<Company> intestingCompanies = new ArrayList<>();
        List<Share> toCompare;

        for (Company company : avaibleCompanies) {
            toCompare = companyController.getTwoToCompare(company, probePeriod);
            if (ifShareVealueChangedBy(toCompare.get(0), toCompare.get(1), percentChange)) {
                intestingCompanies.add(company);
                System.out.println(company);
            }
        }

        return intestingCompanies;

    }

    private boolean ifShareVealueChangedBy(Share shareBefore, Share shareAfter, Double percentChange) {
        //System.out.println("Cena 1: "+shareBefore.getValue()+" Cena 2: "+shareAfter.getValue());
        Double toPercent = ((shareAfter.getValue() - shareBefore.getValue()) / shareBefore.getValue()) * 100;
        if (Math.abs(toPercent) >= percentChange) {
            System.out.println("Wyliczona zmiana: " + toPercent);
            return true;
        }
        return false;
    }

    public static void operateOracle() {

        System.out.println("Warosc: " + avaibleCompanies.size());

        for (Company company : avaibleCompanies) {
            Share share = companyController.getLastShare(company);
            oracleTool.get(company).checkShare(share);
            if (oracleTool.get(company).isComplete()) {
                oracleTool.get(company).getResult();
                oracleTool.put(company, new TimeToEarn(companyController.getLastShare(company), constant.Constants.PROFIT_FOR_ORACLE));
            }
        }
    }

}
