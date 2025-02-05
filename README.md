Objective - 
The goal of this implementation is to build a high-performance REST service using Spring Boot and Java:

Stack -   Java 21 and Spring Boot 3. 
Processes at least 10K requests per second.
Accepts unique request IDs and tracks their count every minute.
Supports HTTP GET & POST requests dynamically.
Logs unique request counts every minute.
Works in a distributed environment (multiple instances behind a load balancer).
Ensures only one instance performs scheduled logging.

Implementation Approach
Our implementation is designed for scalability, fault tolerance, and efficiency using the following key components:
 a) REST API (Spring Boot + WebClient)
Endpoint: /verve/accept
Handles concurrent requests efficiently.
Uses WebClient for external API calls (GET & POST requests).
Dynamically processes request types (GET query params, POST JSON body).
Logs HTTP status codes after sending external requests.
 b) High-Performance Unique Request Tracking (Redisson )
Uses RSet<Integer> (Redis Set) for ID deduplication.
Redis ensures uniqueness across multiple instances.
No race conditions or duplicate counting.
Ensures thread-safe via Redisson.
Redis Set (RSet) ensures fast O(1) insert and lookup operations.
 c) Distributed Task Execution (Redisson RLock)
Ensures only one instance logs unique request counts every minute.
Uses Redisson’s RLock (Distributed Lock) to coordinate scheduled logging.
Prevents multiple instances from logging the same count.
Releases the lock after execution to avoid deadlocks.
 d) Asynchronous Processing (WebClient )
Uses WebClient for non-blocking HTTP calls (GET/POST).
Prevents blocking main application threads.
 e)  Kafka as an Event-Driven Message Broker





Steps to run the project -
1. mvn clean install
2. java -jar verve-1.0-SNAPSHOT.jar

