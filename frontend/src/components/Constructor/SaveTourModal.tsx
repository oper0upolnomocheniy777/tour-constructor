import React, { useState, useEffect } from 'react';
import { TourType } from '../../types';
import './SaveTourModal.css';

interface SaveTourModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSave: (tourData: {
    title: string;
    description: string;
    destination: string;
    price: number;
    type: TourType;
  }) => void;
  initialData?: {
    title: string;
    description: string;
    destination: string;
    price: number;
    type: TourType;
  } | null;
}

export const SaveTourModal: React.FC<SaveTourModalProps> = ({
  isOpen,
  onClose,
  onSave,
  initialData
}) => {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [destination, setDestination] = useState('');
  const [price, setPrice] = useState('');
  const [type, setType] = useState<TourType>(TourType.RECREATION);
  const [errors, setErrors] = useState<Record<string, string>>({});

  // Загружаем начальные данные при открытии
  useEffect(() => {
  console.log('Modal opened, initialData:', initialData);
  if (initialData) {
    setTitle(initialData.title);
    setDescription(initialData.description);
    setDestination(initialData.destination);
    setPrice(initialData.price.toString());
    setType(initialData.type);
  } else {
    setTitle('');
    setDescription('');
    setDestination('');
    setPrice('');
    setType(TourType.RECREATION);
  }
}, [initialData, isOpen]); 

  const validate = () => {
    const newErrors: Record<string, string> = {};
    
    if (!title.trim()) newErrors.title = 'Введите название тура';
    if (!destination.trim()) newErrors.destination = 'Введите направление';
    if (!description.trim()) newErrors.description = 'Введите описание';
    if (!price.trim()) newErrors.price = 'Введите цену';
    else if (isNaN(Number(price)) || Number(price) <= 0) {
      newErrors.price = 'Цена должна быть положительным числом';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

 const handleSubmit = (e: React.FormEvent) => {
  e.preventDefault();
  if (!validate()) return;
  
  onSave({
    title: title.trim(),
    description: description.trim(),
    destination: destination.trim(),
    price: Number(price),
    type
  });
  
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="save-tour-modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Сохранить тур</h2>
          <button className="close-btn" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Название тура *</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="Например: Путешествие в Париж"
              className={errors.title ? 'error' : ''}
            />
            {errors.title && <span className="error-text">{errors.title}</span>}
          </div>

          <div className="form-group">
            <label>Направление *</label>
            <input
              type="text"
              value={destination}
              onChange={(e) => setDestination(e.target.value)}
              placeholder="Например: Франция, Париж"
              className={errors.destination ? 'error' : ''}
            />
            {errors.destination && <span className="error-text">{errors.destination}</span>}
          </div>

          <div className="form-group">
            <label>Тип тура</label>
            <select value={type} onChange={(e) => setType(e.target.value as TourType)}>
              <option value={TourType.RECREATION}>Отдых</option>
              <option value={TourType.EXCURSION}>Экскурсии</option>
              <option value={TourType.SHOPPING}>Шоппинг</option>
            </select>
          </div>

          <div className="form-group">
            <label>Цена (₽) *</label>
            <input
              type="number"
              value={price}
              onChange={(e) => setPrice(e.target.value)}
              placeholder="Например: 45000"
              className={errors.price ? 'error' : ''}
              min="0"
              step="1000"
            />
            {errors.price && <span className="error-text">{errors.price}</span>}
          </div>

          <div className="form-group">
            <label>Описание *</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Расскажите о туре..."
              rows={4}
              className={errors.description ? 'error' : ''}
            />
            {errors.description && <span className="error-text">{errors.description}</span>}
          </div>

          <div className="modal-footer">
            <button type="button" className="btn-cancel" onClick={onClose}>
              Отмена
            </button>
            <button type="submit" className="btn-save">
              Сохранить тур
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};