import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tour } from '../types';
import { getUserTour } from '../services/tourStorage';
import './CheckoutPage.css';

export const CheckoutPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [units, setUnits] = useState(1);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
    if (id) {
      const foundTour = getUserTour(parseInt(id));
      if (foundTour) {
        setTour(foundTour);
      }
    }
    setLoading(false);
  }, [id]);

  const discountedPrice = tour?.discount
    ? tour.price * (1 - tour.discount / 100)
    : tour?.price;

  const totalPrice = discountedPrice ? discountedPrice * units : 0;

  const handlePurchase = () => {
    setIsProcessing(true);
    
    // Имитация отправки заказа
    setTimeout(() => {
      // Сохраняем покупку в localStorage
      const purchases = JSON.parse(localStorage.getItem('purchases') || '[]');
      const newPurchase = {
        id: Date.now(),
        tourId: tour?.id,
        tourTitle: tour?.title,
        units: units,
        price: totalPrice,
        date: new Date().toISOString(),
        status: 'pending'
      };
      purchases.push(newPurchase);
      localStorage.setItem('purchases', JSON.stringify(purchases));
      
      setIsProcessing(false);
      alert(`Заказ оформлен! Сумма: ${totalPrice} ₽`);
      navigate('/my-purchases');
    }, 1000);
  };

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  if (!tour) {
    return (
      <div className="error-page">
        <h2>Тур не найден</h2>
        <button onClick={() => navigate('/tours')}>Вернуться к турам</button>
      </div>
    );
  }

  return (
    <div className="checkout-page">
      <button onClick={() => navigate(-1)} className="back-btn">
        ← Назад
      </button>

      <h1>Оформление покупки</h1>

      <div className="checkout-container">
        <div className="tour-info-card">
          <h2>{tour.title}</h2>
          <div className="tour-meta">
            <span>📍 {tour.destination}</span>
            {tour.hot && <span className="hot-badge">Горячий тур</span>}
            {tour.discount > 0 && (
              <span className="discount-badge">-{tour.discount}%</span>
            )}
          </div>
          <p className="tour-description">
            {tour.description.length > 200
              ? tour.description.substring(0, 200) + '...'
              : tour.description}
          </p>
          {tour.route?.points && (
            <div className="route-info">
              <span>🗺️ {tour.route.points.length} точек маршрута</span>
              {tour.route.totalDistance && (
                <span>📏 {tour.route.totalDistance} км</span>
              )}
            </div>
          )}
        </div>

        <div className="order-form">
          <div className="price-section">
            <div className="price-item">
              <span>Цена за тур:</span>
              {tour.discount > 0 ? (
                <div className="price-with-discount">
                  <span className="old-price">{tour.price} ₽</span>
                  <span className="new-price">{Math.round(discountedPrice!)} ₽</span>
                </div>
              ) : (
                <span className="price">{tour.price} ₽</span>
              )}
            </div>

            <div className="quantity-section">
              <label>Количество:</label>
              <div className="quantity-controls">
                <button
                  onClick={() => setUnits(Math.max(1, units - 1))}
                  disabled={units <= 1}
                >
                  -
                </button>
                <span>{units}</span>
                <button onClick={() => setUnits(units + 1)}>+</button>
              </div>
            </div>

            <div className="total-section">
              <span>Итого:</span>
              <span className="total-price">{totalPrice} ₽</span>
            </div>
          </div>

          <button
            className="purchase-btn"
            onClick={handlePurchase}
            disabled={isProcessing}
          >
            {isProcessing ? 'Обработка...' : 'Подтвердить заказ'}
          </button>
        </div>
      </div>
    </div>
  );
};