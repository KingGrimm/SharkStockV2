/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllersBasic.CounterJpaController;
import entitites.Counter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kingu
 */
public class CounterController extends CounterJpaController {

    public CounterController(EntityManagerFactory emf) {
        super(emf);
    }

    public CounterController() {
        super(Persistence.createEntityManagerFactory("SharkStockV6PU"));
    }

    public Counter getCounter() {
        return this.findCounter(constant.Constants.COUNTER_ID);
    }

    public Counter increaseCounter() {
        Counter counter = this.findCounter(constant.Constants.COUNTER_ID);
        try {
            counter.setValue(counter.getValue() + 1);
            this.edit(counter);
        } catch (Exception e) {
            System.err.println("Error in CounterController.increaseCounter(): " + e.getMessage());
        } finally {
            return counter;
        }
    }

    public void reset() {
        Counter counter = this.findCounter(constant.Constants.COUNTER_ID);
        try {
            counter.setValue(0);
            this.edit(counter);
        } catch (Exception e) {
            System.err.println("Error in CounterController.reset(): " + e.getMessage());
        }
    }
}
