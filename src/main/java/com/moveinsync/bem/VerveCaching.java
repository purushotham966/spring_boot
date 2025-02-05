package com.moveinsync.bem;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class VerveCaching {

    @Autowired
    RedissonClient redissonClient;
    @Autowired
    private KafkaTemplate<Object, Object> apacheKafkaTemplate;


    private final RSet<Integer> uniqueRequestSet;
    private final RLock lock;

    public VerveCaching(RedissonClient redissonClient) {

        uniqueRequestSet = redissonClient.getSet("unique_request_counter");

        lock = redissonClient.getLock("unique_requests_lock");

    }


    public void registerRequest(Integer id) {
        uniqueRequestSet.add(id);
    }

    public int getUniqueCount() {
        return uniqueRequestSet.size();
    }

    public void resetCounter() {
        uniqueRequestSet.clear();
    }


    @Scheduled(fixedRate = 60000)
    public void logUniqueRequests() {
        if (lock.tryLock()) {
            try {
                int count = getUniqueCount();
                log.info("Unique requests in the last minute: {}", count);

                resetCounter();
                publishToKafka(count);
            } finally {
                lock.unlock();
            }
        } else {
            log.debug("Another instance is handling the logging task.");
        }
    }

    private void publishToKafka(int count) {

        apacheKafkaTemplate.send("unique-request-count", count);
    }

}
