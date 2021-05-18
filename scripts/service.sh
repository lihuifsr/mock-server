#!/bin/bash
if [ $# -lt 1 ];
then
  echo "USAGE: $0 classname opts"
  exit 1
fi

JAR_PATH=`ls *.jar`
if [[ ! "$?" == "0" || ! -e $JAR_PATH ]]; then
	echo "no or more than 1 jar"
	exit 2
fi

KEYWORD="$JAR_PATH"

PID_FILE="$JAR_PATH.pid"

function checkProcessIsRunning {
   ps -ef | grep java | grep "$KEYWORD" | grep -q --binary -F java
   if  [ $? -eq 0 ]; then
       return 0;
   fi
   return 1;
}

function startService {

    if checkProcessIsRunning -eq 0 ; then
        echo "start failed . project is already running";
        return 1;
    fi
    echo "Starting ..."
    rm -rf nohup.log
    nohup java -jar $KEYWORD >> nohup.log 2>&1 & echo $! > $PID_FILE

    for ((i=0; i<20; i++)); do
        if checkLog -eq 0; then
            break
        fi
    done

    return 0;
}


function checkLog {
    grep -q "MockServer started on port" nohup.log

    if [ $? -eq 0 ]; then
            echo "MockServer started ok!"
            return 0;
        else
            echo ".\c"
            sleep 1
            return 1
    fi
}

function stopService {
    pid="$(<$PID_FILE)"
    kill -9 $pid
    echo "MockServer stopped ok!"

#    if checkProcessIsRunning -ne 0; then
#        echo "no project is running"
#    fi
#    sf = ps -ef | grep java | grep "$KEYWORD" | grep --binary -F java
    return 0;
}

function main {
   RETVAL=0
   case "$1" in
      start)
         startService
         ;;
      stop)
         stopService
         ;;
      restart)
         stopService && startService
         ;;
      *)
         echo "Usage: $0 {start|stop|restart}"
         exit 1
         ;;
      esac
   exit $RETVAL
}

main "$1"