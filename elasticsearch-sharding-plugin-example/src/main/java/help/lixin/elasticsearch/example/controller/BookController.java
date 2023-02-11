package help.lixin.elasticsearch.example.controller;

import help.lixin.elasticsearch.example.ctx.TenantContext;
import help.lixin.elasticsearch.example.dao.BookRepository;
import help.lixin.elasticsearch.example.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BookController {
    private ElasticsearchOperations elasticsearchOperations;
    private BookRepository bookRepository;

    public BookController(ElasticsearchOperations elasticsearchOperations, BookRepository bookRepository) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.bookRepository = bookRepository;
    }

    /**
     * @return
     */
    @GetMapping("/mapping")
    public String index() {
        IndexOperations indexOperations = elasticsearchOperations.indexOps(Book.class);
        indexOperations.create();
        indexOperations.putMapping(Book.class);
        return "SUCCESS";
    }

    /**
     * curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{ "id":"1" ,"name":"hello","su
     * mmary":"test","price":80 }'   'http://127.0.0.1:8080/book'
     * curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{ "id":"2" ,"name":"world","su
     * mmary":"test2","price":90 }'   'http://127.0.0.1:8080/book'
     * curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{ "id":"3" ,"name":"hello","su
     * mmary":"test","price":60 }'   'http://127.0.0.1:8080/book'
     * curl -X POST -H "Accept: application/json" -H "Content-type: application/json" -d '{ "id":"4" ,"name":"hello","su
     * mmary":"test","price":70 }'   'http://127.0.0.1:8080/book'
     *
     * @param book
     * @return
     */
    @PostMapping("/book")
    public String save(@RequestBody Book book) {
        TenantContext.setTenantCode("00007");
        Book bookTemp = elasticsearchOperations.save(book);
        return bookTemp.getId();
    }

    /**
     * curl -X DELETE http://localhost:8080/book/id/2222
     *
     * @param id
     */
    @DeleteMapping("/book/id/{id}")
    public void delById(@PathVariable("id") String id) {
        TenantContext.setTenantCode("00007");
        elasticsearchOperations.delete(id, Book.class);
        return;
    }

    /**
     * curl -X DELETE 'http://localhost:8080/book'
     */
    @DeleteMapping("/book")
    public void delByName() {
        TenantContext.setTenantCode("00007");
        Criteria criteria = new Criteria("name").is("张三3");
        Query criteriaQuery = new CriteriaQuery(criteria);
        elasticsearchOperations.delete(criteriaQuery, Book.class);
        return;
    }

    /**
     * curl 'http://127.0.0.1:8080/books'
     */
    @GetMapping("/books")
    public void saves() {
        TenantContext.setTenantCode("00007");
        Book book1 = new Book();
        book1.setId("2222");
        book1.setName("张三2");
        book1.setSummary("test");
        book1.setPrice(80);

        Book book2 = new Book();
        book2.setId("3333");
        book2.setName("张三3");
        book2.setSummary("test3");
        book2.setPrice(88);

        elasticsearchOperations.save(book1, book2);
        return;
    }


    /**
     * curl -X GET http://localhost:8080/book/1
     *
     * @param id
     * @return
     */
    @GetMapping("/book/{id}")
    public Book findById(@PathVariable("id") String id) {
        TenantContext.setTenantCode("00007");
        Book book = elasticsearchOperations.get(id, Book.class, IndexCoordinates.of("books"));
        return book;
    }

    /**
     * curl -X GET 'http://localhost:8080/query/hello'
     *
     * @param name
     * @return
     */
    @GetMapping("/query/{name}")
    public List<Book> query(@PathVariable("name") String name) {
        TenantContext.setTenantCode("00007");
        List<Book> result = bookRepository.findByNameAndPrice(name, 80);
        return result;
    }

    /**
     * curl -X PUT 'http://localhost:8080/id/1'
     *
     * @param id
     * @return
     */
    @PutMapping("/id/{id}")
    public UpdateResponse update(@PathVariable("id") String id) {
        TenantContext.setTenantCode("00007");
        org.springframework.data.elasticsearch.core.document.Document document = org.springframework.data.elasticsearch.core.document.Document.create();
        document.put("price", 99);
        UpdateQuery updateQuery = UpdateQuery.builder(id).withDocument(document).build();
        UpdateResponse books = elasticsearchOperations.update(updateQuery, IndexCoordinates.of("books"));
        return books;
    }

    /**
     * curl -X PUT 'http://localhost:8080/ids?ids=1'
     *
     * @param ids
     * @return
     */
    @PutMapping("/ids")
    public void update(String[] ids) {
        TenantContext.setTenantCode("00007");
        List<UpdateQuery> updateQueries = new ArrayList<>();
        for (String id : ids) {
            UpdateQuery updateQuery = UpdateQuery.builder(id).withScript("ctx._source['price'] = params['newPrice']").withLang("painless").withParams(Collections.singletonMap("newPrice", 100)).build();
            updateQueries.add(updateQuery);
        }
        elasticsearchOperations.bulkUpdate(updateQueries, Book.class);
        return;
    }


    /**
     * curl -X GET 'http://localhost:8080/search/hello'
     *
     * @param name
     * @return
     */
    @GetMapping("/search/{name}")
    public List<Book> search(@PathVariable("name") String name) {
        TenantContext.setTenantCode("00007");
        // Criteria criteria = new Criteria("name").is("hello").and("price").greaterThan(30);
        Criteria criteria = new Criteria("name").is(name);
        Query criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<Book> search = elasticsearchOperations.search(criteriaQuery, Book.class);
        List<Book> list = search.getSearchHits().stream().map(item -> item.getContent()).collect(Collectors.toList());
        return list;
    }

    /**
     * curl http://localhost:8080/page/hello
     *
     * @param name
     * @return
     */
    @GetMapping("/page/{name}")
    public Page<Book> page(@PathVariable("name") String name) {
        TenantContext.setTenantCode("00007");
        Criteria criteria = new Criteria("name").is(name);
        Query criteriaQuery = new CriteriaQuery(criteria);
        SearchHits<Book> searchHits = elasticsearchOperations.search(criteriaQuery, Book.class);
        SearchPage<Book> searchPage = SearchHitSupport.searchPageFor(searchHits, PageRequest.of(1, 10));
        Page<Book> page = (Page<Book>) SearchHitSupport.unwrapSearchHits(searchPage);
        // 总页数
        int totalPages = page.getTotalPages();
        // 总记录数
        long totalElements = page.getTotalElements();
        // 每页显示多少条
        int size = page.getSize();
        return page;
    }
}