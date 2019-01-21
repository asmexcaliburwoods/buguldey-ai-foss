package org.jcq2k.util.joe.jsync;

//-< Queue.java >----------------------------------------------------*--------*
// JSYNC                      Version 1.03       (c) 1998  GARRET    *     ?  *
// (Java synchronization classes)                                    *   /\|  *
//                                                                   *  /  \  *
//                          Created:     20-Jun-98    K.A. Knizhnik  * / [] \ *
//                          Last update: 10-Jul-98    K.A. Knizhnik  * GARRET *
//-------------------------------------------------------------------*--------*
// FIFO queue
//-------------------------------------------------------------------*--------*

/** First-In First-Out queue for synchronizing data exchange between several
 *  threads. For example, producer/consumer problem can be implemented by
 *  means of this queue.
 */
public class Queue {
	protected QueueItem first;
	protected QueueItem last;

	protected int nBlocked;
	protected static QueueItem freeItemChain;

	protected synchronized final static QueueItem createItem(Object obj) {
	QueueItem item;
	if (freeItemChain == null) {
	    item = new QueueItem();
	} else {
	    item = freeItemChain;
	    freeItemChain = freeItemChain.next;
	}
	item.obj = obj;
	item.next = null;
	return item;
	}
	protected synchronized final static void freeItem(QueueItem item) {
	item.next = freeItemChain;
	freeItemChain = item;
	}
	/** Wait until queue is not empty and take object from the queue.
	 *
	 * @return object which was inserted in the queue the longest time ago.
	 */
	public synchronized Object get() {
		if (first == null || nBlocked != 0) {
	    do {
	        try {
		    nBlocked += 1;
		    wait();
		    nBlocked -= 1;
		} catch(InterruptedException ex) {
		    // It is possible for a thread to be interrupted after
		    // being notified but before returning from the wait()
	            // call. To prevent lost of notification notify()
	            // is invoked.
		    notify();
		    nBlocked -= 1;
  		    throw new RuntimeException("thread is interrupted");
		}
	    } while (first == null);
	}
	QueueItem item = first;
	Object obj = item.obj;
	first = first.next;
	freeItem(item);
	if (nBlocked != 0 && first != null) {
	    // Notify "polite" threads which are blocked with non-empty queue
	    // because there were other threads waiting for producers.
	    notify();
	}
	return obj;
	}
	/** Wait at most <code>timeout</code> miliseconds until queue becomes
	 *  not empty and take object from the queue.
	 *
	 * @param timeout the maximum time to wait in milliseconds.
	 * @return object if queue is not empty, <code>null</code> otherwise
	 */
	public synchronized Object get(long timeout) {
		if (first == null || nBlocked != 0) {
	    if (timeout == 0) {
		return null;
	    }
	    long startTime = System.currentTimeMillis();
	    do {
	        long currentTime = System.currentTimeMillis();
		if (currentTime - startTime >= timeout) {
		    return null;
		}
	        try {
		    nBlocked += 1;
		    wait(timeout - currentTime + startTime);
		    nBlocked -= 1;
		} catch(InterruptedException ex) {
		    // It is possible for a thread to be interrupted after
		    // being notified but before returning from the wait()
	            // call. To prevent lost of notification notify()
	            // is invoked.
		    notify();
		    nBlocked -= 1;
  		    throw new RuntimeException("thread is interrupted");
		}
	    } while (first == null);
	}
	QueueItem item = first;
	Object obj = item.obj;
	first = first.next;
	freeItem(item);
	if (nBlocked != 0 && first != null) {
	    // Notify "polite" threads which are blocked with non-empty queue
	    // because there were other threads waiting for producers.
	    notify();
	}
	return obj;
	}
	/** Check if queue is empty.
	 */
	public boolean isEmpty() {
		return first == null;
	}
	/** Put object in queue. Notify data consumers.
	 */
	public synchronized void put(Object obj) {
		if (first == null) {
	    first = last = createItem(obj);
	} else {
	    last = last.next = createItem(obj);
	}
	if (nBlocked != 0) {
	    notify();
	}
	}
}