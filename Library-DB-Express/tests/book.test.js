const { Book, sequelize } = require('../models');

beforeAll(async () => {
  await sequelize.sync({ force: true });
});

afterAll(async () => {
  // do not close the shared sequelize connection here — closing it corrupts
  // the cached module for other test files in Stryker's Jest runner
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

  test('should fail validation if title is empty string', async () => {
    await expect(
        Book.create({ title: '', author: 'Reşat Nuri Güntekin', genre: 'Classic', year: 1923 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError',
      errors: expect.arrayContaining([
        expect.objectContaining({ message: 'Please Provide a Value For Title' })
      ])
    });
  });

  test('should fail validation if title is null', async () => {
    await expect(
        Book.create({ title: null, author: 'Reşat Nuri Güntekin', genre: 'Classic', year: 1923 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError'
    });
  });

  test('should fail validation if author is empty string', async () => {
    await expect(
        Book.create({ title: 'Çalıkuşu', author: '', genre: 'Classic', year: 1925 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError',
      errors: expect.arrayContaining([
        expect.objectContaining({ message: 'Please Provide a Value For Author' })
      ])
    });
  });

  test('should fail validation if author is null', async () => {
    await expect(
        Book.create({ title: 'Çalıkuşu', author: null, genre: 'Classic', year: 1925 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError'
    });
  });

  test('should fail validation if both title and author are empty', async () => {
    await expect(
        Book.create({ title: '', author: '', genre: 'Classic', year: 1925 })
    ).rejects.toMatchObject({
      name: 'SequelizeValidationError',
      errors: expect.arrayContaining([
        expect.objectContaining({ message: 'Please Provide a Value For Title' }),
        expect.objectContaining({ message: 'Please Provide a Value For Author' })
      ])
    });
  });

  test('should allow genre and year to be omitted', async () => {
    const book = await Book.create({ title: 'Minimal Book', author: 'Some Author' });
    expect(book.id).toBeDefined();
    expect(book.genre).toBeUndefined()
    expect(book.year).toBeUndefined()
  });
});