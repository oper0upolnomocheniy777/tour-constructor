import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, isAgent, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const getUserDisplayName = () => {
    if (!user) return '';
    if (user.firstName && user.lastName) {
      return `${user.firstName} ${user.lastName}`;
    }
    return user.username;
  };

  const userDiscount = user?.discount ?? 0;

  return (
    <nav className="navbar">
      <div className="navbar-container">
        <Link to="/" className="navbar-brand">
          Tour Constructor
        </Link>

        <div className="navbar-menu">
          <Link to="/tours" className="nav-link">
            Туры
          </Link>

          {isAuthenticated && (
            <>
              <Link to="/my-tours" className="nav-link">
                Мои туры
              </Link>
              <Link to="/my-purchases" className="nav-link">
                Мои покупки
              </Link>
              <Link to="/constructor" className="nav-link">
                Конструктор
              </Link>
            </>
          )}

          {isAgent && (
          <>
            <Link to="/agent/tours" className="nav-link agent-link">
              Управление турами
            </Link>
            <Link to="/admin" className="nav-link admin-link">
              Админ-панель
            </Link>
          </>
        )}

          {isAuthenticated ? (
            <div className="nav-user">
              <span className="username">
                {getUserDisplayName()}
              </span>
              {userDiscount > 0 && (
                <span className="discount-badge">-{userDiscount}%</span>
              )}
              <button onClick={handleLogout} className="btn-logout">
                Выйти
              </button>
            </div>
          ) : (
            <div className="nav-auth">
              <Link to="/login" className="btn-login">Войти</Link>
              <Link to="/register" className="btn-register">Регистрация</Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
};