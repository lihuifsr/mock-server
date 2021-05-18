#!/bin/sh
set -e

#发布包的路径
dist_pkg_path=$1
if [ -z $dist_pkg_path ]; then
  echo '请输入发布包的路径'
  exit 1
fi
if [ ! -f $dist_pkg_path ]; then
  echo '您输入的发布包路径不存在'
  exit 1
fi

#目标机器ip地址
target_server_ip=$2
if [ -z $target_server_ip ]; then
  echo '请输入目标机器ip'
  exit 1
fi

#目标机器用户
target_server_user=$3
if [ -z $target_server_user ]; then
  echo '请输入目标机器用户名'
  exit 1
fi

#环境信息
env_name=$4
if [ -z $env_name ]; then
  echo '请输入环境信息[test,uat]'
  exit 1
fi


#定义一个目标机器上的临时目录，用来暂存发布包
remote_tmp=$5
if [ -z $remote_tmp ]; then
  remote_tmp=tmp_dist_$web_pkg
fi


scp $dist_pkg_path $target_server_user@$target_server_ip:$remote_tmp/

ssh $target_server_user@$target_server_ip "ps -ef | grep mock-server | grep java | grep -v grep | awk  '{print \$2}' | xargs  kill -9"

ssh -tt $target_server_user@$target_server_ip<<EOF

sleep 1
cd $remote_tmp
echo "Starting ..."
rm -rf nohup.log
nohup java -jar mock-server-execjar.jar >> nohup.log 2>&1 &
exit
EOF


##删除之前的临时目录
#ssh $target_server_user@$target_server_ip rm -rf $remote_tmp
#
##重新创建临时目录
#ssh $target_server_user@$target_server_ip mkdir -p $remote_tmp
#
##将应用程序文件复制到目标机器临时目录
#scp $dist_pkg_path $target_server_user@$target_server_ip:$remote_tmp/
#
#ssh $target_server_user@$target_server_ip