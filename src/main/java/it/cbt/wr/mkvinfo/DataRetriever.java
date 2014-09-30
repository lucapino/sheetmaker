/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cbt.wr.mkvinfo;

import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

/**
 *
 * @author Luca Tagliani
 */
public class DataRetriever {
    public static void main(String[] args) {
        DataRetriever retriever = new DataRetriever();
        retriever.retrieve(args[0]);
    }

    private void retrieve(String arg) {
        // use themoviedb api to get info about movie
        
    }
}
