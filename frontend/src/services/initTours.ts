import { Tour, TourType } from '../types';
import { saveUserTour } from './tourStorage';

const MOCK_TOURS: Omit<Tour, 'id' | 'avgRating' | 'votesCount' | 'enabled'>[] = [
  {
    title: 'Путешествие в Париж',
    description: 'Посетите город любви и романтики. Эйфелева башня, Лувр, Монмартр и многое другое.',
    destination: 'Франция, Париж',
    type: TourType.EXCURSION,
    hot: true,
    price: 45000,
    discount: 10,
    hotUntil: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000).toISOString(),
    imageUrl: '',
    route: { points: [] }
  },
  {
    title: 'Отдых в Сочи',
    description: 'Черное море, горы, парки и развлечения для всей семьи.',
    destination: 'Россия, Сочи',
    type: TourType.RECREATION,
    hot: false,
    price: 35000,
    discount: 0,
    imageUrl: '',
    route: { points: [] }
  },
  {
    title: 'Шоппинг в Милане',
    description: 'Лучшие бренды, аутлеты и итальянская кухня.',
    destination: 'Италия, Милан',
    type: TourType.SHOPPING,
    hot: false,
    price: 55000,
    discount: 5,
    imageUrl: '',
    route: { points: [] }
  }
];

export const initTours = () => {
  const existingTours = JSON.parse(localStorage.getItem('user_tours') || '[]');
  
  // Если туров ещё нет, добавляем мок-данные
  if (existingTours.length === 0) {
    MOCK_TOURS.forEach(tour => {
      saveUserTour(tour);
    });
    console.log('✅ Мок-туры добавлены в localStorage');
  }
};