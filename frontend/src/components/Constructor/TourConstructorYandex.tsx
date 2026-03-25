import React, { useState, useCallback } from 'react';
import { v4 as uuidv4 } from 'uuid';
import { YandexMapComponent, RoutePoint } from '../Map/YandexMap';
import { RoutePointList, RoutePointItem } from './RoutePointList';
import { SaveTourModal } from './SaveTourModal';
import { TourRoute, TourType } from '../../types';
import { saveUserTour, SaveTourData } from '../../services/tourStorage';
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
  const [points, setPoints] = useState<RoutePoint[]>(
    initialRoute?.points.map((p: any) => ({
      id: p.id,
      name: p.name,
      position: [p.longitude, p.latitude] as [number, number],
      order: p.order,
      description: p.description
    })) || []
  );
  
  const [routeInfo, setRouteInfo] = useState<{ distance: string; duration: string } | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
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

  const saveData: SaveTourData = {
    title: tourData.title,
    description: tourData.description,
    destination: tourData.destination,
    price: tourData.price,
    type: tourData.type,
    route: route
  };

  const newTour = saveUserTour(saveData);

  console.log('Тур сохранен:', newTour);
  alert(`Тур "${newTour.title}" успешно сохранен!`);
  setIsModalOpen(false);
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
            onClick={() => setIsModalOpen(true)}
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
      <SaveTourModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onSave={handleSaveTour}
      />
    </div>
  );
};