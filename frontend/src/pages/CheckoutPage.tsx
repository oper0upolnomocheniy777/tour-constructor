import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Tour } from '../types';
import './CheckoutPage.css';
import { toast } from 'sonner';
import { toursApi, purchasesApi } from '../services/api';

export const CheckoutPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [units, setUnits] = useState(1);
  const [isProcessing, setIsProcessing] = useState(false);

  useEffect(() => {
  const fetchTour = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const response = await toursApi.getById(parseInt(id));
      setTour(response.data);
    } catch (error) {
      console.error('Ошибка загрузки тура:', error);
      toast.error('Тур не найден');
    } finally {
      setLoading(false);
    }
  };
  
  fetchTour();
}, [id]);

  const discountedPrice = tour?.discount
    ? tour.price * (1 - tour.discount / 100)
    : tour?.price;

  const totalPrice = discountedPrice ? discountedPrice * units : 0;

  const handlePurchase = async () => {
  setIsProcessing(true);
  
  try {
    await purchasesApi.create({
      tourId: tour?.id,
      units: units
    });
    
    toast.success(`Заказ оформлен! Сумма: ${totalPrice} ₽`);
    navigate('/my-purchases');
  } catch (error) {
    console.error('Ошибка при покупке:', error);
    toast.error('Ошибка при оформлении заказа');
  } finally {
    setIsProcessing(false);
  }
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
    <div className="page-container">
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
    </div>
  );
};