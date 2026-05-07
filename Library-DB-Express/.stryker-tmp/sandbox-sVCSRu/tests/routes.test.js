// @ts-nocheck
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

  test('when creating a new book with post request in "/books/new" must return relevant error messages if a error returned ', async () => {
    const res = await request(app)
      .post('/books/new')
      .send({ title: '', author: '' });
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Please Provide a Value For Title');
    expect(res.text).toContain('Please Provide a Value For Author');
  });

  test('"/books/:id" route with a valid id must route the user to a update book panel', async () => {
    const book = await Book.create({ title: 'Update Test', author: 'Author' });
    const res = await request(app).get(`/books/${book.id}`);
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Update Test');
  });

  test('a post request to "/books/:id" must update a book and return 302 status code ', async () => {
    const book = await Book.create({ title: 'Old Title', author: 'Author' });
    const res = await request(app)
      .post(`/books/${book.id}`)
      .send({ title: 'Updated Title', author: 'Author' });
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');

    const updatedBook = await Book.findByPk(book.id);
    expect(updatedBook.title).toBe('Updated Title');
  });

  test('when the user when a get request to "/books" send with a search query must return the filtered results', async () => {
    await Book.create({ title: 'Unique Title XYZ', author: 'Author' });
    const res = await request(app).get('/books?search=Unique+Title+XYZ');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Unique Title XYZ');
    expect(res.text).not.toContain('New Title');
  });

  // fix #7: stronger pagination test — verify page 1 books are absent on page 2
  test('when the user make a get request to "/books" send with a page number must return the limited results', async () => {
    await sequelize.sync({ force: true });
    for (let i = 1; i <= 10; i++) {
      await Book.create({ title: `Pagination Book ${i}`, author: 'Author' });
    }

    const page1 = await request(app).get('/books?page=1');
    expect(page1.statusCode).toBe(200);
    expect(page1.text).toContain('Pagination Book 1');
    // 'Pagination Book 6' is on page 2 — must be absent from page 1
    expect(page1.text).not.toContain('Pagination Book 6');

    const page2 = await request(app).get('/books?page=2');
    expect(page2.statusCode).toBe(200);
    expect(page2.text).toContain('Pagination Book 6');
    // 'Pagination Book 5' is on page 1 — must be absent from page 2
    // Using 5 (not 1) avoids the false substring match: 'Pagination Book 10'.includes('Pagination Book 1') === true
    expect(page2.text).not.toContain('Pagination Book 5');
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

  test('when the user make a post request to "/books/:id/delete" must delete the book with corresponding id', async () => {
    const book = await Book.create({ title: 'Delete Me', author: 'Author' });
    const res = await request(app).post(`/books/${book.id}/delete`);
    expect(res.statusCode).toBe(302);
    expect(res.headers.location).toBe('/books');

    const deletedBook = await Book.findByPk(book.id);
    expect(deletedBook).toBeNull();
  });

  // fix #5: 404 for unknown route and non-existent book id
  test('GET on an unknown route must return 404', async () => {
    const res = await request(app).get('/nonexistent-route');
    expect(res.statusCode).toBe(404);
  });

  test('GET /books/:id with a non-existent id must return 404', async () => {
    const res = await request(app).get('/books/99999');
    expect(res.statusCode).toBe(404);
  });
});
