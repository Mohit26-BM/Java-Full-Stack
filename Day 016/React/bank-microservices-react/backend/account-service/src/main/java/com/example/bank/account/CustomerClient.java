package com.example.bank.account;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Asks the CUSTOMER service whether a customer exists. The service is found through Eureka by name.
 *
 * A @LoadBalanced RestClient.Builder bean is deliberately avoided: Eureka's own HTTP client picks up that bean,
 * which creates a circular dependency and stops this service from registering.
 */
@Component
public class CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(CustomerClient.class);
    private static final String CUSTOMER_SERVICE = "CUSTOMER";

    private final DiscoveryClient discovery;
    private final RestClient restClient = RestClient.create();

    public CustomerClient(DiscoveryClient discovery) {
        this.discovery = discovery;
    }

    /** Throws 404 if the customer does not exist and 503 if the customer service cannot be reached. */
    public void requireCustomer(Long customerId) {
        List<ServiceInstance> instances = discovery.getInstances(CUSTOMER_SERVICE);
        if (instances.isEmpty()) {
            log.warn("No {} instance is registered in Eureka yet", CUSTOMER_SERVICE);
            throw unavailable();
        }
        ServiceInstance instance = instances.get(ThreadLocalRandom.current().nextInt(instances.size()));
        try {
            restClient.get().uri(instance.getUri() + "/api/v1/customers/{id}", customerId).retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer " + customerId + " does not exist");
        } catch (RestClientException ex) {
            log.warn("Calling {} at {} failed", CUSTOMER_SERVICE, instance.getUri(), ex);
            throw unavailable();
        }
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "Customer service is unavailable, so the customer could not be verified");
    }
}
