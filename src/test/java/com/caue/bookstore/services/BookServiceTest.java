package com.caue.bookstore.services;


import com.caue.bookstore.AIService.AssistantService;
import com.caue.bookstore.dto.BookRequestDTO;
import com.caue.bookstore.dto.BookResponseDTO;
import com.caue.bookstore.entities.Author;
import com.caue.bookstore.entities.Book;
import com.caue.bookstore.entities.BookEvent;
import com.caue.bookstore.entities.Category;
import com.caue.bookstore.enums.CategoryType;
import com.caue.bookstore.exceptions.ResourceNotFoundException;
import com.caue.bookstore.projections.BookProjection;
import com.caue.bookstore.repositories.AuthorRepository;
import com.caue.bookstore.repositories.BookRepository;
import com.caue.bookstore.repositories.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    BookRepository bookRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    AuthorRepository authorRepository;

    @Mock
    AssistantService assistantService;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    BookService bookService;


    private Book book = null;

    @BeforeEach
    void defaultValues(){
        book = new Book();
        book.setAuthors(Set.of(new Author(1L, "Fyodor", "Dostoievsky")));
        book.setCategories(Set.of(new Category(1L, CategoryType.CLASSICS)));
        book.setPrice(new BigDecimal("100.00"));
        book.setStock(100);
        book.setDescription("Test description");
        book.setReleaseDate(LocalDate.now());
        book.setTitle("White Nights");
        book.setCoverImageUrl("https://image.com");
        book.setIsbn(16168541565L);
        book.setId(UUID.randomUUID());

    }


    @Test
    void shouldGetBookByIdSuccessfully(){
        BookProjection mockBookProjection = mock(BookProjection.class);

        List<BookProjection> projectionList = List.of(mockBookProjection);

        when(bookRepository.findBookById(book.getId())).thenReturn(projectionList);

        BookResponseDTO result = bookService.getBookById(book.getId());

        assertNotNull(result);

        verify(bookRepository, times(1)).findBookById(book.getId());
    }

    @Test
    void shouldThrowExceptionGetBookById(){

        when(bookRepository.findBookById(book.getId())).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () ->  bookService.getBookById(book.getId()));

        assertEquals("Book not found.", exception.getMessage());
    }

    @Test
    void shouldGetAllBooksSuccessfully(){
        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<BookResponseDTO> result = bookService.getAllBooks();

        assertNotNull(result);

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void shouldGetAllBooksPaginatedSuccessfully(){
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(book)));

        Page<BookResponseDTO> result = bookService.getAllBooksPaginated(PageRequest.of(0,10));

        assertNotNull(result);

        verify(bookRepository, times(1)).findAll(PageRequest.of(0,10));
    }

    @Test
    void shouldGetAllEventsSuccessfully(){
        BookEvent bookEvent = new BookEvent();

        when(bookRepository.findAllEvents()).thenReturn(List.of(bookEvent));

        List<BookEvent> result = bookService.getBookEvents();

        assertNotNull(result);

        verify(bookRepository,times(1)).findAllEvents();

    }

    @Test
    void shouldGetBooksByCategoryPaginated(){
        when(bookRepository.findByCategories_Type(CategoryType.CLASSICS,PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(book)));

        Page<BookResponseDTO> result =  bookService.getBooksByCategoryPaginated("CLASSICS",PageRequest.of(0,10));

        assertNotNull(result);

        verify(bookRepository,times(1)).findByCategories_Type(CategoryType.CLASSICS,PageRequest.of(0,10));
    }

    @Test
    void shouldSearchBookSuccessfully(){
        when(bookRepository.searchBooks("Some book",PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(book)));

       Page<BookResponseDTO> result =  bookService.searchBooks("Some book",PageRequest.of(0,10));

       assertNotNull(result);

        verify(bookRepository,times(1)).searchBooks("Some book", PageRequest.of(0,10));
    }

    @Test
    void shouldInsertNewBookSuccessfully(){
        BookRequestDTO bookRequestDTO = new BookRequestDTO();

        bookRequestDTO.setId(UUID.randomUUID());
        bookRequestDTO.setIsbn(2315415645L);
        bookRequestDTO.setPrice(new BigDecimal("100.00"));
        bookRequestDTO.setTitle("Random Title");
        bookRequestDTO.setDescription("Random Text");
        bookRequestDTO.setReleaseDate(LocalDate.now());
        bookRequestDTO.setCoverImageUrl("img.jpg");
        bookRequestDTO.setStock(100);
        bookRequestDTO.setAuthorsIds(Set.of(1L,2L));
        bookRequestDTO.setCategoriesIds(Set.of(1L,2L));

        Category category = new Category();
        Author author = new Author();


        when(assistantService.extractBookData(bookRequestDTO.getTitle())).thenReturn(new BookRequestDTO());

        when(categoryRepository.findAllById(bookRequestDTO.getCategoriesIds())).thenReturn(List.of(category));

        when(authorRepository.findAllById(bookRequestDTO.getAuthorsIds())).thenReturn(List.of(author));

        when(bookRepository.saveAndFlush(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponseDTO bookResponseDTO = bookService.insertNewBook(bookRequestDTO);

        assertNotNull(bookResponseDTO);

        verify(categoryRepository, times(1)).findAllById(bookRequestDTO.getCategoriesIds());
        verify(authorRepository, times(1)).findAllById(bookRequestDTO.getAuthorsIds());
        verify(bookRepository, times(1)).saveAndFlush(any(Book.class));
    }

    @Test
    void shouldUpdateBookSuccessfully(){
        Set<String> keySet = new HashSet<>();
        Map<String,Object> map = new HashMap<>();

        map.put("stock", 200);

        when(bookRepository.findById(book.getId())).thenReturn(Optional.of(book));
        when(modelMapper.map(200,Integer.class)).thenReturn(200);

        when(modelMapper.map(book,BookResponseDTO.class)).thenReturn(new BookResponseDTO());

        BookResponseDTO result = bookService.updateBook(book.getId(),keySet,map);

        assertNotNull(result);

    }

    @Test
    void shouldDeleteBookByIdSuccessfully(){

        when(bookRepository.existsById(book.getId())).thenReturn(true);

        doNothing().when(bookRepository).deleteById(book.getId());

        bookService.deleteBookById(book.getId());

        verify(bookRepository, times(1)).deleteById(book.getId());
    }

    @Test
    void shouldDeleteBookByIdFail(){

        when(bookRepository.existsById(book.getId())).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> bookService.deleteBookById(book.getId()));

        assertEquals("Book not found.", exception.getMessage());

    }
}
