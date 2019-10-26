## 启动name-server
    ```shell
    docker run -d -p 9876:9876 --name rmqnamesrv rocketmqinc/rocketmq:4.4.0 bash mqnamesrv -n IP:9876
    ```
  
## 启动broker
    ```shell
    docker run -d --name rmqbroker --link rmqnamesrv:namesrv -e "NAMESRV_ADDR=namesrv:9876" -p 10909:10909 -p 10911:10911 rocketmqinc/rocketmq:4.4.0 sh mqbroker
    ```
    
## 启动console
    ```shell
    docker run  --link rmqnamesrv:namesrv -e "JAVA_OPTS=-Drocketmq.namesrv.addr=namesrv:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" -p 8080:8080 --name=rocketmq-console -d -t styletang/rocketmq-console-ng:latest
    ```