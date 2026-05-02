const request = require('supertest');
const app = require('../app');
const { Book, sequelize } = require('../models');

beforeAll(async () => {
  await sequelize.sync({ force: true }); //tablo temizleme
});

afterAll(async () => {
  await sequelize.close(); //bağlantı kapatma
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
    
    const updatedBook = await Book.findByPk(book.id);
    expect(updatedBook.title).toBe('Updated Title');
  });

  test('when the user when a get request to "/books" send with a search query must return the filtered results', async () => {
    await Book.create({ title: 'Unique Title', author: 'Author' });
    const res = await request(app).get('/books?search=Unique');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Unique Title');
    expect(res.text).not.toContain('Test Book'); // from previous test
  });

  test('when the user make a get request to "/books" send with a page number must return the limited results', async () => {
    for (let i = 1; i <= 10; i++) {
      await Book.create({ title: `Pagination Book ${i}`, author: 'Author' });
    }
    const res = await request(app).get('/books?page=2');
    expect(res.statusCode).toBe(200);
    expect(res.text).toContain('Pagination Book');
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

    const deletedBook = await Book.findByPk(book.id);
    expect(deletedBook).toBeNull();
  });
});
