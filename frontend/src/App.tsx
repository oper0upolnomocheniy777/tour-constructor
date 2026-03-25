import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginForm } from './components/Auth/LoginForm';
import { RegisterForm } from './components/Auth/RegisterForm';
import { Navbar } from './components/Layout/Navbar';
import { TourConstructorYandex } from './components/Constructor/TourConstructorYandex';
import './App.css';
import { TourCard } from './components/Tours/TourCard';
import { Tour, TourType } from './types';
import './pages/ToursPage.css';
import ToursPage from './pages/ToursPage';
// Временно добавим мок-данные
const MOCK_TOURS: Tour[] = [
  {
    id: 1,
    title: 'Путешествие в Париж',
    description: 'Посетите город любви и романтики. Эйфелева башня, Лувр, Монмартр и многое другое.',
    destination: 'Франция, Париж',
    type: TourType.EXCURSION,
    hot: true,
    price: 45000,
    enabled: true,
    avgRating: 4.5,
    votesCount: 128,
    discount: 10,
    imageUrl: 'https://example.com/paris.jpg'
  },
  {
    id: 2,
    title: 'Отдых в Сочи',
    description: 'Черное море, горы, парки и развлечения для всей семьи.',
    destination: 'Россия, Сочи',
    type: TourType.RECREATION,
    hot: false,
    price: 35000,
    enabled: true,
    avgRating: 4.2,
    votesCount: 89,
    discount: 0,
    imageUrl: ''
  },
  {
    id: 3,
    title: 'Шоппинг в Милане',
    description: 'Лучшие бренды, аутлеты и итальянская кухня.',
    destination: 'Италия, Милан',
    type: TourType.SHOPPING,
    hot: false,
    price: 55000,
    enabled: true,
    avgRating: 4.7,
    votesCount: 56,
    discount: 5,
    imageUrl: ''
  }
];

const MyToursPage = () => <div className="page-container">Мои туры (в разработке)</div>;

const LoadingSpinner = () => (
  <div className="loading-container">
    <div className="spinner"></div>
    <p>Загрузка...</p>
  </div>
);

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();
  
  if (loading) return <LoadingSpinner />;
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" />;
};

function AppContent() {
  const { loading } = useAuth();
  if (loading) return <LoadingSpinner />;

  const YANDEX_MAPS_API_KEY = process.env.REACT_APP_YANDEX_MAPS_API_KEY || '';

  return (
    <>
      <Navbar />
      <div className="container">
        <Routes>
          <Route path="/login" element={<LoginForm />} />
          <Route path="/register" element={<RegisterForm />} />
          <Route path="/tours" element={<ToursPage />} />
          <Route path="/my-tours" element={<PrivateRoute><MyToursPage /></PrivateRoute>} />
          <Route path="/constructor" element={
            <PrivateRoute>
              <TourConstructorYandex apiKey={YANDEX_MAPS_API_KEY}/>
            </PrivateRoute>
          } />
          <Route path="/constructor/:id" element={
            <PrivateRoute>
              <TourConstructorYandex apiKey={YANDEX_MAPS_API_KEY} />
            </PrivateRoute>
          } />
          <Route path="/" element={<Navigate to="/tours" />} />
          <Route path="*" element={<Navigate to="/tours" />} />
        </Routes>
      </div>
    </>
  );
}

function App() {
  return (
    <Router future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  );
}

export default App;