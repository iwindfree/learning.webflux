package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {
    List<Item> itemList = Arrays.asList(new Item(null,"LG TV",400.0),
                                        new Item(null,"Samsung TV",400.0),
                                        new Item(null,"Apple watch",200.0),
                                        new Item(null,"Macbook",1200.0),
                                        new Item("ABC","NewBOOK",1200.0));
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Before
    public void setup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item-> System.out.println("inserted item:" + item)))
                .blockLast();

    }

    @Test
    public  void getAllItems(){
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public  void getItemByID() {
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item-> item.getDescription().equals("NewBOOK")))
                .verifyComplete();
    }

    @Test
    public void getItemByDesc() {
        StepVerifier.create(itemReactiveRepository.findByDescription("NewBOOK"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {
        Item item= new Item("BBB","Google home mini",100.00);
        Mono<Item> savedItem =  itemReactiveRepository.save(item);
        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches((item1->item1.getId().equals("BBB")))
                .verifyComplete();
    }

    @Test
    public void UpdateItem() {
        double newPrice = 2.0;
        itemReactiveRepository.findById("ABC")
                .map(item->{
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item->{
                    return itemReactiveRepository.save(item);
                });
    }

    @Test
    public void DeleteItem() {
        itemReactiveRepository.findById("ABC")
                .map(Item::getId)
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });
    }

}
