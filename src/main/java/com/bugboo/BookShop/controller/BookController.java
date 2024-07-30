package com.bugboo.BookShop.controller;

import com.bugboo.BookShop.domain.Book;
import com.bugboo.BookShop.domain.Category;
import com.bugboo.BookShop.domain.dto.response.ResponsePagingResultDTO;
import com.bugboo.BookShop.service.BookService;
import com.bugboo.BookShop.service.FileUploadService;
import com.bugboo.BookShop.type.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class BookController {

    private final BookService bookService;
    private final FileUploadService fileUploadService;
    @Autowired
    public BookController(BookService bookService, FileUploadService fileUploadService) {
        this.bookService = bookService;
        this.fileUploadService = fileUploadService;
    }

    //CRUD operations for book
    // get all books
    @ApiMessage("Get all books successfully")
    @GetMapping("/public/books")
    public ResponseEntity<ResponsePagingResultDTO> getAllBooks(@Filter Specification<Book> specification, Pageable pageable) {
        return ResponseEntity.ok(bookService.getAllBooks(specification,pageable));
    }

    // get book by id
    @ApiMessage("Get book by id successfully")
    @GetMapping("/public/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable  int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    // add book
    @ApiMessage("Add book successfully")
    @PostMapping("/admin/books")
    public ResponseEntity<Book> addBook(@Valid  Book book, @RequestParam(name = "file",required = false ) MultipartFile file) throws IOException {
        if(file != null){
            book.setImage(fileUploadService.uploadSingleFile(file,"books").get("url").toString());
        }
        return ResponseEntity.ok(bookService.addBook(book));
    }

    // update book
    @PutMapping("/admin/books")
    @ApiMessage("Update book successfully")
    public ResponseEntity<Book> updateBook(@Valid Book book, @RequestParam(name = "file", required = false) MultipartFile file) throws IOException {
        if(file != null){
            book.setImage(fileUploadService.uploadSingleFile(file,"books").get("url").toString());
        }
        return ResponseEntity.ok(bookService.updateBook(book));
    }

    // delete book
    @DeleteMapping("/admin/books/{id}")
    @ApiMessage("Delete book successfully")
    public ResponseEntity<Book> deleteBook(@PathVariable  int id) {
        bookService.deleteBook(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
