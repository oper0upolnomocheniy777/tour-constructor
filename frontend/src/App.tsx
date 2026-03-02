import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { LoginForm } from './components/Auth/LoginForm';
import { RegisterForm } from './components/Auth/RegisterForm';
import { Navbar } from './components/Layout/Navbar';
import { TourConstructorYandex } from './components/Constructor/TourConstructorYandex';
import './App.css';

const ToursPage = () => <div className="page-container">Список туров (в разработке)</div>;
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

  const handleSaveRoute = (route: any) => {
    console.log('Сохранение маршрута:', route);
    alert('Маршрут сохранен! (тестовый режим)');
  };

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
              <TourConstructorYandex apiKey={YANDEX_MAPS_API_KEY} onSave={handleSaveRoute} />
            </PrivateRoute>
          } />
          <Route path="/constructor/:id" element={
            <PrivateRoute>
              <TourConstructorYandex apiKey={YANDEX_MAPS_API_KEY} onSave={handleSaveRoute} />
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