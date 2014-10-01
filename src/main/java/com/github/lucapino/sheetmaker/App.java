/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.lucapino.sheetmaker;

import com.github.lucapino.sheetmaker.collectors.DataRetriever;
import java.util.ServiceLoader;

/**
 *
 * @author Luca Tagliani
 */
public class App {
    public static void main(String[] args) {
        ServiceLoader<DataRetriever> dataRetrievers = ServiceLoader.load(DataRetriever.class);
        for (DataRetriever dataRetriever : dataRetrievers) {
            System.out.println(dataRetriever.getClass().getName());
        }
        
    }
}
