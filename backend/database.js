const sqlite3 = require('sqlite3').verbose();
const path = require('path');

const db = new sqlite3.Database(path.join(__dirname, 'database.db'));

// Создаём таблицы, если их нет
db.serialize(() => {
  // Пользователи
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username TEXT UNIQUE NOT NULL,
      password TEXT NOT NULL,
      firstName TEXT,
      lastName TEXT,
      telephone TEXT,
      role TEXT DEFAULT 'user',
      discount INTEGER DEFAULT 0,
      createdAt DATETIME DEFAULT CURRENT_TIMESTAMP
    )
  `);

  // Туры
  db.run(`
  CREATE TABLE IF NOT EXISTS tours (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    description TEXT,
    destination TEXT,
    type TEXT,
    price INTEGER,
    discount INTEGER DEFAULT 0,
    hot BOOLEAN DEFAULT 0,
    hotUntil DATETIME,
    userId INTEGER,
    route TEXT,
    imageUrl TEXT,
    avgRating REAL DEFAULT 0,
    votesCount INTEGER DEFAULT 0,
    isPublic BOOLEAN DEFAULT 0,
    createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES users(id)
  )
`);

  // Покупки
  db.run(`
    CREATE TABLE IF NOT EXISTS purchases (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      userId INTEGER NOT NULL,
      tourId INTEGER NOT NULL,
      units INTEGER DEFAULT 1,
      price INTEGER,
      status TEXT DEFAULT 'pending',
      createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (userId) REFERENCES users(id),
      FOREIGN KEY (tourId) REFERENCES tours(id)
    )
  `);

  // Отзывы
  db.run(`
    CREATE TABLE IF NOT EXISTS reviews (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      userId INTEGER NOT NULL,
      tourId INTEGER NOT NULL,
      rating INTEGER CHECK(rating >= 1 AND rating <= 5),
      text TEXT,
      createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (userId) REFERENCES users(id),
      FOREIGN KEY (tourId) REFERENCES tours(id),
      UNIQUE(userId, tourId)
    )
  `);

  // Добавляем тестового пользователя
  const bcrypt = require('bcryptjs');
  const testPassword = bcrypt.hashSync('123456', 10);
  
  db.run(`
    INSERT OR IGNORE INTO users (username, password, firstName, lastName, role)
    VALUES ('test', ?, 'Тест', 'Тестов', 'user')
  `, [testPassword]);
  
  db.run(`
    INSERT OR IGNORE INTO users (username, password, firstName, lastName, role)
    VALUES ('agent', ?, 'Агент', 'Туров', 'agent')
  `, [testPassword]);
});

module.exports = db;