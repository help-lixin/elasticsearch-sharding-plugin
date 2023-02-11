package help.lixin.elasticsearch.example.dao;

import help.lixin.elasticsearch.example.model.Book;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface BookRepository extends ElasticsearchRepository<Book, String> {
    List<Book> findByNameAndPrice(String name, Integer price);
}