/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    
    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int threads){
        
        LinkedList<Integer> blackListOcurrences=new LinkedList<>();
        
        AtomicInteger ocurrencesCount = new AtomicInteger(0);
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        AtomicInteger checkedListsCount = new AtomicInteger(0);

        ArrayList<HostSearch> hostSearchThreads = separateList(threads, skds, ipaddress, ocurrencesCount, checkedListsCount);

        startHilos(hostSearchThreads);
        /* 
        for (int i=0;i<skds.getRegisteredServersCount() && ocurrencesCount.get()<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount.getAndIncrement();
            
            if (skds.isInBlackListServer(i, ipaddress)){
                
                blackListOcurrences.add(i);
                
                ocurrencesCount.getAndIncrement();
            }
        }
        */
        if(ocurrencesCount.get()>=BLACK_LIST_ALARM_COUNT){
            if (ocurrencesCount.get()>=BLACK_LIST_ALARM_COUNT){
                skds.reportAsNotTrustworthy(ipaddress);
            }
            else{
                skds.reportAsTrustworthy(ipaddress);
            }                
        }
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    /*
    * Separa la lista de host en la cantidad de hilos pertinente, teniendo en cuenta si es impar o no
    */
    private static ArrayList<HostSearch> separateList(int threads, HostBlacklistsDataSourceFacade skds, String ipAddress, AtomicInteger cheked, AtomicInteger checkedListCount){

        int range = skds.getRegisteredServersCount() / threads;

        ArrayList<HostSearch> searchedThreads = new ArrayList<>();
    
        for(int i = 0; i < threads; i++){
            int head = range * i;
            int tail = range * (i + 1);

            if(i + 1 == threads){
                tail = skds.getRegisteredServersCount() + 1; 
            }

            HostSearch h = new HostSearch(head, tail, ipAddress, skds, cheked, checkedListCount);
            searchedThreads.add(h);
        }
        return searchedThreads;
    }


    /*
    * Inicializa todos los hilos
    */
    private static void startHilos(ArrayList<HostSearch> threads){
        for(HostSearch h : threads){
            h.start();
        }
    }


    private  void detenerHilos(ArrayList<HostSearch> threads){
        for(HostSearch h : threads){
            h.stop();
        }
    }

    private static boolean threadsLives(ArrayList<HostSearch> threads){
        for(HostSearch h : threads){
            if(h.isAlive()) return false;
        }
        return true;
    }
}
