import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Tour, TourType } from '../types';
import { getUserTours, deleteUserTour } from '../services/tourStorage';
import './MyToursPage.css';

export const MyToursPage: React.FC = () => {
  const [tours, setTours] = useState<Tour[]>([]);
  const [loading, setLoading] = useState(true);
  const [deleteConfirm, setDeleteConfirm] = useState<number | null>(null);

  useEffect(() => {
    loadTours();
  }, []);

  const loadTours = () => {
    setLoading(true);
    const userTours = getUserTours();
    setTours(userTours);
    setLoading(false);
  };

  const handleDelete = (id: number) => {
    deleteUserTour(id);
    loadTours();
    setDeleteConfirm(null);
  };

  const getTypeLabel = (type: TourType): string => {
    switch (type) {
      case TourType.RECREATION: return 'Отдых';
      case TourType.EXCURSION: return 'Экскурсии';
      case TourType.SHOPPING: return 'Шоппинг';
      default: return 'Неизвестно';
    }
  };

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  return (
    <div className="my-tours-page">
      <div className="page-header">
        <h1>Мои туры</h1>
        <Link to="/constructor" className="create-btn">
          + Создать новый тур
        </Link>
      </div>

      {tours.length === 0 ? (
        <div className="empty-state">
          <p>У вас пока нет сохраненных туров</p>
          <Link to="/constructor" className="create-first-btn">
            Создать первый тур
          </Link>
        </div>
      ) : (
        <div className="tours-list">
          {tours.map(tour => (
            <div key={tour.id} className="tour-item">
              <div className="tour-info">
                <h3 className="tour-title">
                  <Link to={`/tour/${tour.id}`}>{tour.title}</Link>
                </h3>
                <div className="tour-meta">
                  <span className="tour-destination">📍 {tour.destination}</span>
                  <span className={`tour-type ${tour.type.toLowerCase()}`}>
                    {getTypeLabel(tour.type)}
                  </span>
                  {tour.route && tour.route.points.length > 0 && (
                    <span className="tour-points">
                      🗺️ {tour.route.points.length} точек
                    </span>
                  )}
                  {tour.route?.totalDistance && (
                    <span className="tour-distance">
                      📏 {tour.route.totalDistance} км
                    </span>
                  )}
                </div>
                <p className="tour-description">
                  {tour.description.length > 150
                    ? tour.description.substring(0, 150) + '...'
                    : tour.description}
                </p>
                <div className="tour-price">
                  {tour.discount > 0 ? (
                    <>
                      <span className="old-price">{tour.price} ₽</span>
                      <span className="new-price">
                        {Math.round(tour.price * (1 - tour.discount / 100))} ₽
                      </span>
                    </>
                  ) : (
                    <span className="price">{tour.price} ₽</span>
                  )}
                </div>
              </div>

              <div className="tour-actions">
                <Link to={`/edit-tour/${tour.id}`} className="btn-edit">
                  ✏️ Редактировать
                </Link>
                {deleteConfirm === tour.id ? (
                  <div className="delete-confirm">
                    <span>Удалить?</span>
                    <button onClick={() => handleDelete(tour.id)} className="btn-confirm">
                      Да
                    </button>
                    <button onClick={() => setDeleteConfirm(null)} className="btn-cancel">
                      Нет
                    </button>
                  </div>
                ) : (
                  <button onClick={() => setDeleteConfirm(tour.id)} className="btn-delete">
                    🗑️ Удалить
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};