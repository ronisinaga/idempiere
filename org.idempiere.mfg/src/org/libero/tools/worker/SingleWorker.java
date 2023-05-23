// 
// Decompiled by Procyon v0.5.36
// 

package org.libero.tools.worker;

public abstract class SingleWorker extends MultiWorker
{
	public SingleWorker() {
		
		super();
	}
	
	public void start() {

		workerThread = new WorkerThread() {

			public Object doWork() {

				return doIt();
			};	
		};
		workerThread.start();
	}

	protected abstract Object doIt();
}
