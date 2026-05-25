const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const db = require('../database');

const router = express.Router();

// Регистрация
router.post('/register', (req, res) => {
  const { username, password, firstName, lastName, telephone } = req.body;
  
  const hashedPassword = bcrypt.hashSync(password, 10);
  
  db.run(
    `INSERT INTO users (username, password, firstName, lastName, telephone)
     VALUES (?, ?, ?, ?, ?)`,
    [username, hashedPassword, firstName, lastName, telephone],
    function(err) {
      if (err) {
        return res.status(400).json({ message: 'Пользователь уже существует' });
      }
      
      const token = jwt.sign(
        { id: this.lastID, username, role: 'user' },
        process.env.JWT_SECRET,
        { expiresIn: '7d' }
      );
      
      res.json({
        token,
        user: {
          id: this.lastID,
          username,
          firstName,
          lastName,
          role: 'user',
          discount: 0
        }
      });
    }
  );
});

// Вход
router.post('/login', (req, res) => {
  const { username, password } = req.body;
  
  db.get('SELECT * FROM users WHERE username = ?', [username], (err, user) => {
    if (err || !user) {
      return res.status(401).json({ message: 'Неверные учётные данные' });
    }
    
    const isValid = bcrypt.compareSync(password, user.password);
    if (!isValid) {
      return res.status(401).json({ message: 'Неверные учётные данные' });
    }
    
    const token = jwt.sign(
      { id: user.id, username: user.username, role: user.role },
      process.env.JWT_SECRET,
      { expiresIn: '7d' }
    );
    
    res.json({
      token,
      user: {
        id: user.id,
        username: user.username,
        firstName: user.firstName,
        lastName: user.lastName,
        role: user.role,
        discount: user.discount,
        telephone: user.telephone
      }
    });
  });
});

// Получить текущего пользователя
router.get('/me', (req, res) => {
  const token = req.headers.authorization?.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ message: 'Нет токена' });
  }
  
  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    db.get('SELECT id, username, firstName, lastName, role, discount FROM users WHERE id = ?', [decoded.id], (err, user) => {
      if (err || !user) {
        return res.status(401).json({ message: 'Пользователь не найден' });
      }
      res.json(user);
    });
  } catch (err) {
    res.status(401).json({ message: 'Неверный токен' });
  }
});

module.exports = router;