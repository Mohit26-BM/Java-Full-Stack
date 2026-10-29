package com.example.bank.account;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Asks the CUSTOMER service whether a customer exists. The service is found through Eureka by name.
 *
 * A call that cannot connect or does not answer in time (2 s to connect, 3 s to read) is retried once, on a randomly
 * chosen instance, before giving up with 503. A definite answer from the customer service (404) is never retried.
 *
 * A @LoadBalanced RestClient.Builder bean is deliberately avoided: Eureka's own HTTP client picks up that bean,
 * which creates a circular dependency and stops this service from registering.
 */
@Component
public class CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerClient.class);
    private static final String CUSTOMER_SERVICE = "CUSTOMER";
    private static final int ATTEMPTS = 2;

    private final DiscoveryClient discovery;
    private final RestClient restClient;

    @Autowired
    public CustomerClient(DiscoveryClient discovery) {
        this(discovery, Duration.ofSeconds(2), Duration.ofSeconds(3));
    }

    /** Timeouts can be shortened for tests. */
    CustomerClient(DiscoveryClient discovery, Duration connectTimeout, Duration readTimeout) {
        this.discovery = discovery;
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(connectTimeout).build());
        factory.setReadTimeout(readTimeout);
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    /** Throws 404 if the customer does not exist and 503 if the customer service cannot be reached. */
    public void requireCustomer(Long customerId, String authorization) {
        for (int attempt = 1; attempt <= ATTEMPTS; attempt++) {
            List<ServiceInstance> instances = discovery.getInstances(CUSTOMER_SERVICE);
            if (instances.isEmpty()) {
                log.warn("No {} instance is registered in Eureka yet", CUSTOMER_SERVICE);
                throw unavailable();
            }
            ServiceInstance instance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
            try {
                restClient.get().uri(instance.getUri() + "/api/v1/customers/{id}", customerId)
                        .header(HttpHeaders.AUTHORIZATION, authorization).retrieve()
                        .toBodilessEntity();
                return;
            } catch (HttpClientErrorException.NotFound ex) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer " + customerId + " does not exist");
            } catch (RestClientException ex) {
                log.warn("Calling {} at {} failed (attempt {} of {})", CUSTOMER_SERVICE, instance.getUri(), attempt, ATTEMPTS, ex);
            }
        }
        throw unavailable();
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Customer service is unavailable, so the customer could not be verified");
    }
}
