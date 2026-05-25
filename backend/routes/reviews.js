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

router.get('/purchases/check/:tourId', authMiddleware, (req, res) => {
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

// Получить отзывы тура
router.get('/tour/:tourId', (req, res) => {
  db.all(
    `SELECT r.*, u.username as userName
     FROM reviews r
     JOIN users u ON r.userId = u.id
     WHERE r.tourId = ?
     ORDER BY r.createdAt DESC`,
    [req.params.tourId],
    (err, reviews) => {
      if (err) {
        return res.status(500).json({ message: 'Ошибка сервера' });
      }
      res.json(reviews);
    }
  );
});

// Добавить отзыв
router.post('/', authMiddleware, (req, res) => {
  const { tourId, rating, text } = req.body;
  
  db.run(
    `INSERT INTO reviews (userId, tourId, rating, text)
     VALUES (?, ?, ?, ?)`,
    [req.user.id, tourId, rating, text],
    function(err) {
      if (err) {
        return res.status(500).json({ message: 'Ошибка при добавлении отзыва' });
      }
      
      // Возвращаем созданный отзыв с данными пользователя
      db.get(
  `SELECT r.*, u.username as userName 
   FROM reviews r
   JOIN users u ON r.userId = u.id
   WHERE r.id = ?`,
  [this.lastID],
  (err, review) => {
    if (err) {
      return res.status(500).json({ message: 'Ошибка получения отзыва' });
    }
    // Добавляем поле date для совместимости с фронтендом
    review.date = review.createdAt;
    res.status(201).json(review);
  }
);
    }
  );
});

module.exports = router;