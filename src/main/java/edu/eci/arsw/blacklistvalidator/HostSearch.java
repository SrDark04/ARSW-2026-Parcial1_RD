package edu.eci.arsw.blacklistvalidator;

import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class HostSearch extends Thread{

    private int head;
    private int tail;
    private AtomicInteger checkedListCount;
    private HostBlacklistsDataSourceFacade hbldsf;
    private String ipAddress;
    private AtomicInteger chekedHostCounter;


    public HostSearch(int head, int tail, String ipAdress, HostBlacklistsDataSourceFacade hbldsf, AtomicInteger chekedHostCounter, AtomicInteger checkedListCount){
        this.head = head;
        this.tail = tail;
        this.hbldsf = hbldsf;
        this.checkedListCount = checkedListCount;
        this.ipAddress = ipAddress;
        this.chekedHostCounter = chekedHostCounter;
    }


    /*
    * Se realiza la implementacion de la sobreescritura del metodo run(), teniendo en cuenta solo recorrer 
    * un segmento de la lista que nos dan para este servidor.
    */
    @Override
    public void run(){
        for(int i = head; i < tail && chekedHostCounter.get()<5; i++){
            if (hbldsf.isInBlackListServer(i, ipAddress)){
                chekedHostCounter.getAndIncrement();
                checkedListCount.getAndIncrement();
            }
        }
    }          
}
