package es.menasoft.reactiveexamples;

import es.menasoft.reactiveexamples.domain.Person;
import es.menasoft.reactiveexamples.domain.PersonCommand;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ReactiveExampleTest {

    Person harry = new Person("Harry", "Kane");
    Person lesly = new Person("Lesly", "Peacock");
    Person sam = new Person("Sam", "Kane");
    Person ashley = new Person("Ashley", "Peacock");

    @Test
    @SneakyThrows
    public void monoTest() {
        Mono<Person> personMono = Mono.just(harry);
        Person person = personMono.block();
        log.info(person.sayMyName());
    }

    @Test
    @SneakyThrows
    public void monoTransformTest() {
        Mono<Person> personMono = Mono.just(harry);
        PersonCommand personCommand = personMono.map(PersonCommand::new).block();
        log.info(personCommand.sayMyName());
    }

    @Test
    @SneakyThrows
    public void monoTransformFilter() {
        Mono<Person> personMono = Mono.just(harry);
        Person person = personMono.filter(p -> p.getFirstName().equals("foo")).block();
        Assertions.assertThrows(NullPointerException.class, () -> log.info(person.sayMyName()));
    }

    @Test
    @SneakyThrows
    public void fluxTest() {
        Flux<Person> personFlux = Flux.just(harry, lesly, sam, ashley);
        personFlux.subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    @SneakyThrows
    public void fluxTestFilter() {
        Flux<Person> personFlux = Flux.just(harry, lesly, sam, ashley);
        personFlux
                .filter(person -> person.getFirstName().equals(harry.getFirstName()))
                .subscribe(person -> log.info(person.sayMyName()));
    }

    @Test
    @SneakyThrows
    public void fluxTestDelayNoOutput() {
        Flux<Person> personFlux = Flux.just(harry, lesly, sam, ashley);
        personFlux
                .delayElements(Duration.ofSeconds(1))
                .subscribe(person -> log.info(person.sayMyName()));
        // Test terminated before delay expired. Therefore, no log message is displayed.
    }

    @Test
    @SneakyThrows
    public void fluxTestDelay() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> personFlux = Flux.just(harry, lesly, sam, ashley);
        personFlux
                .delayElements(Duration.ofSeconds(1))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }


    @Test
    @SneakyThrows
    public void fluxTestFilterDelay() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux<Person> personFlux = Flux.just(harry, lesly, sam, ashley);
        personFlux
                .delayElements(Duration.ofSeconds(1))
                .filter(person -> person.getLastName().contains("K"))
                .doOnComplete(countDownLatch::countDown)
                .subscribe(person -> log.info(person.sayMyName()));

        countDownLatch.await();
    }


  }
