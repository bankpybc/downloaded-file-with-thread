
package downloader.task;

import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloaderThread extends Thread{
    private String url;
    public guiFunction guiFunction;
    private int threadNumber = 0;
    private int startThread;
    private int endThread;
    private int sizeThread;
    public DownloaderThread(int i,guiFunction guiFunction,String url,int startThread,int endThread,int sizeThread){
        threadNumber = i;
        this.guiFunction = guiFunction;
        this.url = url;
        this.startThread = startThread;
        this.endThread = endThread;
        this.sizeThread = sizeThread;
    }
 
    public void run(){
        try{
            this.guiFunction.threadReport(threadNumber);
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setRequestProperty("Range", "Bytes=" + startThread + "-" + endThread);
            connection.connect();
            String fileName = new java.io.File(url).getName();
   
           InputStream input = connection.getInputStream();
           byte buffer[] = new byte[sizeThread];
           RandomAccessFile outputFile = new RandomAccessFile(fileName,"rw"); // read and writing
           outputFile.seek(startThread);
          
          
           int length = input.read(buffer,0,buffer.length);
           int total = length;
           while(total < sizeThread){
               length = input.read(buffer, total, (buffer.length - total));
               if(length != -1){ // found file size
                   total += length;
             this.guiFunction.downloadProgress("Thread ["+threadNumber+"] Downloaded... "+total/1000+" Kb" );
               }

           }
           outputFile.write(buffer, 0, total);
           input.close();
           outputFile.close();
           this.guiFunction.downloadComplete(threadNumber);
        }
        catch(Exception e){
            this.guiFunction.downloadFailed(e.getMessage());
        } 
        
    }
    public interface guiFunction{
        void downloadStart(int threadNumber);
        
        void downloadProgress(String descirption);
        
        void downloadComplete(int threadNumber);

        void downloadFailed(String errorMsg);

       
        void threadReport(int threadNumber);
        
        void fileReport(String fileName,int fileSize);
    }
}
