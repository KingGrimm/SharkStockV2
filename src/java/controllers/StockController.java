/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import controllersBasic.StockJpaController;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Kingu
 */
public class StockController extends StockJpaController{
       public StockController(EntityManagerFactory emf) {
        super(emf);
    }

    public StockController() {
        super(Persistence.createEntityManagerFactory("SharkStockV6PU"));
    }
}
