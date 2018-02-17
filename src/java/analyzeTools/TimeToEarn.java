/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzeTools;

import entitites.Share;
import java.time.Duration;

/**
 *
 * @author Kingu
 */
public class TimeToEarn {

    private Double desiredChange;
    private Share nominalShare = null;
    private Share cheaperShare = null;
    private Share expensiveShare = null;
    private String result = null;

    private TimeToEarn() {

    }

    
   /**
    * 
    * @param nominalShare strting share
    * @param desiredChange desired gain (%)
    */ 
  public TimeToEarn(Share nominalShare, Double desiredChange) {
        this.desiredChange = desiredChange;
        this.nominalShare = nominalShare;
    }

    public void checkShare(Share share) {
        System.out.println("Comparing "+share.getCompany().getName());
        if (cheaperShare == null) {
            System.out.println("Old Share: "+share.getValue()+" <? "+ nominalShare.getValue());
            if (share.getValue() <= nominalShare.getValue() * (1 - (desiredChange / 200))) {
                cheaperShare = share;
            }
        } else if (expensiveShare == null) {
            System.out.println("Old Share: "+share.getValue()+" >? "+ nominalShare.getValue());
            if (share.getValue() >= nominalShare.getValue() * (1 + (desiredChange / 200))) {
                expensiveShare = share;
                countTimeBetweenShares();
            }
        }
    }

    public void countTimeBetweenShares() {

        Duration duration = Duration.between(cheaperShare.getProbeDate().toInstant(), expensiveShare.getProbeDate().toInstant());
        result = duration.toString();

    }

    public String getResult() {
        if (result == null) {
            return "Brak satysfakcjonujacego wyniku od " + nominalShare.getProbeDate() + " dla spolki " + nominalShare.getCompany().getName();
        } else {
            System.out.println("TIMETOEARN:" +result + " dla spolki " + nominalShare.getCompany().getName());
            return result + " dla spolki " + nominalShare.getCompany().getName();
        }
    }
    
    public boolean isComplete(){
        return result!=null;
    }
    
    
}
