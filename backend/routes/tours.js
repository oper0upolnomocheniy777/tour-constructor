const express = require('express');
const jwt = require('jsonwebtoken');
const db = require('../database');

const router = express.Router();

// Middleware для проверки токена
const authMiddleware = (req, res, next) => {
  const token = req.headers.authorization?.split(' ')[1];
  if (!token) {
    return res.status(401).json({ message: 'Нет доступа' });
  }
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    req.user = decoded;
    next();
  } catch (err) {
    res.status(401).json({ message: 'Неверный токен' });
  }
};

// Middleware для проверки роли агента
const agentMiddleware = (req, res, next) => {
  if (req.user.role !== 'agent') {
    return res.status(403).json({ message: 'Доступ только для агентов' });
  }
  next();
};

// Получить все туры
router.get('/', (req, res) => {
  db.all('SELECT * FROM tours ORDER BY id DESC', [], (err, tours) => {
    if (err) {
      return res.status(500).json({ message: 'Ошибка сервера' });
    }
    res.json(tours.map(t => ({ ...t, route: t.route ? JSON.parse(t.route) : null })));
  });
});

// Получить все публичные туры (для главной страницы)
router.get('/public', (req, res) => {
  db.all('SELECT * FROM tours WHERE isPublic = 1 ORDER BY id DESC', [], (err, tours) => {
    if (err) {
      return res.status(500).json({ message: 'Ошибка сервера' });
    }
    res.json(tours.map(t => ({ ...t, route: t.route ? JSON.parse(t.route) : null })));
  });
});

// Получить тур по ID
router.get('/:id', (req, res) => {
  db.get('SELECT * FROM tours WHERE id = ?', [req.params.id], (err, tour) => {
    if (err || !tour) {
      return res.status(404).json({ message: 'Тур не найден' });
    }
    res.json({ ...tour, route: tour.route ? JSON.parse(tour.route) : null });
  });
});

router.get('/user/me', authMiddleware, (req, res) => {
  console.log('User ID:', req.user.id); // ← добавить
  db.all('SELECT * FROM tours WHERE userId = ? ORDER BY id DESC', [req.user.id], (err, tours) => {
    if (err) {
      console.error(err);
      return res.status(500).json({ message: 'Ошибка сервера' });
    }
    console.log('Tours found:', tours); // ← добавить
    res.json(tours || []);
  });
});

// Создать тур (только агент)
router.post('/', authMiddleware, (req, res) => {
  const { title, description, destination, type, price, discount, route, imageUrl } = req.body;
  const userId = req.user.id;
  const userRole = req.user.role;
  
  const publicFlag = userRole === 'agent' ? 1 : 0;
  
  db.run(
    `INSERT INTO tours (title, description, destination, type, price, discount, route, userId, isPublic, imageUrl)
     VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
    [title, description, destination, type, price, discount || 0, JSON.stringify(route || {}), userId, publicFlag, imageUrl || ''],
    function(err) {
      if (err) {
        console.error('Create error:', err);
        return res.status(500).json({ message: 'Ошибка при создании тура' });
      }
      res.status(201).json({ id: this.lastID, message: 'Тур создан' });
    }
  );
});

// Обновить тур (только агент)
router.put('/:id', authMiddleware, (req, res) => {
  const { title, description, destination, type, price, discount, hot, hotUntil, route, imageUrl } = req.body;
  
  console.log('Updating tour:', req.params.id, req.body);
  
  const sql = `
    UPDATE tours SET 
      title = COALESCE(?, title),
      description = COALESCE(?, description),
      destination = COALESCE(?, destination),
      type = COALESCE(?, type),
      price = COALESCE(?, price),
      discount = COALESCE(?, discount),
      hot = COALESCE(?, hot),
      hotUntil = COALESCE(?, hotUntil),
      route = COALESCE(?, route),
      imageUrl = COALESCE(?, imageUrl)
    WHERE id = ?
  `;
  
  const params = [
    title, description, destination, type, price, 
    discount, hot ? 1 : 0, hotUntil, JSON.stringify(route || {}), imageUrl || '',
    req.params.id
  ];
  
  db.run(sql, params, function(err) {
    if (err) {
      console.error('Update error:', err.message);
      return res.status(500).json({ message: 'Ошибка при обновлении: ' + err.message });
    }
    
    if (this.changes === 0) {
      return res.status(404).json({ message: 'Тур не найден' });
    }
    
    res.json({ message: 'Тур обновлён' });
  });
});


// Удалить тур (только агент)
router.delete('/:id', authMiddleware, agentMiddleware, (req, res) => {
  db.run('DELETE FROM tours WHERE id = ?', [req.params.id], function(err) {
    if (err) {
      return res.status(500).json({ message: 'Ошибка при удалении' });
    }
    res.json({ message: 'Тур удалён' });
  });
});


module.exports = router;