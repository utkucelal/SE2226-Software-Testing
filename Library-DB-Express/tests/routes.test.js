const request = require('supertest');
const app = require('../app');
const { Book, sequelize } = require('../models');

beforeAll(async () => {
  await sequelize.sync({ force: true });
});

afterAll(async () => {
  await sequelize.close();
});

describe('Routes', () => {
  test('When a User enter the site from "/" system must route to "/books"', async () => {
    const res = await request(app).get('/');
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');
  });

  test('the "/books" route must return the existing books', async () => {
    await Book.create({ title: 'Test Book', author: 'Test Author' });
    const res = await request(app).get('/books');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Test Book');
  });

  test('the "/books/new" route must route the user to a new book adding panel', async () => {
    const res = await request(app).get('/books/new');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('New Book');
  });

  test('a post request to "/books/new" should create a book and redirect', async () => {
    const res = await request(app)
        .post('/books/new')
        .send({ title: 'New Title', author: 'New Author', genre: 'Fiction', year: 2026 });
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');

    const book = await Book.findOne({ where: { title: 'New Title' } });
    expect(book).toBeDefined();
    expect(book.author).toBe('New Author');
  });

  test('when creating a new book with post request in "/books/new" must return relevant error messages if a error returned', async () => {
    const res = await request(app)
        .post('/books/new')
        .send({ title: '', author: '' });
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Please Provide a Value For Title');
    expect(res.text).toContain('Please Provide a Value For Author');
  });

  test('POST "/books/new" must render server error page when an unexpected error occurs', async () => {
    jest.spyOn(Book, 'create').mockRejectedValueOnce(new Error('Unexpected DB failure'));
    const res = await request(app)
        .post('/books/new')
        .send({ title: 'Valid Title', author: 'Valid Author' });
    expect(res.text).toContain('A Server Error Occured');
    Book.create.mockRestore();
  });

  test('"/books/:id" route with a valid id must route the user to a update book panel', async () => {
    const book = await Book.create({ title: 'Update Test', author: 'Author' });
    const res = await request(app).get(`/books/${book.id}`);
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Update Test');
  });

  test('a post request to "/books/:id" must update a book and return 302 status code', async () => {
    const book = await Book.create({ title: 'Old Title', author: 'Author' });
    const res = await request(app)
        .post(`/books/${book.id}`)
        .send({ title: 'Updated Title', author: 'Author' });
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');

    const updatedBook = await Book.findByPk(book.id);
    expect(updatedBook.title).toBe('Updated Title');
  });

  test('when updating a book with post request in "/books/:id" must return relevant error messages if a error returned', async () => {
    const book = await Book.create({ title: 'Valid Title', author: 'Author' });
    const res = await request(app)
        .post(`/books/${book.id}`)
        .send({ title: '', author: '' });
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Please Provide a Value For Title');
    expect(res.text).toContain('Please Provide a Value For Author');
  });

  test('POST "/books/:id" must render server error page when an unexpected error occurs', async () => {
    const book = await Book.create({ title: 'Error Test Book', author: 'Author' });
    jest.spyOn(book, 'update').mockRejectedValueOnce(new Error('Unexpected DB failure'));

    // We need to spy on Book.findByPk to return our mocked book instance
    jest.spyOn(Book, 'findByPk').mockResolvedValueOnce(book);

    const res = await request(app)
        .post(`/books/${book.id}`)
        .send({ title: 'Updated', author: 'Author' });
    expect(res.text).toContain('A Server Error Occured');
    Book.findByPk.mockRestore();
  });

  test('when the user sends a GET request to "/books" with a search query it must return the title-filtered results', async () => {
    await Book.create({ title: 'UniqueTitle999', author: 'OtherAuthor', genre: 'Drama', year: 2000 });
    await Book.create({ title: 'AnotherBook', author: 'OtherAuthor', genre: 'Drama', year: 2001 });
    const res = await request(app).get('/books?search=UniqueTitle999');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('UniqueTitle999');
    expect(res.text).not.toContain('AnotherBook');
  });

  test('search by author must return books matching the author field', async () => {
    await Book.create({ title: 'AuthorSearchBook', author: 'UniqueAuthorXYZ', genre: 'Fiction', year: 2010 });
    const res = await request(app).get('/books?search=UniqueAuthorXYZ');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('AuthorSearchBook');
  });

  test('search by genre must return books matching the genre field', async () => {
    await Book.create({ title: 'GenreSearchBook', author: 'SomeAuthor', genre: 'UniqueGenreXYZ', year: 2011 });
    const res = await request(app).get('/books?search=UniqueGenreXYZ');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('GenreSearchBook');
  });

  test('search by year must return books matching the year field', async () => {
    await Book.create({ title: 'YearSearchBook', author: 'SomeAuthor', genre: 'Fiction', year: 1888 });
    const res = await request(app).get('/books?search=1888');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('YearSearchBook');
  });

  test('search with no match must return no book results', async () => {
    const res = await request(app).get('/books?search=ZZZNOMATCH00099');
    expect(res.statusCode).toBe(200);
    expect(res.text).not.toContain('Test Book');
    expect(res.text).not.toContain('New Title');
  });

  test('when the user makes a GET request to "/books" with a page number it must return the limited results', async () => {
    for (let i = 1; i <= 10; i++) {
      await Book.create({ title: `Pagination Book ${i}`, author: 'Author' });
    }
    const page1 = await request(app).get('/books?page=1');
    const page2 = await request(app).get('/books?page=2');

    expect(page1.statusCode).toBe(200);
    expect(page2.statusCode).toBe(200);

    // pages must show different content
    expect(page1.text).not.toBe(page2.text);
  });

  test('page 1 must not show books that belong to page 2', async () => {
    // ensure enough books exist: wipe and create exactly 10 predictable books
    await sequelize.sync({ force: true });
    for (let i = 1; i <= 10; i++) {
      await Book.create({ title: `PagedBook ${String(i).padStart(2, '0')}`, author: 'Author' });
    }
    const page1 = await request(app).get('/books?page=1');
    const page2 = await request(app).get('/books?page=2');

    // Each page holds 5 books; page 1 and page 2 should not overlap
    const page1Books = [...page1.text.matchAll(/PagedBook \d+/g)].map(m => m[0]);
    const page2Books = [...page2.text.matchAll(/PagedBook \d+/g)].map(m => m[0]);

    const overlap = page1Books.filter(b => page2Books.includes(b));
    expect(overlap).toHaveLength(0);
    expect(page1Books.length).toBeGreaterThan(0);
    expect(page2Books.length).toBeGreaterThan(0);
  });

  test('when the user makes a POST request to "/books/:id/delete" must delete the book with the corresponding id', async () => {
    const book = await Book.create({ title: 'Delete Me', author: 'Author' });
    const res = await request(app).post(`/books/${book.id}/delete`);
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');

    const deletedBook = await Book.findByPk(book.id);
    expect(deletedBook).toBeNull();
  });
});