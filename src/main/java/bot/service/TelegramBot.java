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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    UserService service = new UserService();

    User user = new User();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    final BotConfig config;

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
            User users = service.findChatId(update.getMessage().getChatId());

            switch (massageTest) {
                case "/start":
                    registerUser(update.getMessage());
                    startCommandReceived(chatId, users.getUserName());
                    startMenu(chatId, "Основне меню");
                    break;
                case "/help":

                    break;
                case "/settings":
                    settingsMenu(chatId, "Налаштування");
                    break;
                case "Допомога":

                    break;
                case "Отримати інформацію":
                    informationACurrency(chatId, "🎃", users.getCurrency(), users.getBankName(), users.getNumberAFP());
                    break;
                case "Налаштування":
                    settingsMenu(chatId, "Налаштування");
                    break;
                case "Назад":
                    startMenu(chatId, "Основне меню.");
                    break;
                case "Назад до налаштувань":
                    settingsMenu(chatId, "Налаштування");
                    break;
                case "Банк":
                    banks(chatId, "Оберіть банк.", users.getBankName());
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
                    numAFP(chatId, "Оберіть кільлькість знаків після коми.", users.getNumberAFP());
                    break;
                case "2":
                    numAFP(chatId, "Оберіть кільлькість знаків після коми.", 0);
                    break;
                case "3":
                    numAFP(chatId, "Оберіть кільлькість знаків після коми.", 1);
                    break;
                case "4":
                    numAFP(chatId, "Оберіть кільлькість знаків після коми.", 2);
                    break;
                case "Валюта":
                    currencySelect(chatId, "Оберіть валюту.", users.getCurrency());
                    break;
                case "USD":
                    currencySelect(chatId, "Оберано USD.", 0);
                    break;
                case "EUR":
                    currencySelect(chatId, "Оберано EUR.", 1);
                    break;
                case "Час сповіщення":
                    notificationUser(chatId, "Оберіть час сповіщення.", users.getHour());
                    break;
                case "Вимкнути сповіщення":
                        notificationUser(chatId, "Обрано: Вимкнути сповіщення.", 0);
                    break;
                case "9":
                    notificationUser(chatId, "Обрано: 9 годину", 1);
                    break;
                case "10":
                    notificationUser(chatId, "Обрано: 10 годину", 2);
                    break;
                case "11":
                    notificationUser(chatId, "Обрано: 11 годину", 3);
                    break;
                case "12":
                    notificationUser(chatId, "Обрано: 12 годину", 4);
                    break;
                case "13":
                    notificationUser(chatId, "Обрано: 13 годину", 5);
                    break;
                case "14":
                    notificationUser(chatId, "Обрано: 14 годину", 6);
                    break;
                case "15":
                    notificationUser(chatId, "Обрано: 15 годину", 7);
                    break;
                case "16":
                    notificationUser(chatId, "Обрано: 16 годину", 8);
                    break;
                case "17":
                    notificationUser(chatId, "Обрано: 17 годину", 9);
                    break;
                case "18":
                    notificationUser(chatId, "Обрано: 18 годину", 10);
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

    protected User getUser(long chatId, User user){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));

        return user = service.findChatId(Long.valueOf(message.getChatId()));
    }
    private void registerUser(Message message) {
        if (!service.existByChatId(message.getChatId())) {
            var chatId = message.getChatId();
            var chat = message.getChat();

            User user = getUser(message.getChatId(), new User());

            user.setChatId(chatId);
            user.setUserName(chat.getUserName());
            user.setBankName(BankName.MonoBank.ordinal());
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

        User user1 = getUser(chatId, user);

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

        User user1 = getUser(chatId, user);

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

        User user1 = getUser(chatId, user);

        user1.setCurrency(choice);
        service.save(user1);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("USD" + ((choice == 0) ? "✅" : ""));
        row.add("EUR" + ((choice == 1) ? "✅" : ""));
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


        String allText = "";

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
        }else{
            currencyText = "EUR";
        }

        sendMessage(chatId,
                "Обрано:\n\nБанк: " + bankText + "." +
                        "\nВалюта: " + currencyText + "." +
                        "\nК-сть знаків після коми: " + (countNum - 3) + ".");

        if (bank == 0 || bank == 1){
            String getValSell = bankClas.getRate(currencyText, bankText, "sell");
            String doneInfoSell = getValSell.substring(0, countNum);

            String getValBuy = bankClas.getRate(currencyText, bankText, "buy");
            String doneInfoBuy = getValBuy.substring(0, countNum);
            allText = "\n\nПродаж: " + doneInfoSell + "\nКупівля: " + doneInfoBuy;
        }else {
            String getValBuy = bankClas.getRate(currencyText, bankText, "buy");
            String doneInfoBuy = getValBuy.substring(0, countNum);
            allText = "\n\nКупівля: " + doneInfoBuy;
        }

        sendMessage(chatId, allText);

        executeMessage(message);
    }

    private void notificationUser(long chatId, String textToSend, Integer choice){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        User user1 = getUser(chatId, user);

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