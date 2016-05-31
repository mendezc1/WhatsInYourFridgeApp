package cs496.whatsinyourfridge;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class BufferThread extends Thread {
    private FileManager fileManager;
    private Server serverSender;
    private StatusListener listener;

    public BufferThread(StatusListener listener, FileManager fileManager, Server serverSender) {
        this.listener = listener;
        this.fileManager = fileManager;
        this.serverSender = serverSender;
    }

    public interface StatusListener {
        void report(String msg);
    }

    private void sendStatusMessage(String msg) {
        listener.report(msg);
    }

    public void run() {
        while (!isInterrupted()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                break;
            }
            if (serverSender.isNetworkOn()) {
                List<Recipe> awaiting = null;
                try {
                    awaiting = fileManager.loadUnsent();
                } catch (FileNotFoundException | JSONException e) {
                    sendStatusMessage(e.getMessage());
                }
                for (Recipe entry : awaiting) {
                    boolean success = false;
                    try {
                        long id = serverSender.send(entry);
                        success = id > 0;
                    } catch (InterruptedException ie) {
                        break;
                    } catch (Exception ex) {
                        sendStatusMessage(ex.getMessage());
                    }
                    if (success)
                        fileManager.markSent(entry);
                }
            }
        }
        cleanup();
    }


    public void write(Recipe entry) {
        try {
            fileManager.write(entry);
        } catch (IOException | JSONException e) {
            sendStatusMessage("Error: " + e);
        }
    }

    public void cleanup() {
    }


}
