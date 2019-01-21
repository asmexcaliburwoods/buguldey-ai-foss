

Abstract

The jcq2k allows to use the TCP-based ICQ2000b protocol.
Features implemented: logging in and out of the ICQ server,
sending/receiving of the text, contacts, and status messages,
and receiving of the url messages.



Purpose

The primary goal of the jcq2k project is to decipher the main features
of the icq2k protocol, and to provide an additional public source for
icq2k protocol information. The secondary goal is to create a reusable
instant messaging plugin library that allows the use of the ICQ2000
protocol.



Packaging and licensing

The jcq2k uses the LGPL license.  Full plugin sources are provided.
Test Java AWT GUI and console applications that use the plugin are
included with the plugin itself.  No reverse-engineering of the files
created by Mirabilis and/or AOL is performed to create the jcq2k.



Status

Working alpha-stage.  There are some critical places
in the icq2k protocol that are still not deciphered.



Contacts

jcq2k author: Filippov Joe
icq: 26500286
mailto:joe@idisys.iae.nsk.su?subject=jcq2k
Sailing smoothly A -> A -> A



Acknowledgements

The jcq2k project is based on the libfaim library <http://www.zigamorph.net/faim/>
written by:

N: Adam Fritzler
H: mid
E: mid@auk.cx
W: http://www.auk.cx/~mid,http://www.zigamorph.net/faim/
D: libfaim: wrote most of the [libfaim].

N: Josh Myer
E: josh@joshisanerd.com
D: libfaim: OFT/ODC (not quite finished yet..), random little things, Munger-At-Large, compile-time warnings.

N: Daniel Reed
H: n, linuxkitty
E: n@ml.org
W: http://users.n.ml.org/n/
D: libfaim: Fixed aim_snac.c

N: Eric Warmenhoven
E: warmenhoven@linux.com
D: libfaim: Some OFT info, author of the faim interface for gaim

N: Brock Wilcox
H: awwaiid
E: awwaiid@auk.cx
D: libfaim: Figured out original password roasting



References

Adam Fritzler et al.
Libfaim sources
http://www.zigamorph.net/faim/

Adam Fritzler et al.:
AIM OSCAR protocol specification
http://www.zigamorph.net/faim/protocol/
