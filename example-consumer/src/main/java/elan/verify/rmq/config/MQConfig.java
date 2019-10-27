package elan.verify.rmq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张一然
 * @date 2019/10/27 下午3:05
 * @Package elan.verify.rmq.config
 * @Description
 */
@Configuration
@ConfigurationProperties(prefix = "rocketmq.consumer")
public class MQConfig {
}
