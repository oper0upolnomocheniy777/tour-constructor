const sqlite3 = require('sqlite3').verbose();
const db = new sqlite3.Database('./database.db');

db.run('ALTER TABLE tours ADD COLUMN imageUrl TEXT', (err) => {
  if (err) {
    console.error('Ошибка:', err.message);
  } else {
    console.log('✅ Колонка imageUrl добавлена');
  }
  db.close();
});