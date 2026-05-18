import { createChatBotMessage } from 'react-chatbot-kit';

const config = {
  botName: "ТурКонсультант",
  initialMessages: [
    createChatBotMessage(
      "✈️ Привет! Я помогу тебе с выбором тура.\n\nЧто я умею:\n• Расскажу о горящих турах 🔥\n• Подскажу направление по интересам\n• Объясню, как работает конструктор\n• Расскажу о ценах и скидках\n\nНапиши 'помощь' или выбери вопрос ниже.",
      {}  // ← второй аргумент (пустой объект)
    ),
  ],
  customStyles: {
    botMessageBox: {
      backgroundColor: "#47BDCF",
    },
    chatButton: {
      backgroundColor: "#47BDCF",
    },
  },
};

export default config;