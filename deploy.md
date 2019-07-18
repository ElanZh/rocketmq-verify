# 部署操作

## 〇、部署之前
### 部署分为以下几个步骤：
* 去官网下载最新release二进制包```http://rocketmq.apache.org/community/```
* 解包 ```unzip rocketmq-all-4.5.1-bin-release.zip```
* 按照官网的quick start文档启动
    * 启动```nameserver``` 即 状态机，中间件会到状态机上注册自己，消费者和生产者也会到状态机寻找中间件信息，注册机
    * 启动```broker``` 即中间件，并指定要注册到哪台状态机

### 坑：
* java8即将收费，转用openjdk11，但是rocketmq的启动脚本有些参数与11并不兼容，需要改造，需要改造的脚本有三个：```bin/runserver.sh``` ```bin/runbroker.sh``` ```bin/tools.sh```
* 官方的测试例子跑不起来，是因为中间件默认不允许自动创建话题，需要在```conf/conf/broker.conf``` 中加入 ```autoCreateTopicEnable = true``` ，但是prod环境要关掉
### 附openjdk11环境下，三个能用的启动脚本：
```bin/runserver.sh```
```shell
    #!/bin/sh
    error_exit ()
    {
        echo "ERROR: $1 !!"
        exit 1
    }
    
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
    [ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"
    
    export JAVA_HOME
    export JAVA="$JAVA_HOME/bin/java"
    export BASE_DIR=$(dirname $0)/..
    # 之前的classpath要改掉
    export CLASSPATH=${BASE_DIR}/lib/rocketmq-namesrv-4.5.1.jar:${BASE_DIR}/lib/*:${BASE_DIR}/conf:${CLASSPATH}
    
    #===========================================================================================
    # JVM Configuration
    #===========================================================================================
    # 下边这一行改堆大小 原有的是 -Xms4g -Xmx4g -Xmn2g
    JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=320m"
    #JAVA_OPT="${JAVA_OPT} -XX:+UseConcMarkSweepGC -XX:+UseCMSCompactAtFullCollection -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8  -XX:-UseParNewGC"
    # 有些垃圾回收器在11版本已经移除，上边那句不行，用下边这句
    JAVA_OPT="${JAVA_OPT} -XX:CMSInitiatingOccupancyFraction=70 -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+CMSClassUnloadingEnabled -XX:SurvivorRatio=8"
    #JAVA_OPT="${JAVA_OPT} -verbose:gc -Xloggc:/dev/shm/rmq_srv_gc.log -XX:+PrintGCDetails"
    # Xloggc 要改成 Xlog:gc
    JAVA_OPT="${JAVA_OPT} -verbose:gc -Xlog:gc:/dev/shm/rmq_srv_gc.log -XX:+PrintGCDetails"
    JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
    JAVA_OPT="${JAVA_OPT}  -XX:-UseLargePages"
    # 下边这一行直接不要
    #JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${BASE_DIR}/lib"
    #JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
    JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
    JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"
    
    $JAVA ${JAVA_OPT} $@
```
```bin/runbroker.sh```
```shell
    #!/bin/sh
    error_exit ()
    {
        echo "ERROR: $1 !!"
        exit 1
    }
    
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
    [ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"
    
    export JAVA_HOME
    export JAVA="$JAVA_HOME/bin/java"
    export BASE_DIR=$(dirname $0)/..
    #export CLASSPATH=.:${BASE_DIR}/conf:${CLASSPATH}
    export CLASSPATH=${BASE_DIR}/lib/rocketmq-broker-4.5.1.jar:${BASE_DIR}/lib/*:${BASE_DIR}/conf:${CLASSPATH}
    
    #===========================================================================================
    # JVM Configuration
    #===========================================================================================
    JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn512m"
    JAVA_OPT="${JAVA_OPT} -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0"
    JAVA_OPT="${JAVA_OPT} -verbose:gc -Xlog:gc:/dev/shm/mq_gc_%p.log -XX:+PrintGCDetails"
    JAVA_OPT="${JAVA_OPT} -XX:-OmitStackTraceInFastThrow"
    JAVA_OPT="${JAVA_OPT} -XX:+AlwaysPreTouch"
    JAVA_OPT="${JAVA_OPT} -XX:MaxDirectMemorySize=15g"
    JAVA_OPT="${JAVA_OPT} -XX:-UseLargePages -XX:-UseBiasedLocking"
    #JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${JAVA_HOME}/jre/lib/ext:${BASE_DIR}/lib"
    #JAVA_OPT="${JAVA_OPT} -Xdebug -Xrunjdwp:transport=dt_socket,address=9555,server=y,suspend=n"
    JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"
    JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"
    
    numactl --interleave=all pwd > /dev/null 2>&1
    if [ $? -eq 0 ]
    then
        if [ -z "$RMQ_NUMA_NODE" ] ; then
            numactl --interleave=all $JAVA ${JAVA_OPT} $@
        else
            numactl --cpunodebind=$RMQ_NUMA_NODE --membind=$RMQ_NUMA_NODE $JAVA ${JAVA_OPT} $@
        fi
    else
        $JAVA ${JAVA_OPT} $@
    fi
```
```bin/tools.sh```
```shell
    #!/bin/sh
    error_exit ()
    {
        echo "ERROR: $1 !!"
        exit 1
    }
    
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
    [ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
    [ ! -e "$JAVA_HOME/bin/java" ] && error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)!"
    
    export JAVA_HOME
    export JAVA="$JAVA_HOME/bin/java"
    export BASE_DIR=$(dirname $0)/..
    #export CLASSPATH=.:${BASE_DIR}/conf:${CLASSPATH}
    export CLASSPATH=${BASE_DIR}/lib/*:${BASE_DIR}/conf:.:${CLASSPATH}
    
    #===========================================================================================
    # JVM Configuration
    #===========================================================================================
    JAVA_OPT="${JAVA_OPT} -server -Xms1g -Xmx1g -Xmn256m -XX:PermSize=128m -XX:MaxPermSize=128m"
    #JAVA_OPT="${JAVA_OPT} -Djava.ext.dirs=${BASE_DIR}/lib:${JAVA_HOME}/jre/lib/ext"
    JAVA_OPT="${JAVA_OPT} -cp ${CLASSPATH}"
    
    $JAVA ${JAVA_OPT} $@
```

## 一、单节点console
* console只是一个 管理页面，开源工程地址：https://github.com/apache/rocketmq-externals/tree/master/rocketmq-console ，可以自己down下来编译运行，
或者是使用docker启动控制台：
```shell
sudo docker pull styletang/rocketmq-console-ng
sudo docker run  -e "JAVA_OPTS=-Drocketmq.namesrv.addr=172.17.0.1:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" -p 8080:8080 --name=rocketmq-console -d -t styletang/rocketmq-console-ng
```
**注意！其中的```172.17.0.1``` 需要用docker角度的宿主机ip，使用 ```ip addr show``` 找到```docker0```的设备，就是宿主机ip**