package bot.service;

import bot.Bank;
import bot.config.BotConfig;
import bot.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private UserService service;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    final BotConfig config;
    String getInformation = "Актуальна інформація на:\n" +
             LocalDate.now();
    String allText;
    String settings = "Налаштування";
    String mainMenu = "Основне меню";
    String help_menu = """
            Короткі відомості про команди ⚙
            /start - для початку роботи.
            /settings - для налаштування параметрів вибору.

            Короткі відомості.
            Цей бот призначений для отримання інформації про курс валют.
            За допомогою нього Ви можете отримати актуальний курс валют з обраних банків.
            Як новий користувач, у Вас обрано банк - PrivatBank, валюта - EUR, сповіщення вимкнені.
            Оберіть в меню налаштувань комфортні для вас параметри, при завершенні поверніться на гологовне меню та натисніть на "Отримати інформацію".

            Дякую що користуєтесь нашим ботом.❤
            """;
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/settings", "set your preferences"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public String getBotToken() {
        return config.getToken();
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String massageTest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (massageTest) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId,update.getMessage().getChat().getUserName());
                    startMenu(chatId, mainMenu);
                    break;
                case "/help", "Допомога":
                    sendMessage(chatId, help_menu);
                    break;
                case "Отримати інформацію":
                    informationACurrency(chatId, getInformation, service.findChatId(chatId).getCurrency(), service.findChatId(chatId).getBankName(), service.findChatId(chatId).getNumberAFP());
                    break;
                case "Налаштування", "Назад до налаштувань", "/settings":
                    settingsMenu(chatId, settings);
                    break;
                case "Назад":
                    startMenu(chatId, mainMenu);
                    break;
                case "Банк":
                    banks(chatId, "Оберіть банк.", service.findChatId(chatId).getBankName());
                    break;
                case "PrivatBank":
                    banks(chatId, "Обрано PrivatBank.", 0);
                    break;
                case "MonoBank":
                    banks(chatId, "Обрано MonoBank.", 1);
                    break;
                case "NBU":
                    banks(chatId, "Обрано NBU.", 2);
                    break;
                case "Кількість знаків після коми":
                    numAFP(chatId, "Оберіть кільлькість знаків після коми.", service.findChatId(chatId).getNumberAFP());
                    break;
                case "2":
                    numAFP(chatId, "Обрано: 2.", 0);
                    break;
                case "3":
                    numAFP(chatId, "Обрано: 3.", 1);
                    break;
                case "4":
                    numAFP(chatId, "Обрано: 4.", 2);
                    break;
                case "Валюта":
                    currencySelect(chatId, "Оберіть валюту.", service.findChatId(chatId).getCurrency());
                    break;
                case "USD", "EUR":
                    currencySelect(chatId, "Оберано USD і EUR.", 2);
                    break;
                case "USD✅":
                    currencySelect(chatId, "Оберано EUR.", 1);
                    break;
                case "EUR✅":
                    currencySelect(chatId, "Оберано USD.", 0);
                    break;
                case "Час сповіщення":
                    notificationUser(chatId, "Оберіть час сповіщення.", service.findChatId(chatId).getHour());
                    break;
                case "Вимкнути сповіщення":
                        notificationUser(chatId, "Сповіщення вимкнуті.", 0);
                    break;
                case "9":
                    notificationUser(chatId, "Обрано: 9-у годину.", 1);
                    break;
                case "10":
                    notificationUser(chatId, "Обрано: 10-у годину.", 2);
                    break;
                case "11":
                    notificationUser(chatId, "Обрано: 11-у годину.", 3);
                    break;
                case "12":
                    notificationUser(chatId, "Обрано: 12-у годину.", 4);
                    break;
                case "13":
                    notificationUser(chatId, "Обрано: 13-у годину.", 5);
                    break;
                case "14":
                    notificationUser(chatId, "Обрано: 14-у годину.", 6);
                    break;
                case "15":
                    notificationUser(chatId, "Обрано: 15-у годину.", 7);
                    break;
                case "16":
                    notificationUser(chatId, "Обрано: 16-у годину.", 8);
                    break;
                case "17":
                    notificationUser(chatId, "Обрано: 17-у годину.", 9);
                    break;
                case "18":
                    notificationUser(chatId, "Обрано: 18-у годину.", 10);
                    break;

                default:
                    sendMessage(chatId, "Щось не так");
            }
        }
    }
    private void startCommandReceived(long chatId, String name) {
        String answer = "Hi, " + name + ", nice to meet you! ✨";
        sendMessage(chatId, answer);
    }
    private void startMenu(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Отримати інформацію");

        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Налаштування");
        row.add("Допомога");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }
    private void settingsMenu(long chatId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Кількість знаків після коми");
        row.add("Банк");
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Валюта");
        row.add("Час сповіщення");

        keyboardRows.add(row);
        row = new KeyboardRow();
        row.add("Назад");
        keyboardRows.add(row);
        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);

        executeMessage(message);
    }
    protected User getUser(long chatId){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        return service.findChatId(Long.valueOf(message.getChatId()));
    }
    private void registerUser(Message message) {
        if (!service.existByChatId(message.getChatId())) {
            Long chatId = message.getChatId();
            var chat = message.getChat();

            User user = new User();

            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            user.setBankName(BankName.PrivatBank.ordinal());
            user.setNumberAFP(NumberAFP.two.ordinal());
            user.setCurrency(Currency.eur.ordinal());
            user.setHour(NotificationHour.zero.ordinal());

            service.save(user);
            System.out.println("user saved: " + user);
        }
    }
    private void banks(long chatId, String textToSend, Integer choice){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        User user1 = getUser(chatId);

        user1.setBankName(choice);
        service.save(user1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("PrivatBank" + ((choice == 0) ? "✅" : ""));
        row.add("MonoBank" + ((choice == 1) ? "✅" : ""));
        row.add("NBU" + ((choice == 2) ? "✅" : ""));
        backToSettings(message, user1, keyboardMarkup, keyboardRows, row);
    }
    private void numAFP(long chatId, String textToSend, Integer choice){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        User user1 = getUser(chatId);

        user1.setNumberAFP(choice);
        service.save(user1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("2" + ((choice == 0) ? "✅" : ""));
        row.add("3" + ((choice == 1) ? "✅" : ""));
        row.add("4" + ((choice == 2) ? "✅" : ""));
        backToSettings(message, user1, keyboardMarkup, keyboardRows, row);
    }
    private void currencySelect(long chatId, String textToSend, Integer choice){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        User user1 = getUser(chatId);

        user1.setCurrency(choice);
        service.save(user1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();

        if (choice == 0 || choice == 1){
            row.add("USD" + ((choice == 0) ? "✅" : ""));
            row.add("EUR" + ((choice == 1) ? "✅" : ""));
        }else if (choice == 2){
            row.add("USD✅");
            row.add("EUR✅");
        }

        backToSettings(message, user1, keyboardMarkup, keyboardRows, row);
    }
    private void informationACurrency(long chatId, String textToSend, Integer currencySel, Integer bankSel, Integer numAFPSel){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        Bank bankClas = new Bank();
        String currencyText;
        String bankText;
        int countNum;

        int currency =  currencySel;
        int bank =  bankSel;
        int numAFP =  numAFPSel;

        if (numAFP == 0){
             countNum = 5;
        }else if (numAFP == 1){
             countNum = 6;
        }else {
             countNum = 7;
        }

        if (bank == 0){
             bankText = "PrivatBank";
        }else if (bank == 1){
             bankText = "MonoBank";
        }else {
             bankText = "NBU";
        }

        if (currency == 0){
            currencyText = "USD";
        }else if(currency == 1){
            currencyText = "EUR";
        }else
            currencyText = "USD і EUR";

        allText = "Обрано:\n\nБанк: " + bankText + ".\nВалюта: " + currencyText + ".\nК-сть знаків після коми: " + (countNum - 3) + ".";

        if (bank == 0 || bank == 1){
            if (currency == 0 || currency == 1){
                String getValSell = bankClas.getRate(currencyText, bankText, "sell");
                String doneInfoSell = getValSell.substring(0, countNum);

                String getValBuy = bankClas.getRate(currencyText, bankText, "buy");
                String doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\n\nПродаж: " + doneInfoSell + "\nКупівля: " + doneInfoBuy;
            }else{
                allText += "\n\nUSD";
                String getValSell = bankClas.getRate("USD", bankText, "sell");
                String doneInfoSell = getValSell.substring(0, countNum);

                String getValBuy = bankClas.getRate("USD", bankText, "buy");
                String doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\nПродаж: " + doneInfoSell + "\nКупівля: " + doneInfoBuy;

                allText += "\n\nEUR";
                getValSell = bankClas.getRate("EUR", bankText, "sell");
                doneInfoSell = getValSell.substring(0, countNum);

                getValBuy = bankClas.getRate("EUR", bankText, "buy");
                doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\nПродаж: " + doneInfoSell + "\nКупівля: " + doneInfoBuy;
            }
        }else {
            if (currency == 0 || currency == 1){
                String getValBuy = bankClas.getRate(currencyText, bankText, "buy");
                String doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\n\nКупівля: " + doneInfoBuy;
            }else {
                allText += "\n\nUSD";
                String getValBuy = bankClas.getRate("USD", bankText, "buy");
                String doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\nКупівля: " + doneInfoBuy;

                allText += "\n\nEUR";
                getValBuy = bankClas.getRate("EUR", bankText, "buy");
                doneInfoBuy = getValBuy.substring(0, countNum);
                allText += "\nКупівля: " + doneInfoBuy;
            }
        }
        sendMessage(chatId, allText);
        executeMessage(message);
    }
    private void notificationUser(long chatId, String textToSend, Integer choice){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        User user1 = getUser(chatId);

        user1.setHour(choice);
        service.save(user1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("9" + ((choice == 1) ? "✅" : ""));
        row.add("10" + ((choice == 2) ? "✅" : ""));
        row.add("11" + ((choice == 3) ? "✅" : ""));
        row.add("12" + ((choice == 4) ? "✅" : ""));
        row.add("13" + ((choice == 5) ? "✅" : ""));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("14" + ((choice == 6) ? "✅" : ""));
        row.add("15" + ((choice == 7) ? "✅" : ""));
        row.add("16" + ((choice == 8) ? "✅" : ""));
        row.add("17" + ((choice == 9) ? "✅" : ""));
        row.add("18" + ((choice == 10) ? "✅" : ""));
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Вимкнути сповіщення" + ((choice == 0) ? "✅" : ""));
        backToSettings(message, user1, keyboardMarkup, keyboardRows, row);
    }

    private void backToSettings(SendMessage message, User user1, ReplyKeyboardMarkup keyboardMarkup, List<KeyboardRow> keyboardRows, KeyboardRow row) {
        keyboardRows.add(row);

        row = new KeyboardRow();
        row.add("Назад до налаштувань");
        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);
        message.setReplyMarkup(keyboardMarkup);
        System.out.println("user saved: " + user1);
        executeMessage(message);
    }

    protected void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        executeMessage(message);
    }
    private void executeMessage(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException ignored) {
        }
    }
}