// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tools.worker;

public abstract class MultiWorker
{
    protected boolean isWorking;
    protected WorkerThread workerThread;
    protected int timeout;
    protected Object value;
    
    public MultiWorker() {
        this.setTimeout(-1);
    }
    
    public abstract void start();
    
    public int getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final int timeout) {
        this.timeout = timeout;
    }
    
    public boolean isWorking() {
        return this.isWorking;
    }
    
    public void waitForComplete(final int timeout) {
        this.setTimeout(timeout);
        this.waitForComplete();
    }
    
    public void stop() {
        this.workerThread.interrupt();
    }
    
    public void waitForComplete() {
        final boolean to = this.getTimeout() > -1;
        int c = 0;
        final int i = 1000;
        while (this.isWorking()) {
            try {
                Thread.sleep(i);
                c += (to ? (c += i) : -1);
            }
            catch (Exception ex) {}
            if (to && c >= this.getTimeout()) {
                this.workerThread.interrupt();
                this.workerThread = null;
                break;
            }
        }
    }
    
    protected abstract class WorkerThread extends Thread
    {
        public abstract Object doWork();
        
        @Override
        public void run() {
            MultiWorker.this.isWorking = true;
            MultiWorker.this.value = this.doWork();
            MultiWorker.this.isWorking = false;
        }
        
        @Override
        public void interrupt() {
            super.interrupt();
            MultiWorker.this.isWorking = false;
        }
    }
}
