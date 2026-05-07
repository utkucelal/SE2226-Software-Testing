// @ts-nocheck
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
  roots: ['<rootDir>/tests'],
  testMatch: ['**/*.test.js'],
};
