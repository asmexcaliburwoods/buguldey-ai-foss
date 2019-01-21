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

public class QueueItem {
	QueueItem next;
	public Object    obj;
}