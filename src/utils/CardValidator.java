package utils;

public class CardValidator {

    // Проверка номера карты (16 цифр + алгоритм Луна)
    public static boolean isValidCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}") && luhnCheck(cardNumber);
    }

    // Проверка CVV (3 цифры)
    public static boolean isValidCVV(String cvv) {
        return cvv.matches("\\d{3}");
    }

    // Проверка срока действия (MM/YY)
    public static boolean isValidExpiryDate(String expiryDate) {
        return expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}");
    }

    // Алгоритм Луна для проверки номера карты
    private static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}
