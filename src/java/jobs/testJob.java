/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jobs;

import java.util.Date;
import main.Overlord;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Kingu
 */
public class testJob implements Job{

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        Date date=new Date();
        System.out.println("SharkStock: TestJob - "+date);      
    }
    
}
