module.exports = {
  testEnvironment: 'node',
  coverageDirectory: 'coverage',
  collectCoverageFrom: [
    'routes/**/*.js',
    'models/**/*.js',
    'app.js',
    '!**/node_modules/**',
    '!**/migrations/**',
    '!**/config/**'
  ],
  testMatch: ['**/tests/**/*.test.js'],
};
