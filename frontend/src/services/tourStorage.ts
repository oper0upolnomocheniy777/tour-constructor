import { Tour, TourType, TourRoute } from '../types';

// Ключ для localStorage
const STORAGE_KEY = 'user_tours';

// Получить все туры пользователя
export const getUserTours = (): Tour[] => {
  const stored = localStorage.getItem(STORAGE_KEY);
  if (!stored) return [];
  try {
    return JSON.parse(stored);
  } catch {
    return [];
  }
};

export interface SaveTourData {
  title: string;
  description: string;
  destination: string;
  price: number;
  type: TourType;
  route?: TourRoute;
}

// Сохранить тур
export const saveUserTour = (data: SaveTourData): Tour => {
  const tours = getUserTours();
  const newTour: Tour = {
    id: Date.now(),
    title: data.title,
    description: data.description,
    destination: data.destination,
    price: data.price,
    type: data.type,
    route: data.route,
    avgRating: 0,
    votesCount: 0,
    enabled: true,
    hot: false,
    discount: 0
  };
  
  tours.push(newTour);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(tours));
  return newTour;
};

// Обновить тур 
export const updateUserTour = (id: number, updates: Partial<Tour>): Tour | null => {
  const tours = getUserTours();
  const index = tours.findIndex(t => t.id === id);
  if (index === -1) return null;
  
  tours[index] = { ...tours[index], ...updates };
  localStorage.setItem(STORAGE_KEY, JSON.stringify(tours));
  return tours[index];
};

// Удалить тур
export const deleteUserTour = (id: number): boolean => {
  const tours = getUserTours();
  const filtered = tours.filter(t => t.id !== id);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(filtered));
  return true;
};

// Получить конкретный тур
export const getUserTour = (id: number): Tour | undefined => {
  const tours = getUserTours();
  return tours.find(t => t.id === id);
};
