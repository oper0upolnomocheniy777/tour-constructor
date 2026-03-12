import React from 'react';
import { Link } from 'react-router-dom';
import { Tour } from '../../types';
import './TourCard.css';

interface TourCardProps {
  tour: Tour;
}

export const TourCard: React.FC<TourCardProps> = ({ tour }) => {
  // Вычисляем цену со скидкой
  const discountedPrice = tour.discount > 0 
    ? tour.price * (1 - tour.discount / 100) 
    : tour.price;

  return (
    <div className="tour-card">
      {tour.hot && <div className="hot-badge">Горячее предложение!</div>}
      {tour.discount > 0 && (
        <div className="discount-badge">-{tour.discount}%</div>
      )}
      
      <div className="tour-image">
        {tour.imageUrl ? (
          <img src={tour.imageUrl} alt={tour.title} />
        ) : (
          <div className="no-image">Нет фото</div>
        )}
      </div>

      <div className="tour-content">
        <h3 className="tour-title">
          <Link to={`/tour/${tour.id}`}>{tour.title}</Link>
        </h3>
        
        <div className="tour-destination">{tour.destination}</div>
        
        <p className="tour-description">
          {tour.description.length > 100
            ? tour.description.substring(0, 100) + '...'
            : tour.description}
        </p>

        <div className="tour-rating">
          <div className="stars">
            {[1, 2, 3, 4, 5].map((star) => (
              <span key={star} className={`star ${star <= Math.round(tour.avgRating) ? 'filled' : ''}`}>
                ★
              </span>
            ))}
          </div>
          <span className="votes">({tour.votesCount} отзывов)</span>
        </div>

        <div className="tour-footer">
          <div className="tour-price">
            {tour.discount > 0 ? (
              <>
                <span className="old-price">{tour.price} ₽</span>
                <span className="new-price">{Math.round(discountedPrice)} ₽</span>
              </>
            ) : (
              <span className="price">{tour.price} ₽</span>
            )}
          </div>
          
          <Link to={`/tour/${tour.id}`} className="view-btn">
            Подробнее
          </Link>
        </div>
      </div>
    </div>
  );
};