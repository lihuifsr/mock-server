#!/bin/bash

PROG_NAME=$0
ACTION=$1
APP_START_TIMEOUT=90   # 等待应用启动的时间

# 应用名
APP_NAME=$2
if [ -z $APP_NAME ]; then
  echo '请输入应用名称'
  exit 1
fi

# 环境名称
ENV_NAME=$3
if [ -z $ENV_NAME ]; then
   echo '请输入环境名称[dev,test,prod]'
  exit 1
fi

# 应用端口
APP_PORT=$4
if [ -z $APP_PORT ]; then
  echo '请输入应用端口'
  exit 1
fi

# 应用path
CONTEXT_PATH=$5
if [ -z $CONTEXT_PATH ]; then
  echo '请输入应用上下文路径'
  exit 1
fi

HEALTH_CHECK_URL=http://127.0.0.1:${APP_PORT}/$CONTEXT_PATH/he  # 应用健康检查URL
APP_HOME=/home/apps/ # 从package.tgz中解压出来的包放到这个目录下
JAR_NAME=${APP_HOME}/${APP_NAME}/build/libs/${APP_NAME}.jar # jar包的名字
JAVA_OUT=${APP_HOME}/${APP_NAME}/logs/start.log  #应用的启动日志

# 创建出相关目录
mkdir -p ${APP_HOME}
mkdir -p ${APP_HOME}/${APP_NAME}/logs
usage() {
    echo "Usage: $PROG_NAME {start|stop|restart}"
    exit 2
}

health_check() {
    exptime=0
    echo "checking ${HEALTH_CHECK_URL}"
    while true
        do
            status_code=`/usr/bin/curl -L -o /dev/null --connect-timeout 5 -s -w %{http_code}  ${HEALTH_CHECK_URL}`
            if [ "$?" != "0" ]; then
               echo -n -e "\rapplication not started"
            else
                echo "code is $status_code"
                if [ "$status_code" == "200" ];then
                    break
                fi
            fi
            sleep 1
            ((exptime++))

            echo -e "\rWait app to pass health check: $exptime..."

            if [ $exptime -gt ${APP_START_TIMEOUT} ]; then
                echo 'app start failed'
               exit 1
            fi
        done
    echo "check ${HEALTH_CHECK_URL} success"
}
start_application() {

    echo "preparing data dirs..."
    mkdir -p /data/app/dump
    mkdir -p /data/app/logs/${APP_NAME}/${ENV_NAME}
    mkdir -p /data/tmp/
    echo "starting jar path -> ${JAR_NAME}"
    echo "starting java process"

    if [ ${ENV_NAME} == "prod" ];then
        echo "starting prod env"
        nohup java -Xms1024m -Xmx2048m -XX:MaxPermSize=256m -XX:MetaspaceSize=2048m -XX:-OmitStackTraceInFastThrow -Djava.net.preferIPv4Stack=true -XX:+TieredCompilation -Djava.awt.headless=true -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/app/dump -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Dsun.misc.URLClassPath.disableJarChecking=true -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -Xloggc:/data/app/logs/${APP_NAME}/${ENV_NAME}/${APP_NAME}-gc.log -Dlogging.file=/data/app/logs/${APP_NAME}/${ENV_NAME}/${APP_NAME}.log -Djava.io.tmpdir=/data/tmp/ -Dspring.config.location=/home/apps/rocket/deploy/configure/application-prod.properties -jar ${JAR_NAME} > ${JAVA_OUT} 2>&1 &
    else
        echo "starting ${ENV_NAME} env"
        nohup java -Xms512m -Xmx1024m -XX:MaxPermSize=256m -XX:MetaspaceSize=1024m -XX:-OmitStackTraceInFastThrow -Djava.net.preferIPv4Stack=true -XX:+TieredCompilation -Djava.awt.headless=true -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/data/app/dump -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintHeapAtGC -Dsun.misc.URLClassPath.disableJarChecking=true -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Djava.security.egd=file:/dev/./urandom -Xloggc:/data/app/logs/${APP_NAME}/${ENV_NAME}/${APP_NAME}-gc.log -Dlogging.file=/data/app/logs/${APP_NAME}/${ENV_NAME}/${APP_NAME}.log -Djava.io.tmpdir=/data/tmp/ -jar ${JAR_NAME} /data/mock-server-config/ --spring.profiles.active=${ENV_NAME} > ${JAVA_OUT} 2>&1 &
    fi

    echo "started java process"
}

stop_application() {
   checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`

   if [[ ! $checkjavapid ]];then
      echo -e "\rno java process"
      return
   fi

   echo "stop java process"
   times=60
   for e in $(seq 60)
   do
        sleep 1
        COSTTIME=$(($times - $e ))
        checkjavapid=`ps -ef | grep java | grep ${APP_NAME} | grep -v grep |grep -v 'deploy.sh'| awk '{print$2}'`
        if [[ $checkjavapid ]];then
            kill -9 $checkjavapid
            echo -e  "\r        -- stopping java lasts `expr $COSTTIME` seconds."
        else
            echo -e "\rjava process has exited"
            break;
        fi
   done
   echo ""
}
start() {
    start_application
    health_check
}
stop() {
    stop_application
}
case "$ACTION" in
    start)
        start
    ;;
    stop)
        stop
    ;;
    restart)
        stop
        start
    ;;
    *)
        usage
    ;;
esac
