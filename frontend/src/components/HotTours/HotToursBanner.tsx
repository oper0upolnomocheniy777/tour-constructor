import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Tour } from '../../types';
import { getUserTours } from '../../services/tourStorage';
import './HotToursBanner.css';

export const HotToursBanner: React.FC = () => {
  const [hotTours, setHotTours] = useState<Tour[]>([]);
  const [timeLeft, setTimeLeft] = useState<{ [key: number]: string }>({});

  useEffect(() => {
    const allTours = getUserTours();
    const hot = allTours.filter(t => t.hot === true);
    setHotTours(hot);
  }, []);

  useEffect(() => {
    const interval = setInterval(() => {
      const newTimeLeft: { [key: number]: string } = {};
      
      hotTours.forEach(tour => {
        if (tour.hotUntil) {
          const endDate = new Date(tour.hotUntil);
          const now = new Date();
          const diff = endDate.getTime() - now.getTime();
          
          if (diff <= 0) {
            newTimeLeft[tour.id] = 'Акция закончилась';
          } else {
            const hours = Math.floor(diff / (1000 * 60 * 60));
            const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
            const seconds = Math.floor((diff % (1000 * 60)) / 1000);
            newTimeLeft[tour.id] = `${hours}ч ${minutes}м ${seconds}с`;
          }
        } else {
          newTimeLeft[tour.id] = 'Спецпредложение!';
        }
      });
      
      setTimeLeft(newTimeLeft);
    }, 1000);
    
    return () => clearInterval(interval);
  }, [hotTours]);

  if (hotTours.length === 0) {
    return null;
  }

  return (
    <div className="hot-tours-banner">
      <div className="hot-tours-header">
        <div className="header-content">
          <span className="fire-icon">🔥</span>
          <h2>Горящие туры</h2>
          <span className="fire-icon">🔥</span>
        </div>
        <p>Успей забронировать! Предложения с максимальными скидками</p>
      </div>
      
      <div className="hot-tours-grid">
        {hotTours.map(tour => {
          const discountedPrice = tour.discount > 0 
            ? tour.price * (1 - tour.discount / 100) 
            : tour.price;
          
          return (
            <Link to={`/tour/${tour.id}`} key={tour.id} className="hot-tour-card">
              <div className="hot-badge-large">🔥 HOT! -{tour.discount}%</div>
              
              <div className="hot-tour-content">
                <h3>{tour.title}</h3>
                <p className="destination">📍 {tour.destination}</p>
                
                <div className="price-block">
                  <div className="price">
                    <span className="old-price">{tour.price} ₽</span>
                    <span className="new-price">{Math.round(discountedPrice)} ₽</span>
                  </div>
                  <div className="savings">Экономия {tour.price - Math.round(discountedPrice)} ₽</div>
                </div>
                
                <div className="timer">
                  <span className="timer-icon">⏰</span>
                  <span className="timer-text">{timeLeft[tour.id] || 'Спешите!'}</span>
                </div>
                
                <button className="book-btn">Забронировать</button>
              </div>
            </Link>
          );
        })}
      </div>
    </div>
  );
};