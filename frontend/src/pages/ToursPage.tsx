import React, { useState, useEffect } from 'react';
import { TourCard } from '../components/Tours/TourCard';
import { Tour, TourType } from '../types';
import './ToursPage.css';
import { HotToursBanner } from '../components/HotTours/HotToursBanner';
import { toursApi } from '../services/api';

const ToursPage: React.FC = () => {
  const [tours, setTours] = useState<Tour[]>([]);
  const [filteredTours, setFilteredTours] = useState<Tour[]>([]);
  const [loading, setLoading] = useState(true);
  
  // Состояния для фильтров
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedType, setSelectedType] = useState<TourType | 'ALL'>('ALL');
  const [priceRange, setPriceRange] = useState<[number, number]>([0, 100000]);
  const [sortBy, setSortBy] = useState<'price' | 'rating' | 'none'>('none');
  const [showHotOnly, setShowHotOnly] = useState(false);

  useEffect(() => {
  const fetchTours = async () => {
    setLoading(true);
    try {
      const response = await toursApi.getPublic();
      setTours(response.data);
      setFilteredTours(response.data);
    } catch (error) {
      console.error('Ошибка загрузки туров:', error);
    } finally {
      setLoading(false);
    }
  };
  fetchTours();
}, []);

  // Применяем фильтры
  useEffect(() => {
    let result = [...tours];

    // Поиск по названию
    if (searchQuery) {
      result = result.filter(tour => 
        tour.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        tour.destination.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    // Фильтр по типу
    if (selectedType !== 'ALL') {
      result = result.filter(tour => tour.type === selectedType);
    }

    // Фильтр по цене
    result = result.filter(tour => 
      tour.price >= priceRange[0] && tour.price <= priceRange[1]
    );

    // Фильтр "горячие туры"
    if (showHotOnly) {
      result = result.filter(tour => tour.hot);
    }

    // Сортировка
    if (sortBy === 'price') {
      result.sort((a, b) => a.price - b.price);
    } else if (sortBy === 'rating') {
      result.sort((a, b) => b.avgRating - a.avgRating);
    }

    setFilteredTours(result);
  }, [tours, searchQuery, selectedType, priceRange, showHotOnly, sortBy]);

  if (loading) {
    return <div className="loading">Загрузка туров...</div>;
  }

  return (
    <div className="page-container">
    <div className="tours-page">
  <div className="tours-header">
    <h1>Наши туры</h1>
    <p>Выберите идеальное путешествие</p>
  </div>
  
  <HotToursBanner /> 

      {/* Панель фильтров */}
      <div className="filters-panel">
        <div className="search-box">
          <input
            type="text"
            placeholder="Поиск по названию или направлению..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="search-input"
          />
        </div>

        <div className="filters-row">
          <div className="filter-group">
            <label>Тип тура:</label>
            <select 
              value={selectedType} 
              onChange={(e) => setSelectedType(e.target.value as TourType | 'ALL')}
              className="filter-select"
            >
              <option value="ALL">Все типы</option>
              <option value={TourType.RECREATION}>Отдых</option>
              <option value={TourType.EXCURSION}>Экскурсии</option>
              <option value={TourType.SHOPPING}>Шоппинг</option>
            </select>
          </div>

          <div className="filter-group">
            <label>Цена:</label>
            <div className="price-inputs">
              <input
                type="number"
                placeholder="От"
                value={priceRange[0] === 0 ? '' : priceRange[0]}
                onChange={(e) => {
                    const value = e.target.value === '' ? 0 : Number(e.target.value);
                    setPriceRange([value, priceRange[1]]);
                }}
                className="price-input"
                min="0"
                />
              <span>-</span>
              <input
                type="number"
                placeholder="До"
                value={priceRange[1] === 100000 ? '' : priceRange[1]}
                onChange={(e) => {
                    const value = e.target.value === '' ? 100000 : Number(e.target.value);
                    setPriceRange([priceRange[0], value]);
                }}
                className="price-input"
                min="0"
                />
            </div>
          </div>

          <div className="filter-group">
            <label>Сортировка:</label>
            <select 
              value={sortBy} 
              onChange={(e) => setSortBy(e.target.value as 'price' | 'rating' | 'none')}
              className="filter-select"
            >
              <option value="none">Без сортировки</option>
              <option value="price">По цене (возрастание)</option>
              <option value="rating">По рейтингу (убывание)</option>
            </select>
          </div>

          <div className="filter-group checkbox">
            <label>
              <input
                type="checkbox"
                checked={showHotOnly}
                onChange={(e) => setShowHotOnly(e.target.checked)}
              />
              Только горячие туры
            </label>
          </div>
        </div>
      </div>

      {/* Результаты */}
      <div className="results-info">
        Найдено туров: {filteredTours.length}
      </div>

      {filteredTours.length === 0 ? (
        <div className="no-results">
          <p>По вашему запросу ничего не найдено</p>
          <button 
            onClick={() => {
              setSearchQuery('');
              setSelectedType('ALL');
              setPriceRange([0, 100000]);
              setShowHotOnly(false);
              setSortBy('none');
            }}
            className="reset-btn"
          >
            Сбросить фильтры
          </button>
        </div>
      ) : (
        <div className="tours-grid">
          {filteredTours.map(tour => (
            <TourCard key={tour.id} tour={tour} />
          ))}
        </div>
      )}
    </div>
    </div>
  );
};

export default ToursPage;