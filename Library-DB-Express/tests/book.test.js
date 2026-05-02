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
  });

  test('should fail validation if title is empty', async () => {
    try {
      await Book.create({
        title: '',
        author: 'Reşat Nuri Güntekin',
        genre: 'Classic',
        year: 1923
      });
    } catch (error) {
      expect(error.name).toBe('SequelizeValidationError');
      expect(error.errors[0].message).toBe('Please Provide a Value For Title');
    }
  });

  test('should fail validation if author is empty', async () => {
    try {
      await Book.create({
        title: 'çalıkuşu',
        author: '',
        genre: 'Classic',
        year: 1925
      });
    } catch (error) {
      expect(error.name).toBe('SequelizeValidationError');
      expect(error.errors[0].message).toBe('Please Provide a Value For Author');
    }
  });
});
