package com.gerp.elasticsearch.example;

import help.lixin.elasticsearch.example.ElasticSearchExampleApplication;
import help.lixin.elasticsearch.example.ctx.TenantContext;
import help.lixin.elasticsearch.example.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = ElasticSearchExampleApplication.class)
public class ElasticSearchExampleApplicationTest {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Test
    public void testSave() {
        TenantContext.setTenantCode("00007");
        Book book = new Book();
        book.setId("2");
        book.setName("张三");
        book.setPrice(90);
        book.setSummary("test");
        elasticsearchOperations.save(book);
    }

    @Test
    public void testBatchSave() {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    TenantContext.setTenantCode("00007");
                    Book book = new Book();
                    book.setId(String.valueOf(index));
                    book.setName("张三");
                    book.setPrice(90);
                    book.setSummary("test");
                    elasticsearchOperations.save(book);
                }
            });
            threads.add(t);
        } // end for

        //
        for (Thread t : threads) {
            t.start();
        }

        try {
            TimeUnit.SECONDS.sleep(60);
        } catch (Exception ignore) {
        }
    }

    @Test
    public void testBatchSaveList() {
        TenantContext.setTenantCode("00007");

        List<Book> books = new ArrayList<>();

        Book book1 = new Book();
        book1.setId("1");
        book1.setName("张三");
        book1.setPrice(90);
        book1.setSummary("test");

        Book book2 = new Book();
        book2.setId("2");
        book2.setName("张三");
        book2.setPrice(90);
        book2.setSummary("test");

        books.add(book1);
        books.add(book2);

        elasticsearchOperations.save(books);
    }
}
