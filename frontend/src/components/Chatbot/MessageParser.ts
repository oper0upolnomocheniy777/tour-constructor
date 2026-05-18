class MessageParser {
  actionProvider: any;
  state: any;

  constructor(actionProvider: any, state: any) {
    this.actionProvider = actionProvider;
    this.state = state;
  }

  parse(message: string) {
    const lowerMessage = message.toLowerCase();
    
    if (lowerMessage.includes("привет") || lowerMessage.includes("здравствуй")) {
      this.actionProvider.greet();
    } 
    else if (lowerMessage.includes("как работа") || lowerMessage.includes("конструктор")) {
      this.actionProvider.explainConstructor();
    }
    else if (lowerMessage.includes("горящ") || lowerMessage.includes("горячий") || lowerMessage.includes("акция")) {
      this.actionProvider.handleHotTours();
    }
    else if (lowerMessage.includes("море") || lowerMessage.includes("пляж")) {
      this.actionProvider.suggestSea();
    }
    else if (lowerMessage.includes("горы") || lowerMessage.includes("поход")) {
      this.actionProvider.suggestMountains();
    }
    else if (lowerMessage.includes("экскурс")) {
      this.actionProvider.suggestExcursion();
    }
    else if (lowerMessage.includes("шопинг") || lowerMessage.includes("покупк")) {
      this.actionProvider.suggestShopping();
    }
    else if (lowerMessage.includes("цена") || lowerMessage.includes("сколько стоит")) {
      this.actionProvider.tellPrices();
    }
    else if (lowerMessage.includes("помощь")) {
      this.actionProvider.showHelp();
    }
    else {
      this.actionProvider.handleUnknown();
    }
  }
}

export default MessageParser;