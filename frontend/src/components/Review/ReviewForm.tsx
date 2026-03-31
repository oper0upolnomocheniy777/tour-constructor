import React, { useState } from 'react';
import './ReviewForm.css';

interface ReviewFormProps {
  tourId: number;
  onSubmit: (rating: number, text: string) => void;
  onCancel: () => void;
}

export const ReviewForm: React.FC<ReviewFormProps> = ({ onSubmit, onCancel }) => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [text, setText] = useState('');
  const [errors, setErrors] = useState<{ rating?: string; text?: string }>({});

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    
    const newErrors: { rating?: string; text?: string } = {};
    if (rating === 0) newErrors.rating = 'Выберите оценку';
    if (!text.trim()) newErrors.text = 'Напишите отзыв';
    if (text.length < 10) newErrors.text = 'Отзыв должен содержать минимум 10 символов';
    
    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }
    
    onSubmit(rating, text);
  };

  return (
    <form className="review-form" onSubmit={handleSubmit}>
      <h3>Оставить отзыв</h3>
      
      <div className="rating-input">
        <label>Ваша оценка:</label>
        <div className="stars-input">
          {[1, 2, 3, 4, 5].map((star) => (
            <span
              key={star}
              className={`star ${star <= (hoverRating || rating) ? 'selected' : ''}`}
              onClick={() => setRating(star)}
              onMouseEnter={() => setHoverRating(star)}
              onMouseLeave={() => setHoverRating(0)}
            >
              ★
            </span>
          ))}
        </div>
        {errors.rating && <span className="error">{errors.rating}</span>}
      </div>
      
      <div className="text-input">
        <label>Ваш отзыв:</label>
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="Расскажите о впечатлениях от тура..."
          rows={4}
        />
        {errors.text && <span className="error">{errors.text}</span>}
      </div>
      
      <div className="form-actions">
        <button type="button" className="cancel-btn" onClick={onCancel}>
          Отмена
        </button>
        <button type="submit" className="submit-btn">
          Отправить
        </button>
      </div>
    </form>
  );
};