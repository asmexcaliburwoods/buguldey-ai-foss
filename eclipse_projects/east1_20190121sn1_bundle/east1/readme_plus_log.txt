this old east1 system uses opencyc 0.7.0b which is win32-only and not supported anymore 
by its vendor (Cycorp). i need to port east1 to cross-platform ontology semantics/logics inference engine, maybe 
allowing multiple engines, creating a multiontology inference adapter. rdf/opencog atomese/etc.

Defining basic concepts
Initialising natural language parser
Parsing natural language definition
java.lang.ExceptionInInitializerError
	at org.east.e1.RuleLabelReferenceOrWordClass.resolveRuleLabelReferenceOrWordClass(RuleLabelReferenceOrWordClass.java:62)
	at org.east.e1.LHSSeq.resolveRuleLabelReferenceOrWordClass(LHSSeq.java:24)
	at org.east.e1.E1Parser.resolveRuleLabelReferenceOrWordClass(E1Parser.java:82)
	at org.east.e1.E1Parser.compile(E1Parser.java:57)
	at org.east.e1.E1Parser.update(E1Parser.java:43)
	at org.east.East.main(East.java:45)
Caused by: java.lang.RuntimeException: java.net.ConnectException: Connection refused (Connection refused)
	at org.east.cyc.CycLink.<clinit>(CycLink.java:53)
	... 6 more
Caused by: java.net.ConnectException: Connection refused (Connection refused)
	at java.net.PlainSocketImpl.socketConnect(Native Method)
	at java.net.AbstractPlainSocketImpl.doConnect(AbstractPlainSocketImpl.java:350)
	at java.net.AbstractPlainSocketImpl.connectToAddress(AbstractPlainSocketImpl.java:206)
	at java.net.AbstractPlainSocketImpl.connect(AbstractPlainSocketImpl.java:188)
	at java.net.SocksSocketImpl.connect(SocksSocketImpl.java:392)
	at java.net.Socket.connect(Socket.java:589)
	at java.net.Socket.connect(Socket.java:538)
	at java.net.Socket.<init>(Socket.java:434)
	at java.net.Socket.<init>(Socket.java:211)
	at org.opencyc.api.CycConnection.initializeApiConnections(CycConnection.java:373)
	at org.opencyc.api.CycConnection.<init>(CycConnection.java:349)
	at org.opencyc.api.CycConnection.<init>(CycConnection.java:308)
	at org.opencyc.api.CycAccess.<init>(CycAccess.java:297)
	at org.east.cyc.CycLink$CycAccess2.<init>(CycLink.java:30)
	at org.east.cyc.CycLink$CycAccess2.<init>(CycLink.java:25)
	at org.east.cyc.CycLink.<clinit>(CycLink.java:50)
	... 6 more
