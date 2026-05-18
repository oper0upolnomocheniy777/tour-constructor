import React, { useState } from 'react';
import Chatbot from 'react-chatbot-kit';
import 'react-chatbot-kit/build/main.css';
import config from './config';
import MessageParser from './MessageParser';
import ActionProvider from './ActionProvider';
import './ChatbotWidget.css';

const ChatbotWidget: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="chatbot-widget">
      {isOpen && (
        <div className="chatbot-container">
          <Chatbot
            config={config}
            messageParser={MessageParser}
            actionProvider={ActionProvider}
          />
        </div>
      )}
      <button
        className="chatbot-button"
        onClick={() => setIsOpen(!isOpen)}
        title="Чат-бот помощник"
      >
        💬
      </button>
    </div>
  );
};

export default ChatbotWidget;