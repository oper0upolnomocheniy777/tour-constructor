import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tour } from '../types';
import { getUserTours, deleteUserTour } from '../services/tourStorage';
import './AdminPanel.css';

export const AdminPanel: React.FC = () => {
  const navigate = useNavigate();
  const [tours, setTours] = useState<Tour[]>([]);
  const [purchases, setPurchases] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState<'tours' | 'purchases'>('tours');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = () => {
    const allTours = getUserTours();
    setTours(allTours);
    
    const allPurchases = JSON.parse(localStorage.getItem('purchases') || '[]');
    setPurchases(allPurchases);
    
    setLoading(false);
  };

  const handleDeleteTour = (id: number) => {
    if (window.confirm('Удалить этот тур?')) {
      deleteUserTour(id);
      loadData();
    }
  };

  const handleUpdatePurchaseStatus = (purchaseId: number, newStatus: string) => {
    const updatedPurchases = purchases.map(p => 
      p.id === purchaseId ? { ...p, status: newStatus } : p
    );
    localStorage.setItem('purchases', JSON.stringify(updatedPurchases));
    setPurchases(updatedPurchases);
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'pending': return 'Ожидает';
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

  const handleToggleHot = (tourId: number, isHot: boolean) => {
  const tours = JSON.parse(localStorage.getItem('user_tours') || '[]');
  const index = tours.findIndex((t: any) => t.id === tourId);
  
  if (index !== -1) {
    const hotUntil = isHot 
      ? new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString()
      : undefined;
    
    tours[index] = { 
      ...tours[index], 
      hot: isHot,
      hotUntil: hotUntil
    };
    
    localStorage.setItem('user_tours', JSON.stringify(tours));
    loadData();
  }
};

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  return (
    <div className="admin-panel">
      <h1>Админ-панель</h1>
      
      <div className="admin-tabs">
        <button 
          className={`tab ${activeTab === 'tours' ? 'active' : ''}`}
          onClick={() => setActiveTab('tours')}
        >
          Туры ({tours.length})
        </button>
        <button 
          className={`tab ${activeTab === 'purchases' ? 'active' : ''}`}
          onClick={() => setActiveTab('purchases')}
        >
          Заказы ({purchases.length})
        </button>
      </div>

      {activeTab === 'tours' && (
        <div className="admin-tours">
          <button 
            className="create-tour-btn"
            onClick={() => navigate('/constructor')}
          >
            + Создать новый тур
          </button>
          
          <div className="tours-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Горячий</th>
                  <th>Название</th>
                  <th>Направление</th>
                  <th>Цена</th>
                  <th>Рейтинг</th>
                  <th>Статус</th>
                  <th>Действия</th>
                </tr>
              </thead>
              <tbody>
                {tours.map(tour => (
                  <tr key={tour.id}>
                    <td>{tour.id}</td>
                    <td className="hot-toggle-cell">
  <label className="hot-toggle">
    <input
      type="checkbox"
      checked={tour.hot}
      onChange={() => handleToggleHot(tour.id, !tour.hot)}
    />
    <span className="slider"></span>
  </label>
</td>
                    <td>
                      <a href={`/tour/${tour.id}`} target="_blank" rel="noopener noreferrer">
                        {tour.title}
                      </a>
                    </td>
                    <td>{tour.destination}</td>
                    <td>{tour.price} ₽</td>
                    <td>
                      <span className="rating-badge">
                        {tour.avgRating.toFixed(1)} ★ ({tour.votesCount})
                      </span>
                    </td>
                    <td>
                      <span className={tour.enabled ? 'status-active' : 'status-inactive'}>
                        {tour.enabled ? 'Активен' : 'Отключен'}
                      </span>
                    </td>
                    <td className="actions">
                      <button 
                        className="btn-edit"
                        onClick={() => navigate(`/edit-tour/${tour.id}`)}
                      >
                        ✏️
                      </button>
                      <button 
                        className="btn-delete"
                        onClick={() => handleDeleteTour(tour.id)}
                      >
                        🗑️
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'purchases' && (
        <div className="admin-purchases">
          <div className="purchases-table">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Тур</th>
                  <th>Кол-во</th>
                  <th>Сумма</th>
                  <th>Дата</th>
                  <th>Статус</th>
                  <th>Действия</th>
                </tr>
              </thead>
              <tbody>
                {purchases.map(purchase => (
                  <tr key={purchase.id}>
                    <td>{purchase.id}</td>
                    <td>
                      <a href={`/tour/${purchase.tourId}`} target="_blank" rel="noopener noreferrer">
                        {purchase.tourTitle}
                      </a>
                    </td>
                    <td>{purchase.units} шт.</td>
                    <td>{purchase.price} ₽</td>
                    <td>{new Date(purchase.date).toLocaleDateString('ru-RU')}</td>
                    <td>
                      <span className={`status-badge ${getStatusClass(purchase.status)}`}>
                        {getStatusLabel(purchase.status)}
                      </span>
                    </td>
                    <td className="actions">
                      {purchase.status === 'pending' && (
                        <>
                          <button 
                            className="btn-confirm"
                            onClick={() => handleUpdatePurchaseStatus(purchase.id, 'confirmed')}
                          >
                            ✓ Подтвердить
                          </button>
                          <button 
                            className="btn-cancel-order"
                            onClick={() => handleUpdatePurchaseStatus(purchase.id, 'cancelled')}
                          >
                            ✗ Отменить
                          </button>
                        </>
                      )}
                      {purchase.status === 'confirmed' && (
                        <span className="completed-badge">Выполнен</span>
                      )}
                      {purchase.status === 'cancelled' && (
                        <span className="cancelled-badge">Отменен</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};