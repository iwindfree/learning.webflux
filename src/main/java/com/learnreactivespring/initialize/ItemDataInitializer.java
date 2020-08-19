package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    public List<Item> Data() {
        List<Item> itemList = Arrays.asList(new Item(null,"LG TV",400.0),
                new Item(null,"Samsung TV",400.0),
                new Item(null,"Apple watch",200.0),
                new Item(null,"Macbook",1200.0),
                new Item("ABC","NewBOOK",1200.0),
                new Item("DCF","NewBOOK2",1300.0));
        return itemList;
    }
    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(Data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item->{
                    System.out.println("thread:" + Thread.currentThread().getName() +  "  Item inserted :" + item);
                });




    }
}
