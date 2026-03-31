import React, { useState, useCallback, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { YandexMapComponent, RoutePoint } from '../Map/YandexMap';
import { RoutePointList, RoutePointItem } from './RoutePointList';
import { SaveTourModal } from './SaveTourModal';
import { TourRoute, TourType } from '../../types';
import { saveUserTour, updateUserTour, SaveTourData, getUserTour } from '../../services/tourStorage';
import './TourConstructor.css';

interface TourConstructorYandexProps {
  initialRoute?: TourRoute;
  tourId?: number;
  apiKey: string;
}

export const TourConstructorYandex: React.FC<TourConstructorYandexProps> = ({
  initialRoute,
  tourId,
  apiKey
}) => {
 const [points, setPoints] = useState<RoutePoint[]>(() => {
    if (initialRoute?.points) {
      return initialRoute.points.map(p => ({
        id: p.id,
        name: p.name,
        position: [p.longitude, p.latitude] as [number, number],
        order: p.order,
        description: p.description
      }));
    }
    return [];
  });
  
  const [routeInfo, setRouteInfo] = useState<{ distance: string; duration: string } | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
const [editingTour, setEditingTour] = useState<{
  title: string;
  description: string;
  destination: string;
  price: number;
  type: TourType;
} | null>(null);

// Загружаем данные тура при редактировании (НЕ открываем модалку)
useEffect(() => {
  if (tourId) {
    const tour = getUserTour(tourId);
    if (tour) {
      setEditingTour({
        title: tour.title,
        description: tour.description,
        destination: tour.destination,
        price: tour.price,
        type: tour.type
      });
      // НЕ открываем модалку здесь!
    }
  }
}, [tourId]);

// Функция открытия модального окна (вызывается по кнопке)
const openSaveModal = () => {
  console.log('Opening modal, editingTour:', editingTour);
  setIsModalOpen(true);
};

  const [pointForm, setPointForm] = useState<{
    open: boolean;
    point?: RoutePoint;
    name: string;
    description: string;
  }>({
    open: false,
    name: '',
    description: ''
  });

 const handleMapClick = useCallback((position: [number, number]) => {
  setPoints(prev => {
    const newPoint: RoutePoint = {
      id: uuidv4(),
      name: `Точка ${prev.length + 1}`,
      position: position,
      order: prev.length,
      description: '',
    };
    return [...prev, newPoint];
  });
}, []);

  const handleMarkerDragEnd = useCallback((id: string, position: [number, number]) => {
    setPoints(prev =>
      prev.map(point =>
        point.id === id
          ? { ...point, position }
          : point
      )
    );
  }, []);

  const handleRemovePoint = useCallback((id: string) => {
    setPoints(prev => {
      const filtered = prev.filter(p => p.id !== id);
      return filtered.map((point, idx) => ({
        ...point,
        order: idx
      }));
    });
  }, []);

  const handleEditPoint = useCallback((point: RoutePoint) => {
    setPointForm({
      open: true,
      point,
      name: point.name,
      description: point.description || ''
    });
  }, []);

  const handleSavePoint = useCallback(() => {
    if (pointForm.point) {
      setPoints(prev =>
        prev.map(p =>
          p.id === pointForm.point?.id
            ? { ...p, name: pointForm.name, description: pointForm.description }
            : p
        )
      );
    }
    setPointForm({ open: false, name: '', description: '' });
  }, [pointForm]);

  const handleReorder = useCallback((reordered: RoutePointItem[]) => {
    const yandexPoints: RoutePoint[] = reordered.map(p => ({
      id: p.id,
      name: p.name,
      description: p.description,
      position: [p.longitude, p.latitude] as [number, number],
      order: p.order
    }));
    setPoints(yandexPoints);
  }, []);

  const handleRouteCalculated = useCallback((distance: string, duration: string) => {
    setRouteInfo({ distance, duration });
  }, []);

const handleSaveTour = (tourData: {
  title: string;
  description: string;
  destination: string;
  price: number;
  type: TourType;
}) => {
  // Преобразуем точки маршрута в формат для сохранения
  const routePoints = points.map(p => ({
    id: p.id,
    name: p.name,
    description: p.description,
    latitude: p.position[1],
    longitude: p.position[0],
    order: p.order
  }));

  const route: TourRoute = {
    points: routePoints,
    totalDistance: routeInfo ? parseFloat(routeInfo.distance) : undefined,
    totalDuration: routeInfo?.duration
  };

  if (tourId) {
    // Редактируем существующий тур
    const updated = updateUserTour(tourId, {
      title: tourData.title,
      description: tourData.description,
      destination: tourData.destination,
      price: tourData.price,
      type: tourData.type,
      route: route
    });
    
    if (updated) {
      alert(`Тур "${updated.title}" успешно обновлен!`);
    } else {
      alert('Ошибка при обновлении тура');
    }
  } else {
    // Создаем новый тур
    const saveData: SaveTourData = {
      title: tourData.title,
      description: tourData.description,
      destination: tourData.destination,
      price: tourData.price,
      type: tourData.type,
      route: route
    };

    const newTour = saveUserTour(saveData);
    alert(`Тур "${newTour.title}" успешно сохранен!`);
  }
  
  setIsModalOpen(false); // Это должно закрыть модальное окно
  setEditingTour(null);   // Сбрасываем редактируемый тур
};

  // Преобразуем YandexRoutePoint в RoutePointItem для списка
  const pointItems: RoutePointItem[] = points.map(p => ({
    id: p.id,
    name: p.name,
    description: p.description,
    latitude: p.position[1],
    longitude: p.position[0],
    order: p.order
  }));

  return (
    <div className="tour-constructor">
      <div className="constructor-header">
        <h2>Конструктор маршрута</h2>
        <div className="header-actions">
          {routeInfo && (
            <div className="route-info">
              <span>📏 {routeInfo.distance}</span>
              <span>⏱️ {routeInfo.duration}</span>
            </div>
          )}
          <button 
  className="btn-save-tour"
  onClick={openSaveModal}  
  disabled={points.length < 2}
>
  💾 Сохранить тур
</button>
        </div>
      </div>

      <div className="constructor-layout">
        <div className="map-section">
          <YandexMapComponent
            apiKey={apiKey}
            onMapClick={handleMapClick}
            markers={points}
            onMarkerDragEnd={handleMarkerDragEnd}
            showRoute={points.length >= 2}
            onRouteCalculated={handleRouteCalculated}
          />
        </div>

        <div className="points-section">
          <div className="points-header">
            <h3>Точки маршрута ({points.length})</h3>
            <p className="points-hint">
              {points.length < 2 ? 'Добавьте минимум 2 точки для построения маршрута' : ''}
            </p>
          </div>

          <RoutePointList
            points={pointItems}
            onReorder={handleReorder}
            onRemove={handleRemovePoint}
            onEdit={(point) => {
              const yandexPoint = points.find(p => p.id === point.id);
              if (yandexPoint) {
                handleEditPoint(yandexPoint);
              }
            }}
          />
        </div>
      </div>

      {/* Модальное окно редактирования точки */}
      {pointForm.open && (
        <div className="modal-overlay" onClick={() => setPointForm({ open: false, name: '', description: '' })}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <h3>Редактировать точку</h3>
            
            <div className="form-group">
              <label>Название</label>
              <input
                type="text"
                value={pointForm.name}
                onChange={(e) => setPointForm(prev => ({ ...prev, name: e.target.value }))}
                placeholder="Введите название точки"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label>Описание</label>
              <textarea
                value={pointForm.description}
                onChange={(e) => setPointForm(prev => ({ ...prev, description: e.target.value }))}
                placeholder="Введите описание (необязательно)"
                rows={3}
              />
            </div>

            <div className="modal-actions">
              <button className="btn-secondary" onClick={() => setPointForm({ open: false, name: '', description: '' })}>
                Отмена
              </button>
              <button className="btn-primary" onClick={handleSavePoint}>
                Сохранить
              </button>
            </div>
          </div>
        </div>
      )}

     {/* Модальное окно сохранения тура */}
{isModalOpen && (
  <SaveTourModal
    isOpen={isModalOpen}
    onClose={() => {
      console.log('Closing modal');
      setIsModalOpen(false);
    }}
    onSave={handleSaveTour}
    initialData={editingTour}
  />
)}
    </div>
  );

  console.log('TourConstructor render, tourId:', tourId, 'editingTour:', editingTour, 'isModalOpen:', isModalOpen);
};