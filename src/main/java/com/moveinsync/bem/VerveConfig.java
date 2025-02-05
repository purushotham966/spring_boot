package com.moveinsync.bem;

import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import io.micrometer.common.util.StringUtils;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class VerveConfig {


    private final Environment env;

    public VerveConfig(Environment env) {
        this.env = env;
    }


    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress(env.getProperty("redis.endpoint"));
        String password = env.getProperty("redis.password");
        if (StringUtils.isNotBlank(password)) {
            config.useSingleServer().setPassword(password);
        }

        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8080") // Set a default base URL (optional)
                .build();
    }


    @Value(value = "${kafka.servers}")
    private String kafkaServerEndPoint;

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServerEndPoint);
        props.put(ProducerConfig.RETRIES_CONFIG, 1);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10000);
        return props;
    }

    @Bean
    public ProducerFactory<Object, Object> apacheProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<Object, Object> apacheKafkaTemplate() {
        return new KafkaTemplate<>(apacheProducerFactory());
    }

}
