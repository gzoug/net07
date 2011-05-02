#!/bin/sh

#export LD_LIBRARY_PATH=/usr/local/diablo-jdk1.5.0/jre/lib/i386:/usr/local/diablo-jdk1.5.0/jre/lib/i386/client
#export JAVA_HOME=/usr/local/diablo-jdk1.5.0
#set

export NET07_ENCODING=8859_7 #UTF8
#export NET07_ENCODING=UTF8

UserID=$USER 
homefolder=/usr/dmst/networks
active=1

if [ "$UserID" = gzoug ]; then
 active=1
fi

echo
echo "Online Exercise System Net07"
echo "Welcome $UserID !"

if [ $active -eq 0 ]
then
 echo "--------------------------------------------"
 echo "Η εφαρμογή είναι προσωρινά εκτός λειτουργίας"
 echo "H Efarmogh einai proswrina ektos leitourgias"
 echo "--------------------------------------------"
 exit
fi

#/usr/local/diablo-jdk1.5.0/jre/bin/java  -jre-restrict-search -jar $homefolder/exercises.jar $1 $2 $3 $4 $5 $6 $7 $8 $9
java -jar $homefolder/exercises.jar $1 $2 $3 $4 $5 $6 $7 $8 $9

