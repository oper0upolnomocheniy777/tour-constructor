import { Review, CreateReviewData } from '../types/review';
import api from './api';

const STORAGE_KEY = 'tour_reviews'; // оставим только для кэша (опционально)

export const getTourReviews = async (tourId: number): Promise<Review[]> => {
  try {
    const response = await api.get(`/reviews/tour/${tourId}`);
    return response.data || [];
  } catch (error) {
    console.error('Ошибка загрузки отзывов:', error);
    return [];
  }
};

export const canUserReview = async (tourId: number): Promise<boolean> => {
  try {
    const response = await api.get(`/purchases/check/${tourId}`);
    return response.data.canReview === true;
  } catch (error) {
    console.error('Ошибка проверки права на отзыв:', error);
    return false;
  }
};

export const addReview = async (data: CreateReviewData): Promise<Review> => {
  try {
    const response = await api.post('/reviews', {
      tourId: data.tourId,
      rating: data.rating,
      text: data.text
    });
    
    // Получаем полные данные отзыва (с именем пользователя, датой)
    const reviewsResponse = await api.get(`/reviews/tour/${data.tourId}`);
    const newReview = reviewsResponse.data.find((r: Review) => r.userId === response.data.userId);
    return newReview || response.data;
  } catch (error: any) {
    throw new Error(error.response?.data?.message || 'Ошибка при добавлении отзыва');
  }
};

export const deleteReview = async (reviewId: number, tourId: number): Promise<void> => {
  try {
    await api.delete(`/reviews/${reviewId}`);
  } catch (error) {
    console.error('Ошибка удаления отзыва:', error);
    throw error;
  }
};

// Вспомогательная функция для обновления рейтинга (бэкенд сам обновит)
export const updateTourRating = async (tourId: number): Promise<void> => {
  // Бэкенд обновляет рейтинг при добавлении/удалении отзыва
  // Эта функция не нужна, оставлена для совместимости
};