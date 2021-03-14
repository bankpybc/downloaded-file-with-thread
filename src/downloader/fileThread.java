/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package downloader;

import downloader.gui.DownloaderGUI;
import downloader.task.DownloaderThread;
import downloader.task.DownloaderThread.guiFunction;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author user
 */
public class fileThread {
    private static guiFunction guiFunction ;
    private static String url;
    private static int sizeEachThread;
    private int fileSize;

    public fileThread(guiFunction guiFunction,String url) {
        this.url = url;
        this.guiFunction = guiFunction;
    }
    public void running() {
        try{
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
            connection.connect();
            
            String fileName = new java.io.File(url).getName();
            fileSize = connection.getContentLength();
            
             this.guiFunction.fileReport(fileName,fileSize);
            sizeEachThread = fileSize/ 10;
             ExecutorService threadPool = Executors.newFixedThreadPool(10);
             int startThread = 0;
             int endThread = sizeEachThread;
            for (int i = 1; i <= 10; i++) {
                if (i < 10) {
                    threadPool.submit(new DownloaderThread(i,guiFunction, url,startThread, endThread, sizeEachThread));
                    
                } else {
                    threadPool.submit(new DownloaderThread(i,guiFunction , url, startThread, fileSize, sizeEachThread));
                    
                }
                startThread = endThread + 1;
                endThread += sizeEachThread;
                
            }
            threadPool.shutdown();
         }
        
        catch(Exception e){
            this.guiFunction.downloadFailed(e.getMessage());
        } 
   
        
    }
}
