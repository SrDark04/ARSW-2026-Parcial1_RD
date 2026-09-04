package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class HostSearch extends Thread{

    private int head;
    private int tail;
    private int hostSearched;
    private HostBlacklistsDataSourceFacade hbldsf;
    private String ipAddress;


    public HostSearch(int head, int tail, String ipAdress, HostBlacklistsDataSourceFacade hbldsf){
        this.head = head;
        this.tail = tail;
        this.hbldsf = hbldsf;
        this.hostSearched = 0;
        this.ipAddress = ipAddress;
    }

    @Override
    public void run(){
        for(int i = head; i < tail; i++){
            if (hbldsf.isInBlackListServer(i, ipAddress)){
                
                hostSearched++;
            }
        }
    }          
}
