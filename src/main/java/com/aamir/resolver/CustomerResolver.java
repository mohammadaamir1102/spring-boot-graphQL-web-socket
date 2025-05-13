package com.aamir.resolver;

import com.aamir.entity.Customer;
import com.aamir.repo.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CustomerResolver {

    private final CustomerRepository customerRepository;

    /*
       Create a reactive sink that can handle multiple subscribers, using Project Reactor's `Sinks` class.
       This type of sink allows you to emit data that multiple subscribers can consume.

       The `multicast()` method ensures that this sink will send data to all the subscribers simultaneously.
       Without using `multicast()`, the data will only be sent to the first subscriber,
       meaning other subscribers will miss the data.

       The `onBackpressureBuffer()` method is crucial for handling cases where the subscribers are slower than the producer (the one emitting the data).
       If the subscribers can't keep up with the data being emitted, this method will buffer the data in memory
       until the subscribers are ready to process it.
       This ensures no data is lost even if the consumer (subscriber) is slow.
   */

    private final Sinks.Many<Customer> sink = Sinks.many().multicast().onBackpressureBuffer();

    @QueryMapping
    public List<Customer> customers() {
        return customerRepository.findAll();
    }

    @MutationMapping
    public Customer addCustomer(@Argument String name) {
        Customer customer = new Customer();
        customer.setName(name);
        Customer saved = customerRepository.save(customer);
        // Push the saved customer to all subscribers via sink
        sink.tryEmitNext(saved);
        return saved;
    }

    // This method handles the GraphQL "Subscription" for real-time updates
    @SubscriptionMapping
    public Flux<Customer> customerAdded() {
        // Returns a stream (Flux) of customers for subscribers to listen
        return sink.asFlux();
    }
}