import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './MyPurchasesPage.css';
import { toast } from 'sonner';
import { purchasesApi } from '../services/api';

interface Purchase {
  id: number;
  tourId: number;
  tourTitle: string;
  units: number;
  price: number;
  date: string;
  status: 'pending' | 'confirmed' | 'cancelled';
}

export const MyPurchasesPage: React.FC = () => {
  const [purchases, setPurchases] = useState<Purchase[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchPurchases = async () => {
      setLoading(true);
      try {
        const response = await purchasesApi.getMy();
        setPurchases(response.data);
      } catch (error) {
        console.error('Ошибка загрузки покупок:', error);
        toast.error('Не удалось загрузить покупки');
      } finally {
        setLoading(false);
      }
    };
    fetchPurchases();
  }, []);

  const formatDate = (dateString: string) => {
  if (!dateString) return 'Дата неизвестна';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return 'Дата неизвестна';
  return date.toLocaleDateString('ru-RU', {
    day: 'numeric',
    month: 'long',
    year: 'numeric'
  });
};

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'pending': return 'Ожидает подтверждения';
      case 'confirmed': return 'Подтвержден';
      case 'cancelled': return 'Отменен';
      default: return status;
    }
  };

  const getStatusClass = (status: string) => {
    switch (status) {
      case 'pending': return 'status-pending';
      case 'confirmed': return 'status-confirmed';
      case 'cancelled': return 'status-cancelled';
      default: return '';
    }
  };

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  return (
    <div className="page-container">
    <div className="my-purchases-page">
      <h1>Мои покупки</h1>

      {purchases.length === 0 ? (
        <div className="empty-state">
          <p>У вас пока нет покупок</p>
          <Link to="/tours" className="browse-btn">Посмотреть туры</Link>
        </div>
      ) : (
        <div className="purchases-list">
          {purchases.map(purchase => (
            <div key={purchase.id} className="purchase-card">
              <div className="purchase-header">
                <h3>
                  <Link to={`/tour/${purchase.tourId}`}>{purchase.tourTitle}</Link>
                </h3>
                <span className={`status-badge ${getStatusClass(purchase.status)}`}>
                  {getStatusLabel(purchase.status)}
                </span>
              </div>

              <div className="purchase-details">
                <div className="detail">
                  <span>Количество:</span>
                  <strong>{purchase.units} шт.</strong>
                </div>
                <div className="detail">
                  <span>Сумма:</span>
                  <strong>{purchase.price} ₽</strong>
                </div>
              </div>

              <div className="purchase-actions">
                <Link to={`/tour/${purchase.tourId}`} className="btn-view">
                  Подробнее о туре
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
    </div>
  );
};