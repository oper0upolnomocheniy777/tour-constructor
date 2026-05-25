import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Tour } from '../types';
import './AdminPanel.css';
import { toast } from 'sonner';
import { toursApi, purchasesApi } from '../services/api';
import api from '../services/api';

export const AdminPanel: React.FC = () => {
  const navigate = useNavigate();
  const [tours, setTours] = useState<Tour[]>([]);
  const [purchases, setPurchases] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState<'tours' | 'purchases'>('tours');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
  const loadAllData = async () => {
    setLoading(true);
    try {
      const toursRes = await toursApi.getAll();
      setTours(toursRes.data);
      
      const purchasesRes = await purchasesApi.getAll();  // ← изменил getMy на getAll
      setPurchases(purchasesRes.data);
    } catch (error) {
      console.error('Ошибка загрузки:', error);
    } finally {
      setLoading(false);
    }
  };
  
  loadAllData();
}, []);

  const loadData = async () => {
  try {
    const response = await toursApi.getAll();
    setTours(response.data);
  } catch (error) {
    console.error('Ошибка загрузки туров:', error);
  }
};

const [purchasesLoading, setPurchasesLoading] = useState(false);

const loadPurchases = async () => {
  try {
    const response = await purchasesApi.getAll();
    setPurchases(response.data || []);
  } catch (error) {
    console.error('Ошибка загрузки заказов:', error);
    setPurchases([]);
  }
};

useEffect(() => {
  loadData();
  loadPurchases();
}, []);

  const handleDeleteTour = async (id: number) => {
  if (window.confirm('Удалить этот тур?')) {
    try {
      await toursApi.delete(id);
      loadData();
      toast.success('Тур удален');
    } catch (error) {
      console.error('Ошибка удаления:', error);
      toast.error('Ошибка при удалении тура');
    }
  }
};

  const handleUpdatePurchaseStatus = async (purchaseId: number, newStatus: string) => {
  try {
    await purchasesApi.updateStatus(purchaseId, newStatus);
    toast.success(`Заказ ${newStatus === 'confirmed' ? 'подтвержден' : 'отменен'}`);
    loadPurchases(); 
  } catch (error) {
    console.error('Ошибка обновления статуса:', error);
    toast.error('Ошибка при обновлении статуса');
  }
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

  const handleToggleHot = async (tourId: number, isHot: boolean) => {
  try {
    const tourResponse = await toursApi.getById(tourId);
    const tour = tourResponse.data;
    
    await toursApi.update(tourId, {
      ...tour,
      hot: isHot
    });
    
    toast.success(isHot ? 'Тур помечен как горячий' : 'Горячий статус снят');
    loadData();
  } catch (error) {
    console.error('Ошибка:', error);
    toast.error('Ошибка при обновлении статуса');
  }
};

const formatDate = (dateString: string) => {
  if (!dateString) return 'Дата неизвестна';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return 'Дата неизвестна';
  return date.toLocaleDateString('ru-RU');
};

  if (loading) {
    return <div className="loading">Загрузка...</div>;
  }

  return (
    <div className="page-container">
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
  Заказы ({purchases?.length ?? 0})
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
    {loading ? (
      <div>Загрузка заказов...</div>
    ) : purchases.length === 0 ? (
      <div>Нет заказов</div>
    ) : (
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
                <td>{purchase.tourTitle}</td>
                <td>{purchase.units} шт.</td>
                <td>{purchase.price} ₽</td>
                <td>{new Date(purchase.createdAt).toLocaleDateString()}</td>
                <td>
                  <span className={`status-badge ${getStatusClass(purchase.status)}`}>
                    {getStatusLabel(purchase.status)}
                  </span>
                </td>
                <td className="actions">
                  {purchase.status === 'pending' && (
                    <>
                      <button onClick={() => handleUpdatePurchaseStatus(purchase.id, 'confirmed')}>
                        ✓
                      </button>
                      <button onClick={() => handleUpdatePurchaseStatus(purchase.id, 'cancelled')}>
                        ✗
                      </button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    )}
  </div>
)}
    </div>
    </div>
  );
};