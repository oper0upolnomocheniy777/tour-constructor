const express = require('express');
const jwt = require('jsonwebtoken');
const db = require('../database');

const router = express.Router();

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

// Для обычных пользователей — только свои покупки (используется в MyPurchasesPage)
router.get('/my', authMiddleware, (req, res) => {
  db.all(
    `SELECT p.*, t.title as tourTitle 
     FROM purchases p
     JOIN tours t ON p.tourId = t.id
     WHERE p.userId = ?
     ORDER BY p.createdAt DESC`,
    [req.user.id],
    (err, purchases) => {
      if (err) {
        return res.status(500).json({ message: 'Ошибка сервера' });
      }
      res.json(purchases);
    }
  );
});

// Для админ-панели — все покупки (только для агента)
router.get('/all', authMiddleware, (req, res) => {
  if (req.user.role !== 'agent') {
    return res.status(403).json({ message: 'Доступ только для агентов' });
  }
  
  db.all(
    `SELECT p.*, t.title as tourTitle, u.username as userName
     FROM purchases p
     JOIN tours t ON p.tourId = t.id
     JOIN users u ON p.userId = u.id
     ORDER BY p.createdAt DESC`,
    [],
    (err, purchases) => {
      if (err) {
        console.error('SQL error:', err);
        return res.status(500).json({ message: 'Ошибка сервера' });
      }
      res.json(purchases || []);
    }
  );
});

// Купить тур
router.post('/', authMiddleware, (req, res) => {
  const { tourId, units } = req.body;
  
  db.get('SELECT price, discount FROM tours WHERE id = ?', [tourId], (err, tour) => {
    if (err || !tour) {
      return res.status(404).json({ message: 'Тур не найден' });
    }
    
    const finalPrice = tour.price * (1 - (tour.discount / 100));
    const totalPrice = finalPrice * (units || 1);
    
    db.run(
      `INSERT INTO purchases (userId, tourId, units, price)
       VALUES (?, ?, ?, ?)`,
      [req.user.id, tourId, units || 1, totalPrice],
      function(err) {
        if (err) {
          return res.status(500).json({ message: 'Ошибка при покупке' });
        }
        res.status(201).json({ id: this.lastID, message: 'Покупка оформлена' });
      }
    );
  });
});

// Обновление статуса заказа
router.patch('/:id', authMiddleware, (req, res) => {
  const { status } = req.body;
  
  db.run(
    'UPDATE purchases SET status = ? WHERE id = ?',
    [status, req.params.id],
    function(err) {
      if (err) {
        return res.status(500).json({ message: 'Ошибка обновления' });
      }
      res.json({ message: 'Статус обновлён' });
    }
  );
});

// Проверка, может ли пользователь оставить отзыв
router.get('/check/:tourId', authMiddleware, (req, res) => {
  db.get(
    `SELECT id FROM purchases WHERE userId = ? AND tourId = ? AND status != 'cancelled'`,
    [req.user.id, req.params.tourId],
    (err, purchase) => {
      if (err) {
        return res.status(500).json({ message: 'Ошибка сервера' });
      }
      res.json({ canReview: !!purchase });
    }
  );
});

module.exports = router;