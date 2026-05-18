import { createChatBotMessage } from 'react-chatbot-kit';

class ActionProvider {
  createChatBotMessage: any;
  setState: any;
  createClientMessage: any;
  stateRef: any;
  createCustomMessage: any;
  actions: any;

  constructor(
    createChatBotMessage: any,
    setStateFunc: any,
    createClientMessage: any,
    stateRef: any,
    createCustomMessage: any,
    ...rest: any[]
  ) {
    this.createChatBotMessage = createChatBotMessage;
    this.setState = setStateFunc;
    this.createClientMessage = createClientMessage;
    this.stateRef = stateRef;
    this.createCustomMessage = createCustomMessage;
    this.actions = rest[0];
  }

  greet() {
    const message = this.createChatBotMessage(
      "Привет! ✈️ Рад видеть тебя! Чем могу помочь? Напиши 'помощь' для списка команд.",
      {}
    );
    this.updateChatbotMessage(message);
  }

  explainConstructor() {
    const message = this.createChatBotMessage(
      "Конструктор туров — это карта, на которую можно кликать и добавлять точки маршрута. Точки можно перетаскивать мышкой, менять порядок. Когда добавишь 2+ точки, построится маршрут с расстоянием и временем. Затем сохрани тур с названием и ценой! 🗺️",
      {}
    );
    this.updateChatbotMessage(message);
  }

  handleHotTours() {
    const message = this.createChatBotMessage(
      "🔥 Горящие туры — это предложения со скидкой до 25%! Они отмечены специальным значком на главной странице. Успей забронировать!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  suggestSea() {
    const message = this.createChatBotMessage(
      "🌊 Отдых у моря: Сочи, Бали, Турция, Таиланд. На сайте есть туры с пляжным отдыхом. Выбери в фильтре 'Отдых'!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  suggestMountains() {
    const message = this.createChatBotMessage(
      "🏔️ Горные направления: Алтай, Кавказ, Гималаи, Альпы. В конструкторе можно проложить пеший или автомобильный маршрут по горам!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  suggestExcursion() {
    const message = this.createChatBotMessage(
      "🏛️ Экскурсионные туры: Париж, Рим, Золотое кольцо России, Санкт-Петербург. Выбери в фильтре 'Экскурсии'!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  suggestShopping() {
    const message = this.createChatBotMessage(
      "🛍️ Шопинг-туры: Милан, Дубай, Стамбул, Бангкок. Выбери в фильтре 'Шопинг'!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  tellPrices() {
    const message = this.createChatBotMessage(
      "💰 Цены на туры зависят от направления и длительности. На сайте есть туры от 25 000 ₽. Также действуют персональные скидки для постоянных клиентов!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  showHelp() {
    const message = this.createChatBotMessage(
      "📋 Список команд:\n\n• 'конструктор' — как работает конструктор\n• 'горящие' — горящие туры\n• 'море' — подбор тура к морю\n• 'горы' — подбор горного тура\n• 'экскурсии' — экскурсионные туры\n• 'шопинг' — шопинг-туры\n• 'цена' — информация о ценах\n\nМожешь написать любой вопрос!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  handleUnknown() {
    const message = this.createChatBotMessage(
      "😊 Не совсем понял. Напиши 'помощь', чтобы увидеть список команд, или задай вопрос про туры!",
      {}
    );
    this.updateChatbotMessage(message);
  }

  updateChatbotMessage(message: any) {
    this.setState((prev: any) => ({
      ...prev,
      messages: [...prev.messages, message],
    }));
  }
}

export default ActionProvider;