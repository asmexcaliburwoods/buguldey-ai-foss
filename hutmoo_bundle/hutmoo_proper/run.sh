cd tomcat/bin
./shutdown.sh
export L=../../lib
export LMOO_ENDORSE=$L
export LOGICMOOCLASSPATH=$L/../classes:$L/bsf.jar:$L/bsfengines.jar:$L/bsh-1.2b6.jar:$L/jakarta-oro-2.0.6.jar:$L/jcq2k-classes.zip:$L/jsync.zip:$L/log4j.jar:$L/OpenCyc.jar:$L/pop3.jar:$L/soap.jar:$L/ViolinStrings.jar:$L/xmisoap.jar:$L/xerces.jar
./startup.sh
cd $L

../kill.sh hutmoo.main
java -Dprocess.id=hutmoo.main -Dmoo.ircbot.nick=hobot -Dcyc.httpd.htdocs=../../opencyc-0.6.0/run/httpd/htdocs/ -Dmoo.classes=../classes -Dmoo.ircbot.channel=#thehut -Dmoo.ircbot.nickserv_password=ho_bu_bu_la_nd -classpath ../classes:bsf.jar:bsfengines.jar:bsh-1.2b6.jar:jakarta-oro-2.0.6.jar:jcq2k-classes.zip:jsync.zip:log4j.jar:OpenCyc.jar:pop3.jar:soap.jar:ViolinStrings.jar:xmisoap.jar:xerces.jar logicmoo.LogicMoo
#:bootstrap.jar:../tomcat/server/lib/catalina.jar:../tomcat/server/lib/jakarta-regexp-1.2.jar:../tomcat/server/lib/servlets-common.jar:../tomcat/server/lib/servlets-default.jar:../tomcat/server/lib/servlets-invoker.jar:../tomcat/server/lib/servlets-manager.jar:../tomcat/server/lib/servlets-snoop.jar:../tomcat/server/lib/servlets-webdav.jar:../tomcat/server/lib/tomcat-ajp.jar:../tomcat/server/lib/tomcat-util.jar:../tomcat/server/lib/warp.jar:jasper-compiler.jar:jasper-runtime.jar:naming-factory.jar:activation.jar:jdbc2_0-stdext.jar:jndi.jar:jta-spec1_0_1.jar:mail.jar:naming-common.jar:naming-resources.jar:servlet.jar:tyrex-0.9.7.0.jar 
cd ..