import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Tour } from '../../types';
import './TourCard.css';

interface TourCardProps {
  tour: Tour;
}

export const TourCard: React.FC<TourCardProps> = ({ tour }) => {
  const [isHovered, setIsHovered] = useState(false);
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [isTransitioning, setIsTransitioning] = useState(false);
  const [imgErrors, setImgErrors] = useState<boolean[]>([]);

  // Добавим по 3 изображения для каждого тура
  const getTourImages = (tourId: number) => {
    const basePath = '/images/tours/';
    const tourNames: Record<number, string[]> = {
      1: ['paris-1.jpg', 'paris-2.jpg', 'paris-3.jpg'],
      2: ['sochi-1.jpg', 'sochi-2.jpg', 'sochi-3.jpg'],
      3: ['milan-1.jpg', 'milan-2.jpg', 'milan-3.jpg'],
      4: ['golden-ring-1.jpg', 'golden-ring-2.jpg', 'golden-ring-3.jpg'],
      5: ['bali-1.jpg', 'bali-2.jpg', 'bali-3.jpg'],
    };
    
    const images = tourNames[tourId] || ['placeholder.jpg', 'placeholder.jpg', 'placeholder.jpg'];
    return images.map(name => basePath + name);
  };

  const tourImages = getTourImages(tour.id);

  // Автоматическое переключение слайдов при наведении
  useEffect(() => {
    let interval: NodeJS.Timeout;
    let timeout: NodeJS.Timeout;
    
    if (isHovered) {
      interval = setInterval(() => {
        // Начинаем переход
        setIsTransitioning(true);
        
        // Через 300ms меняем изображение и заканчиваем переход
        timeout = setTimeout(() => {
          setCurrentImageIndex((prev) => (prev + 1) % 3);
          setIsTransitioning(false);
        }, 300);
        
      }, 3000);
    } else {
      setCurrentImageIndex(0);
      setIsTransitioning(false);
    }
    
    return () => {
      clearInterval(interval);
      clearTimeout(timeout);
    };
  }, [isHovered]);

  // Вычисляем цену со скидкой
  const discountedPrice = tour.discount > 0 
    ? tour.price * (1 - tour.discount / 100) 
    : tour.price;

  const handleImageError = (index: number) => {
    setImgErrors(prev => {
      const newErrors = [...prev];
      newErrors[index] = true;
      return newErrors;
    });
  };

  const showPlaceholder = imgErrors[currentImageIndex];
  const nextImageIndex = (currentImageIndex + 1) % 3;

  return (
    <div 
      className="tour-card"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {tour.hot && <div className="hot-badge">Горячее предложение!</div>}
      {tour.discount > 0 && (
        <div className="discount-badge">-{tour.discount}%</div>
      )}
      
      <div className="tour-image">
        {/* Текущее изображение */}
        <div className={`image-wrapper ${isTransitioning ? 'fade-out' : ''}`}>
          {!showPlaceholder ? (
            <img 
              src={tourImages[currentImageIndex]} 
              alt={`${tour.title}`}
              onError={() => handleImageError(currentImageIndex)}
            />
          ) : (
            <div className="no-image">Нет фото</div>
          )}
        </div>

        {/* Индикаторы слайдов */}
        {isHovered && (
          <div className="slide-indicators">
            {[0, 1, 2].map((index) => (
              <div 
                key={index}
                className={`indicator ${index === currentImageIndex ? 'active' : ''}`}
                onClick={() => {
                  setCurrentImageIndex(index);
                  setIsTransitioning(false);
                }}
              />
            ))}
          </div>
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