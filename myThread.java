package chatbox;

public class myThread extends Thread {
    private Runnable runnable;
    public myThread(Runnable runnable) {
        super(runnable);
        this.runnable=runnable;
    }
    
    public Runnable getRunnable() {
        return runnable;
    }
}
