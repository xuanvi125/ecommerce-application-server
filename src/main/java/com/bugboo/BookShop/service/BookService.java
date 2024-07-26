package com.bugboo.BookShop.service;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Category;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.repository.BookRepository;
import com.bugboo.BookShop.repository.CategoryRepository;
import com.bugboo.BookShop.type.apiResponse.MetaData;
import com.bugboo.BookShop.type.exception.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public BookService(BookRepository bookRepository, CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponsePagingResultDTO getAllBooks(Specification<Book> specification, Pageable pageable) {
        Page<Book> pageBook = bookRepository.findAll(specification, pageable);
        ResponsePagingResultDTO responsePagingResultDTO = new ResponsePagingResultDTO();
        MetaData metaData = new MetaData();
        metaData.setTotalElements(pageBook.getTotalElements());
        metaData.setTotalPages(pageBook.getTotalPages());
        metaData.setCurrentPage(pageBook.getNumber() + 1);
        responsePagingResultDTO.setMetadata(metaData);
        responsePagingResultDTO.setResult(pageBook.getContent());
        return responsePagingResultDTO;
    }

    public Book getBookById(int id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new AppException("Book not found",404);
        }
        return book;
    }

    public Book addBook(Book book) {
        Category category = categoryRepository.findById(book.getCategory().getId()).orElse(null);
        if (category == null) {
                throw new AppException("Category not found",400);
        }
        return bookRepository.save(book);
    }

    public void deleteBook(int id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new AppException("Book not found",400);
        }
        bookRepository.deleteById(id);
    }

    public Book updateBook(Book book) {
        Book bookDB = bookRepository.findById(book.getId()).orElse(null);
        if (bookDB == null) {
            throw new AppException("Book not found",400);
        }
        return bookRepository.save(book);
    }

    public Book findById(int id) {
        return bookRepository.findById(id).orElse(null);
    }
}
