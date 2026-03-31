import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { TourConstructorYandex } from '../components/Constructor/TourConstructorYandex';
import { getUserTour } from '../services/tourStorage';
import { Tour } from '../types';

export const EditTourPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [tour, setTour] = useState<Tour | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  useEffect(() => {
    if (id) {
      const foundTour = getUserTour(parseInt(id));
      if (foundTour) {
        setTour(foundTour);
      } else {
        setError(true);
      }
    }
    setLoading(false);
  }, [id]);

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  if (error || !tour) {
    return (
      <div className="error-page">
        <h2>Тур не найден</h2>
        <button onClick={() => navigate('/my-tours')}>Вернуться к моим турам</button>
      </div>
    );
  }

  const YANDEX_MAPS_API_KEY = process.env.REACT_APP_YANDEX_MAPS_API_KEY || '';

  return (
    <div className="edit-tour-page">
      <div className="edit-header">
        <button onClick={() => navigate('/my-tours')} className="back-btn">
          ← Назад
        </button>
        <h1>Редактирование: {tour.title}</h1>
      </div>
      <TourConstructorYandex
        apiKey={YANDEX_MAPS_API_KEY}
        initialRoute={tour.route}
        tourId={tour.id}
      />
    </div>
  );
};