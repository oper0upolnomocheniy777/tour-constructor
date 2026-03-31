import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import './MyPurchasesPage.css';

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
    const savedPurchases = JSON.parse(localStorage.getItem('purchases') || '[]');
    setPurchases(savedPurchases.sort((a: Purchase, b: Purchase) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    ));
    setLoading(false);
  }, []);

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
                <div className="detail">
                  <span>Дата:</span>
                  <strong>{new Date(purchase.date).toLocaleDateString('ru-RU')}</strong>
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
  );
};