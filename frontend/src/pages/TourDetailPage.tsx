import React, { useState, useEffect } from 'react';
import { Link, useParams, useNavigate } from 'react-router-dom';
import { Tour, TourType } from '../types';
import { getUserTour } from '../services/tourStorage';
import { YandexMapComponent } from '../components/Map/YandexMap';
import './TourDetailPage.css';


export const TourDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [showFullDescription, setShowFullDescription] = useState(false);

  useEffect(() => {
    if (id) {
      const foundTour = getUserTour(parseInt(id));
      if (foundTour) {
        setTour(foundTour);
      }
    }
    setLoading(false);
  }, [id]);

  const getTypeLabel = (type: TourType): string => {
    switch (type) {
      case TourType.RECREATION: return 'Отдых';
      case TourType.EXCURSION: return 'Экскурсии';
      case TourType.SHOPPING: return 'Шоппинг';
      default: return 'Неизвестно';
    }
  };

  const getTypeClass = (type: TourType): string => {
    switch (type) {
      case TourType.RECREATION: return 'recreation';
      case TourType.EXCURSION: return 'excursion';
      case TourType.SHOPPING: return 'shopping';
      default: return '';
    }
  };

  // Преобразуем точки маршрута в формат для карты
  const mapMarkers = tour?.route?.points.map(point => ({
    id: point.id,
    name: point.name,
    position: [point.longitude, point.latitude] as [number, number],
    order: point.order,
    description: point.description
  })) || [];

  const discountedPrice = tour?.discount
    ? tour.price * (1 - tour.discount / 100)
    : tour?.price;

  const YANDEX_MAPS_API_KEY = process.env.REACT_APP_YANDEX_MAPS_API_KEY || '';

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  if (!tour) {
    return (
      <div className="error-page">
        <h2>Тур не найден</h2>
        <button onClick={() => navigate('/my-tours')}>Вернуться к моим турам</button>
      </div>
    );
  }

  return (
    <div className="tour-detail-page">
      <button onClick={() => navigate(-1)} className="back-btn">
        ← Назад
      </button>

      <div className="tour-detail-header">
        <div className="tour-title-section">
          <h1>{tour.title}</h1>
          <div className="tour-badges">
            {tour.hot && <span className="hot-badge">Горячий тур</span>}
            {tour.discount > 0 && (
              <span className="discount-badge">-{tour.discount}%</span>
            )}
          </div>
        </div>

        <div className="tour-meta">
          <span className="tour-destination">📍 {tour.destination}</span>
          <span className={`tour-type ${getTypeClass(tour.type)}`}>
            {getTypeLabel(tour.type)}
          </span>
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
        </div>
      </div>

      {/* Карта маршрута */}
      {mapMarkers.length > 0 && (
        <div className="route-map">
          <h2>Маршрут</h2>
          <YandexMapComponent
            apiKey={YANDEX_MAPS_API_KEY}
            markers={mapMarkers}
            showRoute={mapMarkers.length >= 2}
            onRouteCalculated={(distance, duration) => {
              console.log('Route:', distance, duration);
            }}
          />
          {tour.route?.totalDistance && (
            <div className="route-info">
              <span>📏 Общая протяженность: {tour.route.totalDistance} км</span>
              {tour.route.totalDuration && (
                <span>⏱️ Примерное время: {tour.route.totalDuration}</span>
              )}
              <span>📍 Количество точек: {mapMarkers.length}</span>
            </div>
          )}
        </div>
      )}

      {/* Описание */}
      <div className="tour-description-section">
        <h2>Описание</h2>
        <div className="description-content">
          {showFullDescription ? tour.description : tour.description.substring(0, 500)}
          {tour.description.length > 500 && (
            <button
              className="toggle-description"
              onClick={() => setShowFullDescription(!showFullDescription)}
            >
              {showFullDescription ? 'Свернуть' : 'Читать далее'}
            </button>
          )}
        </div>
      </div>

      {/* Точки маршрута */}
      {mapMarkers.length > 0 && (
        <div className="points-list-section">
          <h2>Точки маршрута</h2>
          <div className="points-list">
            {mapMarkers.map((point, index) => (
              <div key={point.id} className="point-item">
                <div className="point-number">{index + 1}</div>
                <div className="point-info">
                  <div className="point-name">{point.name}</div>
                  {point.description && (
                    <div className="point-description">{point.description}</div>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Цена и покупка */}
      <div className="tour-purchase-section">
        <div className="price-card">
          <div className="price">
            {tour.discount > 0 ? (
              <>
                <span className="old-price">{tour.price} ₽</span>
                <span className="new-price">{Math.round(discountedPrice!)} ₽</span>
              </>
            ) : (
              <span className="price">{tour.price} ₽</span>
            )}
          </div>
          <Link to={`/edit-tour/${tour.id}`} className="edit-btn">
            Редактировать тур
          </Link>
          <Link to={`/checkout/${tour.id}`} className="buy-btn">
            Купить тур
            </Link>
        </div>
      </div>
    </div>
  );
};