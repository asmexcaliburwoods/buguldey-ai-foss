package org.jcq2k.util.joe.jsync;

import org.jcq2k.util.joe.jsync.*;

//
public class Wheel extends Queue
{
	protected int size = 0;
/**
 * Wheel constructor comment.
 */
public Wheel() {
	super();
}
/** Wait until queue is not empty and take object from the queue.
 *
 * @return object which was inserted in the queue the longest time ago.
 */
public synchronized Object get()
{
	Object o = super.get();
	--size;
	return o;
}
/** Wait at most <code>timeout</code> miliseconds until queue becomes
 *  not empty and take object from the queue.
 *
 * @param timeout the maximum time to wait in milliseconds.
 * @return object if queue is not empty, <code>null</code> otherwise
 */
public synchronized Object get(long timeout)
{
	Object o = super.get(timeout);
	--size;
	return o;
}
/** Immediately returns the oldest object or null, if the queue is empty.
 *
 * @return object which was inserted in the queue the longest time ago, or null, if the queue is empty.
 */
public synchronized Object peek()
{
	return (first == null ? null : first.obj);
}
/** Put object in queue. Notify data consumers.
 */
public synchronized void put(Object obj)
{
	super.put(obj);
	++size;
}
/** Returns the number of objects in the queue.
 * @return the number of objects in the queue
 */
public synchronized int size()
{
	return size;
}
}