package uz.pdp.botsale.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import uz.pdp.botsale.entity.Customer;
import uz.pdp.botsale.repository.CustomerRepository;

import java.util.Optional;

@Component
public class BotSale extends TelegramLongPollingBot {
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.username}")
    private String botUsername;

    @Autowired
    TelegramBotService telegramBotService;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasContact()) {
                telegramBotService.getFutureDeletingMessageData(update.getMessage().getMessageId(),update.getMessage().getChatId());
                telegramBotService.sendToClient(telegramBotService.getPhoneNumber(update));
            } else if (update.getMessage().hasLocation()) {
                telegramBotService.getFutureDeletingMessageData(update.getMessage().getMessageId(),update.getMessage().getChatId());
                telegramBotService.sendToClient(telegramBotService.getLocation(update));
            } else if (update.getMessage().hasText()) {
                if (update.getMessage().getText().equals("/start")) {
                    telegramBotService.sendToClient(telegramBotService.chooseLang(update));
                } else if (update.getMessage().getText().contains("O'zbek tili") || update.getMessage().getText().contains("Русский язык")) {
                    telegramBotService.getFutureDeletingMessageData(update.getMessage().getMessageId(),update.getMessage().getChatId());
                    telegramBotService.sendToClient(telegramBotService.getLang(update));
                } else if (update.getMessage().getText().contains("Tilni o'zgartirish") || update.getMessage().getText().contains("Изменить язык")) {
                    telegramBotService.getFutureDeletingMessageData(update.getMessage().getMessageId(),update.getMessage().getChatId());
                    telegramBotService.sendToClient(telegramBotService.chooseLang(update));
                } else {
                    telegramBotService.getFutureDeletingMessageData(update.getMessage().getMessageId(),update.getMessage().getChatId());
                    Optional<Customer> optionalCustomer = customerRepository.findByChatId(update.getMessage().getFrom().getId());
                    if (optionalCustomer.isPresent()) {
                        Customer customer = optionalCustomer.get();
                        if (customer.getBotState().equals(BotState.SHARE_CONTACT)) {
                            telegramBotService.sendToClient(telegramBotService.getLang2(update));
                        }
                        if (customer.getBotState().equals(BotState.CHOOSE_LANG)) {
                            telegramBotService.sendToClient(telegramBotService.chooseLang(update));
                        }
                        if (customer.getBotState().equals(BotState.CHOOSE_GENDER)) {
                            telegramBotService.sendToClient(telegramBotService.getGender(update));
                        }
                        if (customer.getBotState().equals(BotState.CHOOSE_AMOUNT)) {
                            telegramBotService.sendToClient(telegramBotService.getAmount(update));
                        }
                        if (customer.getBotState().equals(BotState.CHOOSE_LOCATION_OR_ADDRESS)) {
                            telegramBotService.sendToClient(telegramBotService.getLocation(update));
                        }
                    }
                }
            }

        } else if (update.getCallbackQuery() != null) {
            if (update.getCallbackQuery().getData().equals("backToGender")||update.getCallbackQuery().getData().equals("continueOrder")) {
                telegramBotService.sendToClientWithPhoto(telegramBotService.getLang2(update));
            }
            if (update.getCallbackQuery().getData().equals("back")) {
                telegramBotService.sendToClientWithPhoto(telegramBotService.back(update));
            }
            if (update.getCallbackQuery().getData().startsWith("parentCategoryId:")) {
                telegramBotService.sendToClient(telegramBotService.getParentCategory(update));
            }
            if (update.getCallbackQuery().getData().startsWith("childrenCategoryId:")) {
                telegramBotService.sendToClient(telegramBotService.getChildrenCategory(update));
            }
            if (update.getCallbackQuery().getData().startsWith("brandId:")) {
                telegramBotService.sendToClient(telegramBotService.getBrand(update));
            }
            if (update.getCallbackQuery().getData().equals("nextPage")) {
                telegramBotService.sendToClient(telegramBotService.nextPage(update));
            }
            if (update.getCallbackQuery().getData().startsWith("ChoosenProductId:")) {
                telegramBotService.sendToClientWithPhoto(telegramBotService.getChoosenProduct(update));
            }
            if (update.getCallbackQuery().getData().startsWith("productySizeId:")) {
                telegramBotService.sendToClientWithPhoto(telegramBotService.getProductSize(update));
            }
            if (update.getCallbackQuery().getData().startsWith("amount:")) {
                telegramBotService.sendToClient(telegramBotService.getAmount(update));
            }
            if (update.getCallbackQuery().getData().startsWith("DeletingFromBasket:")) {
                telegramBotService.sendToClient(telegramBotService.removeBasketItem(update));
            }
            if (update.getCallbackQuery().getData().equals("finishOrder")) {
                telegramBotService.sendToClient(telegramBotService.finishOrder(update));
            }
            if (update.getCallbackQuery().getData().equals("cancelOrder")) {
                telegramBotService.sendToClient(telegramBotService.cancelOrder(update));
            }
            if (update.getCallbackQuery().getData().startsWith("payType:")) {
                telegramBotService.sendToClient(telegramBotService.getPayType(update));
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
