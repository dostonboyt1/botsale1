package uz.pdp.botsale.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.botsale.entity.*;
import uz.pdp.botsale.entity.enums.Gender;
import uz.pdp.botsale.entity.enums.OrderStatus;
import uz.pdp.botsale.entity.enums.PayStatus;
import uz.pdp.botsale.exception.ResourceNotFoundException;
import uz.pdp.botsale.repository.*;
import uz.pdp.botsale.utils.CommonUtils;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramBotService {
    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    BotSale botSale;

    @Autowired
    DeletingMessageRepository deletingMessageRepository;

    @Autowired
    DeletingMessageWithPhotoRepository deletingMessageWithPhotoRepository;

    @Autowired
    BrandRepository brandRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    ProductWithAmountRepository productWithAmountRepository;

    @Autowired
    ProductSizeRepository productSizeRepository;

    @Autowired
    PayTypeRepository payTypeRepository;

    public SendMessage chooseLang(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBotState(BotState.CHOOSE_LANG);
            customerRepository.save(customer);
        } else {
            Customer customer = new Customer();
            customer.setChatId(update.getMessage().getFrom().getId());
            customer.setBotState(BotState.CHOOSE_LANG);
            customerRepository.save(customer);
        }
        sendMessage.setText("Iltimos, til tanlang.\nПожалуста выберите язык");
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText("\uD83C\uDDFA\uD83C\uDDFF  O'zbek tili");
        keyboardRow1.add(keyboardButton);
        keyboardButton = new KeyboardButton();
        keyboardButton.setText("\uD83C\uDDF7\uD83C\uDDFA  Русский язык");
        keyboardRow1.add(keyboardButton);
        keyboard.add(keyboardRow1);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage getLang(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        String text = update.getMessage().getText();
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (text.contains("O'zbek tili")) {
                customer.setLang("UZ");
            }
            if (text.contains("Русский язык")) {
                customer.setLang("RU");
            }
            Customer savedCustomer = customerRepository.save(customer);
            if (savedCustomer.getPhoneNumber() != null) {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Erkaklar uchun" : "Мужской");
                keyboardRow1.add(keyboardButton);
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Ayollar uchun" : "Женская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Bolalar uchun" : "Детская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Tilni o'zgartirish" : "Изменить язык");
                keyboardRow1.add(keyboardButton);

                keyboardButton = new KeyboardButton();
                int orderProductCount=0;
                Optional<Orders> optionalOrders = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, savedCustomer);
                if (optionalOrders.isPresent()){
                    Orders orders = optionalOrders.get();
                   orderProductCount=orders.getPRoductWithAmountList().size();
                }
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Savatcha ("+orderProductCount+")" : "Карзинка ("+orderProductCount+")");
                keyboardRow1.add(keyboardButton);

                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
                savedCustomer.setBotState(BotState.CHOOSE_GENDER);
                customerRepository.save(savedCustomer);
            } else {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton().setRequestContact(true);
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Jo'natish" : "Отправить");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                savedCustomer.setBotState(BotState.SHARE_CONTACT);
                customerRepository.save(savedCustomer);
                sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Telefon raqamingizni jo'nating" : "Отправте номер телефона");
            }
        }
        return sendMessage;
    }

    public SendMessage getLang2(Update update) {
        SendMessage sendMessage = new SendMessage();
        if (update.getCallbackQuery() != null) {
            sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        } else {
            sendMessage.setChatId(update.getMessage().getChatId());
        }
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery() != null ? update.getCallbackQuery().getFrom().getId() : update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPage(0);
            Customer savedCustomer = customerRepository.save(customer);
            if (savedCustomer.getPhoneNumber() != null) {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Erkaklar uchun" : "Мужской");
                keyboardRow1.add(keyboardButton);
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Ayollar uchun" : "Женская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Bolalar uchun" : "Детская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Tilni o'zgartirish" : "Изменить язык");
                keyboardRow1.add(keyboardButton);

                keyboardButton = new KeyboardButton();
                int orderProductCount=0;
                Optional<Orders> optionalOrders = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, savedCustomer);
                if (optionalOrders.isPresent()){
                    Orders orders = optionalOrders.get();
                    orderProductCount=orders.getPRoductWithAmountList().size();
                }
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Savatcha ("+orderProductCount+")" : "Карзинка ("+orderProductCount+")");
                keyboardRow1.add(keyboardButton);

                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
                savedCustomer.setBotState(BotState.CHOOSE_GENDER);
                customerRepository.save(savedCustomer);
            } else {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton().setRequestContact(true);
                keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Jo'natish" : "Отправить");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                savedCustomer.setBotState(BotState.SHARE_CONTACT);
                customerRepository.save(savedCustomer);
                sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Telefon raqamingizni jo'nating" : "Отправте номер телефона");
            }
        }
        return sendMessage;
    }

    public SendMessage getPhoneNumber(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        String phoneNumber = getCheckPhoneNumber(update.getMessage().getContact().getPhoneNumber());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPhoneNumber(phoneNumber);
            Customer savedCustomer = customerRepository.save(customer);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Erkaklar uchun" : "Мужской");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Ayollar uchun" : "Женская");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Bolalar uchun" : "Детская");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(savedCustomer.getLang().equals("UZ") ? "Tilni o'zgartirish" : "Изменить язык");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
            savedCustomer.setBotState(BotState.CHOOSE_GENDER);
            customerRepository.save(savedCustomer);
        }
        return sendMessage;
    }

    public String getCheckPhoneNumber(String phoneNumber) {
        if (phoneNumber.trim().contains("+")) {
            return phoneNumber;
        } else {
            return "+" + phoneNumber;
        }
    }

    public void sendToClient(SendMessage sendMessage) {
        try {

            List<DeletingMessage> messageList = deletingMessageRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessage deletingMessage : messageList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                botSale.execute(deleteMessage);
                deletingMessageRepository.delete(deletingMessage);
            }
            Message message = botSale.execute(sendMessage);
            getFutureDeletingMessageData(message.getMessageId(), message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void sendToClientWithPhoto(SendMessage sendMessage) {
        try {

            List<DeletingMessage> messageList = deletingMessageRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessage deletingMessage : messageList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                botSale.execute(deleteMessage);
                deletingMessageRepository.delete(deletingMessage);
            }
            List<DeletingMessageWithPhoto> messageWithPhotoList = deletingMessageWithPhotoRepository.findAllByChatId(Long.parseLong(sendMessage.getChatId()));
            for (DeletingMessageWithPhoto deletingMessage : messageWithPhotoList) {
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(deletingMessage.getChatId());
                deleteMessage.setMessageId(deletingMessage.getMessageId());
                botSale.execute(deleteMessage);
                deletingMessageWithPhotoRepository.delete(deletingMessage);
            }
            Message message = botSale.execute(sendMessage);
            getFutureDeletingMessageData(message.getMessageId(), message.getChatId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void getFutureDeletingMessageData(Integer messageId, Long chatId) {
        deletingMessageRepository.save(new DeletingMessage(messageId, chatId));
    }

    public void getFutureDeletingMessageWithPhotoData(Integer messageId, Long chatId) {
        deletingMessageWithPhotoRepository.save(new DeletingMessageWithPhoto(messageId, chatId));
    }


    public SendMessage getGender(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        sendMessage.setChatId(update.getMessage().getChatId());

        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            String text = update.getMessage().getText();
//            sendMessage.setText(customer.getLang().equals("UZ")?"Siz "+text+" bo'limini tanladingiz":"Вы выберали раздел "+text);
//            try {
//                botSale.execute(sendMessage);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
            if (text.equals("Erkaklar uchun") || text.equals("Мужской")) {
                customer.setTempGender(Gender.MALE.name());
            }
            if (text.equals("Ayollar uchun") || text.equals("Женская")) {
                customer.setTempGender(Gender.FEMALE.name());
            }
            if (text.equals("Bolalar uchun") || text.equals("Детская")) {
                customer.setTempGender(Gender.KIDS.name());
            }
            customer.setBotState(BotState.CHOOSE_CATEGORY);
            customerRepository.save(customer);

            List<Category> categoryList = categoryRepository.findAllByParentIsNull();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            int count = 0;
            for (Category category : categoryList) {
                count++;
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? category.getNameUz() : category.getNameRu())
                        .setCallbackData("parentCategoryId:" + category.getId()));
                if (count % 2 == 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
            }
            if (count % 2 != 0) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
        }
        return sendMessage;
    }

    public SendMessage back(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPage(0);
            if (customer.getBotState().equals(BotState.CHOOSE_CATEGORY)) {
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                List<KeyboardRow> keyboard = new ArrayList<>();
                KeyboardRow keyboardRow1 = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText(customer.getLang().equals("UZ") ? "Erkaklar uchun" : "Мужской");
                keyboardRow1.add(keyboardButton);
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(customer.getLang().equals("UZ") ? "Ayollar uchun" : "Женская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(customer.getLang().equals("UZ") ? "Bolalar uchun" : "Детская");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                keyboardRow1 = new KeyboardRow();
                keyboardButton = new KeyboardButton();
                keyboardButton.setText(customer.getLang().equals("UZ") ? "Tilni o'zgartirish" : "Изменить язык");
                keyboardRow1.add(keyboardButton);
                keyboard.add(keyboardRow1);
                replyKeyboardMarkup.setKeyboard(keyboard);
                sendMessage.setReplyMarkup(replyKeyboardMarkup);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Menu tanlang" : "Выберите меню");
                customer.setBotState(BotState.CHOOSE_GENDER);
            }
            if (customer.getBotState().equals(BotState.CHOOSE_CHILDREN_CATEGORY) || customer.getBotState().equals(BotState.CHOOSE_BRAND)) {
                List<Category> categoryList = categoryRepository.findAllByParentIsNull();
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                int count = 0;
                for (Category category : categoryList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? category.getNameUz() : category.getNameRu())
                            .setCallbackData("parentCategoryId:" + category.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
                customer.setBotState(BotState.CHOOSE_CATEGORY);
            }
            if (customer.getBotState().equals(BotState.CHOOSE_PRODUCT) || customer.getBotState().equals(BotState.CHOOSE_SIZE) || customer.getBotState().equals(BotState.CHOOSE_AMOUNT) || customer.getBotState().equals(BotState.SHOW_BASKET)) {
                customer.setBotState(BotState.CHOOSE_BRAND);
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                List<Brand> brandList = brandRepository.findAll();
                int count = 0;
                for (Brand brand : brandList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? brand.getNameUz() : brand.getNameRu())
                            .setCallbackData("brandId:" + brand.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
            }
            customerRepository.save(customer);
        }
        return sendMessage;
    }

    public SendMessage getParentCategory(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            String data = update.getCallbackQuery().getData();
            customer.setTempParentCategoryId(Integer.parseInt(data.substring(17)));
            List<Category> childrenCategoryList = categoryRepository.findAllByParentId(Integer.parseInt(data.substring(17)));
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (childrenCategoryList.size() > 0) {
                int count = 0;
                for (Category category : childrenCategoryList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? category.getNameUz() : category.getNameRu())
                            .setCallbackData("childrenCategoryId:" + category.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
                customer.setBotState(BotState.CHOOSE_CHILDREN_CATEGORY);
            } else {
                customer.setBotState(BotState.CHOOSE_BRAND);
                customer.setTempChildrenCategoryId(null);
                List<Brand> brandList = brandRepository.findAll();
                int count = 0;
                for (Brand brand : brandList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? brand.getNameUz() : brand.getNameRu())
                            .setCallbackData("brandId:" + brand.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
            }
            customerRepository.save(customer);

        }
        return sendMessage;
    }

    public SendMessage getChildrenCategory(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            int childrenCategoryId = Integer.parseInt(update.getCallbackQuery().getData().substring(19));
            List<Category> childrenCategoryList = categoryRepository.findAllByParentId(childrenCategoryId);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (childrenCategoryList.size() > 0) {
                customer.setTempParentCategoryId(childrenCategoryId);
                int count = 0;
                for (Category category : childrenCategoryList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? category.getNameUz() : category.getNameRu())
                            .setCallbackData("childrenCategoryId:" + category.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
                customer.setBotState(BotState.CHOOSE_CHILDREN_CATEGORY);
            } else {
                customer.setBotState(BotState.CHOOSE_BRAND);
                customer.setTempChildrenCategoryId(childrenCategoryId);
                List<Brand> brandList = brandRepository.findAll();
                int count = 0;
                for (Brand brand : brandList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton()
                            .setText(customer.getLang().equals("UZ") ? brand.getNameUz() : brand.getNameRu())
                            .setCallbackData("brandId:" + brand.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                    rowInline = new ArrayList<>();
                }
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Kategoriya tanlang." : "Выберите категория.");
                customerRepository.save(customer);
            }
        }
        return sendMessage;
    }

    public SendMessage getBrand(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBotState(BotState.CHOOSE_PRODUCT);
            int brandId = Integer.parseInt(update.getCallbackQuery().getData().substring(8));
            customer.setTempBrandId(brandId);
            Customer savedCustomer = customerRepository.save(customer);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            Page<Product> productPage = productRepository.findAllByCategoryIdAndBrandIdAndGenderAndActive(savedCustomer.getTempChildrenCategoryId() != null ? savedCustomer.getTempChildrenCategoryId() : savedCustomer.getTempParentCategoryId(), savedCustomer.getTempBrandId(), Gender.valueOf(savedCustomer.getTempGender()), true, CommonUtils.getPageable(savedCustomer.getPage(), savedCustomer.getSize()));
            int count = 0;
            for (Product product : productPage) {
                List<Object[]> objects = productRepository.getAmountByAllWarehouseAndProductId(product.getId());
                int amount = 0;
                for (Object[] object : objects) {
                    amount += Integer.parseInt(object[1].toString());
                }
                if (amount > 0) {
                    count++;
                    rowsInline = new ArrayList<>();
                    rowInline = new ArrayList<>();
                    SendPhoto sendPhoto = new SendPhoto();
                    AttachmentContent file = attachmentContentRepository.getByAttachment(product.getAttachment());
                    SendPhoto photo = new SendPhoto()
                            .setChatId(update.getCallbackQuery().getMessage().getChatId())
                            .setParseMode(ParseMode.MARKDOWN)
                            .setPhoto("SetOfDishes" + product.getId(), new ByteArrayInputStream(file.getContent()));
                    photo.setCaption(savedCustomer.getLang().equals("UZ") ? "Nomi : " + product.getNameUz() + "\nIzoh : " + product.getDescriptionUz() + "\nNarxi : " + CommonUtils.thousandSeparator((int) product.getSalePrice()) + " so'm/dona" : "Названия : " + product.getNameRu() + "\nОписания : " + product.getDescriptionRu() + "\nЦена : " + CommonUtils.thousandSeparator((int) product.getSalePrice()) + " сум/шт");
                    rowInline.add(new InlineKeyboardButton()
                            .setText("Buyurtma berish")
                            .setCallbackData("ChoosenProductId:" + product.getId()));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    photo.setReplyMarkup(markupInline);
                    Message execute;
                    try {
                        execute = botSale.execute(photo);
                        getFutureDeletingMessageWithPhotoData(execute.getMessageId(), execute.getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

            rowsInline = new ArrayList<>();
            rowInline = new ArrayList<>();
            if (count > (savedCustomer.getPage() + 1) * savedCustomer.getSize()) {
                rowInline.add(new InlineKeyboardButton().setText(savedCustomer.getLang().equals("UZ") ? "Yana maxsulot ko'rish" : "Ище продукции").setCallbackData("nextPage"));
                rowsInline.add(rowInline);
            }
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(customer.getLang().equals("UZ") ? "Maxsulot tanlang yoki brand o'zgartiring." : "Выберите продукции или измените бренд.");
            if (productPage.getContent().size() < 1 || count < 1) {
                sendMessage.setText(savedCustomer.getLang().equals("UZ") ? "Uzr bunaqa brand maxsulotlarimiz hozircha yo'q. Iltimos boshqa brand tanlang" : "Извените, продукции таким брандом на данний момент отсустьвует. Выберите другой  бренд");
            }
        }
        return sendMessage;
    }

    public SendMessage nextPage(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPage(customer.getPage() + 1);
            Customer savedCustomer = customerRepository.save(customer);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            Page<Product> productPage = productRepository.findAllByCategoryIdAndBrandIdAndGenderAndActive(savedCustomer.getTempChildrenCategoryId() != null ? savedCustomer.getTempChildrenCategoryId() : savedCustomer.getTempParentCategoryId(), savedCustomer.getTempBrandId(), Gender.valueOf(savedCustomer.getTempGender()), true, CommonUtils.getPageable(savedCustomer.getPage(), savedCustomer.getSize()));
            int count = 0;
            for (Product product : productPage) {
                List<Object[]> objects = productRepository.getAmountByAllWarehouseAndProductId(product.getId());
                int amount = 0;
                for (Object[] object : objects) {
                    amount += Integer.parseInt(object[1].toString());
                }
                if (amount > 0) {
                    count++;
                    rowsInline = new ArrayList<>();
                    rowInline = new ArrayList<>();
                    SendPhoto sendPhoto = new SendPhoto();
                    AttachmentContent file = attachmentContentRepository.getByAttachment(product.getAttachment());
                    SendPhoto photo = new SendPhoto()
                            .setChatId(update.getCallbackQuery().getMessage().getChatId())
                            .setParseMode(ParseMode.MARKDOWN)
                            .setPhoto("SetOfDishes" + product.getId(), new ByteArrayInputStream(file.getContent()));
                    photo.setCaption(savedCustomer.getLang().equals("UZ") ? "Nomi : " + product.getNameUz() + "\nIzoh : " + product.getDescriptionUz() + "\nNarxi : " + CommonUtils.thousandSeparator((int) product.getSalePrice()) + " so'm/dona" : "Названия : " + product.getNameRu() + "\nОписания : " + product.getDescriptionRu() + "\nЦена : " + CommonUtils.thousandSeparator((int) product.getSalePrice()) + " сум/шт");
                    rowInline.add(new InlineKeyboardButton()
                            .setText("Buyurtma berish")
                            .setCallbackData("ChoosenProductId:" + product.getId()));
                    rowsInline.add(rowInline);
                    markupInline.setKeyboard(rowsInline);
                    photo.setReplyMarkup(markupInline);
                    Message execute;
                    try {
                        execute = botSale.execute(photo);
                        getFutureDeletingMessageWithPhotoData(execute.getMessageId(), execute.getChatId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }

            rowsInline = new ArrayList<>();
            rowInline = new ArrayList<>();
            if (count > (savedCustomer.getPage() + 1) * savedCustomer.getSize()) {
                rowInline.add(new InlineKeyboardButton().setText(savedCustomer.getLang().equals("UZ") ? "Yana maxsulot ko'rish" : "Ище продукции").setCallbackData("nextPage"));
                rowsInline.add(rowInline);
            }
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(customer.getLang().equals("UZ") ? "Maxsulot tanlang yoki brand o'zgartiring." : "Выберите продукции или измените бренд.");
        }
        return sendMessage;
    }

    public SendMessage getChoosenProduct(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        UUID tempProductId = UUID.fromString(update.getCallbackQuery().getData().substring(17));
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setTempProductId(tempProductId);
            customer.setBotState(BotState.CHOOSE_SIZE);
            Customer savedCustomer = customerRepository.save(customer);
            Optional<Product> optionalProduct = productRepository.findById(tempProductId);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                int count = 0;
                for (ProductSize productSize : product.getProductSizeList()) {
                    List<Object[]> objects = productRepository.getAmountByAllWarehouseAndProductIdAndSize(product.getId(), productSize.getId());
                    int amount = 0;
                    for (Object[] object : objects) {
                        amount += Integer.parseInt(object[1].toString());
                    }
                    if (amount > 0) {
                        count++;
                        rowInline.add(new InlineKeyboardButton().setText(productSize.getName()).setCallbackData("productySizeId:" + productSize.getId()));
                        if (count % 2 == 0) {
                            rowsInline.add(rowInline);
                            rowInline = new ArrayList<>();
                        }
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                }
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Maxsulot razmerini tanlang yoki brand o'zgartiring." : "Выберите размер продукции или измените бренд.");

            }
        }
        return sendMessage;
    }

    public SendMessage getProductSize(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        UUID tempProductSizeId = UUID.fromString(update.getCallbackQuery().getData().substring(15));
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBotState(BotState.CHOOSE_AMOUNT);
            customer.setTempProductSizeId(tempProductSizeId);
            Customer savedCustomer = customerRepository.save(customer);
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            int amount = 0;
            List<Object[]> objects = productRepository.getAmountByAllWarehouseAndProductIdAndSize(customer.getTempProductId(), tempProductSizeId);
            for (Object[] object : objects) {
                amount += Integer.parseInt(object[1].toString());
            }
            savedCustomer.setTempMaxAmount(amount);
            customerRepository.save(savedCustomer);
            rowInline.add(new InlineKeyboardButton().setText("1").setCallbackData("amount:1"));
            rowInline.add(new InlineKeyboardButton().setText("2").setCallbackData("amount:2"));
            rowInline.add(new InlineKeyboardButton().setText("3").setCallbackData("amount:3"));
            rowsInline.add(rowInline);
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText("4").setCallbackData("amount:4"));
            rowInline.add(new InlineKeyboardButton().setText("5").setCallbackData("amount:5"));
            rowInline.add(new InlineKeyboardButton().setText("6").setCallbackData("amount:6"));
            rowsInline.add(rowInline);
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton().setText("7").setCallbackData("amount:7"));
            rowInline.add(new InlineKeyboardButton().setText("8").setCallbackData("amount:8"));
            rowInline.add(new InlineKeyboardButton().setText("9").setCallbackData("amount:9"));
            rowsInline.add(rowInline);
            rowInline = new ArrayList<>();
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                    .setCallbackData("backToGender"));
            rowInline.add(new InlineKeyboardButton()
                    .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                    .setCallbackData("back"));
            rowsInline.add(rowInline);
            markupInline.setKeyboard(rowsInline);
            sendMessage.setReplyMarkup(markupInline);
            sendMessage.setText(customer.getLang().equals("UZ") ? "Maxsulot sonini kiriting.\n Bizda ayni damda siz tanlagan maxsulotdan *" + amount + " ta* mavjud" : "Введите количества продукции. \nНа данний момент в наличии *" + amount + " шт* есть");
        }
        return sendMessage;
    }

    public SendMessage getAmount(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        long chatId = 0;
        int userChatId = 0;
        int amount = 0;
        if (update.getCallbackQuery() != null) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userChatId = update.getCallbackQuery().getFrom().getId();
            amount = Integer.parseInt(update.getCallbackQuery().getData().substring(7));
        } else {
            chatId = update.getMessage().getChatId();
            userChatId = update.getMessage().getFrom().getId();
            amount = Integer.parseInt(update.getMessage().getText());
        }
        sendMessage.setChatId(chatId);
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(userChatId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            if (amount <= customer.getTempMaxAmount()) {
                customer.setTempAmount(amount);
                customer.setBotState(BotState.SHOW_BASKET);
                Customer savedCustomer = customerRepository.save(customer);
                Optional<Orders> optionalOrders = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, savedCustomer);
                if (optionalOrders.isPresent()) {
                    Orders orders = optionalOrders.get();
                    Product product = productRepository.findById(savedCustomer.getTempProductId()).orElseThrow(() -> new ResourceNotFoundException("product", "id", savedCustomer.getTempProductId()));
                    ProductSize productSize = productSizeRepository.findById(savedCustomer.getTempProductSizeId()).orElseThrow(() -> new ResourceNotFoundException("size", "id", savedCustomer.getTempProductSizeId()));
                    Optional<ProductWithAmount> byOrdersAndProductSizeAndProduct = productWithAmountRepository.findByOrdersAndProductSizeAndProduct(orders, productSize, product);
                    if (byOrdersAndProductSizeAndProduct.isPresent()) {
                        ProductWithAmount productWithAmount = byOrdersAndProductSizeAndProduct.get();
                        productWithAmount.setAmount(productWithAmount.getAmount() + amount);
                        productWithAmountRepository.save(productWithAmount);
                    } else {
                        ProductWithAmount productWithAmount = new ProductWithAmount();
                        productWithAmount.setOrders(orders);
                        productWithAmount.setAmount(amount);
                        productWithAmount.setProduct(product);
                        productWithAmount.setProductSize(productSize);
                        productWithAmountRepository.save(productWithAmount);
                    }
                    for (ProductWithAmount productWithAmountByOrder : orders.getPRoductWithAmountList()) {
                        rowInline.add(new InlineKeyboardButton().setText(savedCustomer.getLang().equals("UZ") ? "❌  " + productWithAmountByOrder.getProduct().getNameUz() + "  (" + productWithAmountByOrder.getProductSize().getName() + ") " + productWithAmountByOrder.getAmount() + " ta" : "❌ " + productWithAmountByOrder.getProduct().getNameRu() + "  (" + productWithAmountByOrder.getProductSize().getName() + ") " + productWithAmountByOrder.getAmount() + " шт").setCallbackData("DeletingFromBasket:" + productWithAmountByOrder.getId()));
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                } else {
                    Orders order = new Orders();
                    order.setCustomer(savedCustomer);
                    order.setOrderStatus(OrderStatus.DRAFT);
                    order.setPayStatus(PayStatus.UNPAID);
                    order.setFromBot(true);
                    Orders savedOrder = ordersRepository.save(order);
                    ProductWithAmount productWithAmount = new ProductWithAmount();
                    productWithAmount.setOrders(savedOrder);
                    productWithAmount.setAmount(amount);
                    productWithAmount.setProduct(productRepository.findById(savedCustomer.getTempProductId()).orElseThrow(() -> new ResourceNotFoundException("product", "id", savedCustomer.getTempProductId())));
                    productWithAmount.setProductSize(productSizeRepository.findById(savedCustomer.getTempProductSizeId()).orElseThrow(() -> new ResourceNotFoundException("size", "id", savedCustomer.getTempProductSizeId())));
                    ProductWithAmount savedProductWithAmount = productWithAmountRepository.save(productWithAmount);

                    rowInline.add(new InlineKeyboardButton().setText(savedCustomer.getLang().equals("UZ") ? "❌  " + savedProductWithAmount.getProduct().getNameUz() + "  (" + savedProductWithAmount.getProductSize().getName() + ") " + savedProductWithAmount.getAmount() + " ta" : "❌ " + savedProductWithAmount.getProduct().getNameRu() + "  (" + savedProductWithAmount.getProductSize().getName() + ") " + savedProductWithAmount.getAmount() + " шт").setCallbackData("DeletingFromBasket:" + savedProductWithAmount.getId()));
                    rowsInline.add(rowInline);
                }
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Yana maxsulot tanlash" : "Ище выберат продукции.")
                        .setCallbackData("continueOrder"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Xaridni yakunlash" : "Закончит заказ")
                        .setCallbackData("finishOrder"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Yana maxsulot tanlang yoki xaridni yakunlang" : "Выберите ище или закончите заказ");

            } else {
                rowInline.add(new InlineKeyboardButton().setText("1").setCallbackData("amount:1"));
                rowInline.add(new InlineKeyboardButton().setText("2").setCallbackData("amount:2"));
                rowInline.add(new InlineKeyboardButton().setText("3").setCallbackData("amount:3"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("4").setCallbackData("amount:4"));
                rowInline.add(new InlineKeyboardButton().setText("5").setCallbackData("amount:5"));
                rowInline.add(new InlineKeyboardButton().setText("6").setCallbackData("amount:6"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText("7").setCallbackData("amount:7"));
                rowInline.add(new InlineKeyboardButton().setText("8").setCallbackData("amount:8"));
                rowInline.add(new InlineKeyboardButton().setText("9").setCallbackData("amount:9"));
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowInline.add(new InlineKeyboardButton()
                        .setText(customer.getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                        .setCallbackData("back"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Hozirda " + amount + " ta maxsulot yo'q.\n Bizda ayni damda siz tanlagan maxsulotdan *" + customer.getTempMaxAmount() + " ta* mavjud" : "Извените, " + amount + " шт нет \nНа данний момент в наличии *" + customer.getTempMaxAmount() + " шт* есть");
            }
        }
        return sendMessage;
    }

    public SendMessage removeBasketItem(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        UUID productWithAmountId = UUID.fromString(update.getCallbackQuery().getData().substring(19));
        ProductWithAmount productWithAmount = productWithAmountRepository.findById(productWithAmountId).orElseThrow(() -> new ResourceNotFoundException("productWithAmount", "id", productWithAmountId));
        Orders orders = productWithAmount.getOrders();
        productWithAmountRepository.delete(productWithAmount);
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        for (ProductWithAmount productWithAmountByOrder : orders.getPRoductWithAmountList()) {
            rowInline.add(new InlineKeyboardButton().setText(orders.getCustomer().getLang().equals("UZ") ? "❌  " + productWithAmountByOrder.getProduct().getNameUz() + "  (" + productWithAmountByOrder.getProductSize().getName() + ") " + productWithAmountByOrder.getAmount() + " ta" : "❌ " + productWithAmountByOrder.getProduct().getNameRu() + "  (" + productWithAmountByOrder.getProductSize().getName() + ") " + productWithAmountByOrder.getAmount() + " шт").setCallbackData("DeletingFromBasket:" + productWithAmountByOrder.getId()));
            rowsInline.add(rowInline);
            rowInline = new ArrayList<>();
        }
        rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton()
                .setText(orders.getCustomer().getLang().equals("UZ") ? "Yana maxsulot tanlash" : "Ище выберат продукции.")
                .setCallbackData("continueOrder"));
        rowInline.add(new InlineKeyboardButton()
                .setText(orders.getCustomer().getLang().equals("UZ") ? "Xaridni yakunlash" : "Закончит заказ")
                .setCallbackData("finishOrder"));
        rowsInline.add(rowInline);
        rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton()
                .setText(orders.getCustomer().getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                .setCallbackData("backToGender"));
        rowInline.add(new InlineKeyboardButton()
                .setText(orders.getCustomer().getLang().equals("UZ") ? "Orqaga qaytish" : "Назад")
                .setCallbackData("back"));
        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);
        sendMessage.setReplyMarkup(markupInline);
        sendMessage.setText(orders.getCustomer().getLang().equals("UZ") ? "Yana maxsulot tanlang yoki xaridni yakunlang" : "Выберите ище или закончите заказ");
        return sendMessage;
    }

    public SendMessage finishOrder(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setBotState(BotState.CHOOSE_LOCATION_OR_ADDRESS);
            customerRepository.save(customer);
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton().setRequestLocation(true);
            keyboardButton.setText(customer.getLang().equals("UZ") ? "Lokatsiyani jo'natish" : "Отправить локатцию");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            sendMessage.setText(customer.getLang().equals("UZ") ? "Locatsiyzngizni jonating yoki addressingizni kiriting" : "Отправте локатция или введите адрес");
        }
        return sendMessage;
    }

    public SendMessage getLocation(Update update) {
        SendMessage sendMessage = new SendMessage().setParseMode(ParseMode.MARKDOWN);
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Optional<Orders> optionalOrder = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, customer);
            if (optionalOrder.isPresent()) {
                Orders order = optionalOrder.get();
                if (update.getMessage().getLocation() != null) {
                    Location location = update.getMessage().getLocation();
                    order.setLan((double) location.getLongitude());
                    order.setLat((double) location.getLatitude());
                } else {
                    order.setAddress(update.getMessage().getText());
                }
                ordersRepository.save(order);
                customer.setBotState(BotState.CHOOSE_PAY_TYPE);
                customerRepository.save(customer);
                double orderSum = 0;
                for (ProductWithAmount productWithAmount : order.getPRoductWithAmountList()) {
                    orderSum += productWithAmount.getAmount() * productWithAmount.getProduct().getSalePrice();
                }
                List<PayType> payTypeList = payTypeRepository.findAllByActive(true);
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                int count = 0;
                for (PayType payType : payTypeList) {
                    count++;
                    rowInline.add(new InlineKeyboardButton().setText(customer.getLang().equals("UZ") ? payType.getNameUz() : payType.getNameRu()).setCallbackData("payType:" + payType.getId()));
                    if (count % 2 == 0) {
                        rowsInline.add(rowInline);
                        rowInline = new ArrayList<>();
                    }
                }
                if (count % 2 != 0) {
                    rowsInline.add(rowInline);
                }
                rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton().setText(customer.getLang().equals("UZ") ? "Buyurtmani bekor qilish.": "Отменить заказ").setCallbackData("cancelOrder"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(customer.getLang().equals("UZ") ? "Sizning buyurtmangiz *" + CommonUtils.thousandSeparator((int) orderSum) + " so'm* boldi.To'lov turini tanlang." : "Сумма вашева заказа составляет *" + CommonUtils.thousandSeparator((int) orderSum) + " сум*. Выберите тип платежа");
            }
        }
        return sendMessage;
    }

    public SendMessage cancelOrder(Update update) {
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            sendMessage.setText(customer.getLang().equals("UZ")?"Sizning buyurtmangiz bekor qilindi .Yana buyurtma berish uchun menuni tanlang.":"Ваш заказ отменен. Выберите меню чтобы дать заказ.");
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
            replyKeyboardMarkup.setSelective(true);
            replyKeyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();
            KeyboardRow keyboardRow1 = new KeyboardRow();
            KeyboardButton keyboardButton = new KeyboardButton();
            keyboardButton.setText(customer.getLang().equals("UZ") ? "Erkaklar uchun" : "Мужской");
            keyboardRow1.add(keyboardButton);
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(customer.getLang().equals("UZ") ? "Ayollar uchun" : "Женская");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(customer.getLang().equals("UZ") ? "Bolalar uchun" : "Детская");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            keyboardRow1 = new KeyboardRow();
            keyboardButton = new KeyboardButton();
            keyboardButton.setText(customer.getLang().equals("UZ") ? "Tilni o'zgartirish" : "Изменить язык");
            keyboardRow1.add(keyboardButton);
            keyboard.add(keyboardRow1);
            replyKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            customer.setBotState(BotState.CHOOSE_GENDER);
            customerRepository.save(customer);
            Optional<Orders> optionalOrders = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, customer);
            if (optionalOrders.isPresent()){
                Orders orders = optionalOrders.get();
                for (ProductWithAmount productWithAmount : orders.getPRoductWithAmountList()) {
                    productWithAmountRepository.delete(productWithAmount);
                }
            }
        }
        return sendMessage;
    }

    public SendMessage getPayType(Update update) {
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getCallbackQuery().getFrom().getId());
        if (optionalCustomer.isPresent()){
            Customer customer = optionalCustomer.get();
            customer.setBotState(BotState.FINISH_ORDER);
            customerRepository.save(customer);
            Optional<Orders> optionalOrder = ordersRepository.findByOrderStatusAndCustomer(OrderStatus.DRAFT, customer);
            if (optionalOrder.isPresent()){
                Orders order = optionalOrder.get();
                order.setPayType(payTypeRepository.findById(Integer.parseInt(update.getCallbackQuery().getData().substring(8))).orElseThrow(() -> new ResourceNotFoundException("payType","id",Integer.parseInt(update.getCallbackQuery().getData().substring(8)))));
                order.setOrderStatus(OrderStatus.NEW);
                double orderSum=0;
                for (ProductWithAmount productWithAmount : order.getPRoductWithAmountList()) {
                    orderSum+=productWithAmount.getAmount()*productWithAmount.getProduct().getSalePrice();
                }
                order.setOrderSum(orderSum);
                ordersRepository.save(order);
                InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
                List<InlineKeyboardButton> rowInline = new ArrayList<>();
                rowInline.add(new InlineKeyboardButton()
                        .setText(order.getCustomer().getLang().equals("UZ") ? "Bosh menuga qaytish" : "Назад в главний меню")
                        .setCallbackData("backToGender"));
                rowsInline.add(rowInline);
                markupInline.setKeyboard(rowsInline);
                sendMessage.setReplyMarkup(markupInline);
                sendMessage.setText(order.getCustomer().getLang().equals("UZ") ? "Sizning buyurtmangiz qabul qilindi. Tez orada operatorlarimiz siz bilan boglanishadi." : "Ваш заказ принят. Скоро наши оператори свяжеться с вами.");

            }

        }
        return sendMessage;
    }
}
