// @ts-nocheck
const { Book, sequelize } = require('../models');

beforeAll(async () => {
  await sequelize.sync({ force: true });
});

afterAll(async () => {
  await sequelize.close();
});

describe('Book Model', () => {
  test('should create a book when all fields are valid', async () => {
    const book = await Book.create({
      title: 'Çalıkuşu',
      author: 'Reşat Nuri Güntekin',
      genre: 'Classic',
      year: 1925
    });
    expect(book.id).toBeDefined();
    expect(book.title).toBe('Çalıkuşu');
    expect(book.author).toBe('Reşat Nuri Güntekin');
    expect(book.genre).toBe('Classic');
    expect(book.year).toBe(1925);
  });

  // fix #3: confirm genre and year are optional
  test('should create a book when only required fields are provided', async () => {
    const book = await Book.create({
      title: 'Minimal Book',
      author: 'Some Author'
    });
    expect(book.id).toBeDefined();
    expect(book.genre).toBeNull();
    expect(book.year).toBeNull();
  });

  // fix #1: use rejects instead of try/catch to avoid silent false positives
  test('should fail validation if title is empty', async () => {
    await expect(
      Book.create({ title: '', author: 'Reşat Nuri Güntekin', genre: 'Classic', year: 1923 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError',
      errors: [{ message: 'Please Provide a Value For Title' }]
    });
  });

  test('should fail validation if author is empty', async () => {
    await expect(
      Book.create({ title: 'Çalıkuşu', author: '', genre: 'Classic', year: 1925 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError',
      errors: [{ message: 'Please Provide a Value For Author' }]
    });
  });

  // fix #2: null tests for allowNull: false (DB-level constraint, separate from notEmpty)
  test('should fail if title is null', async () => {
    await expect(
      Book.create({ title: null, author: 'Some Author' })
    ).rejects.toThrow();
  });

  test('should fail if author is null', async () => {
    await expect(
      Book.create({ title: 'Some Title', author: null })
    ).rejects.toThrow();
  });
});
