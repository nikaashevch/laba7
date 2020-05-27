import java.util.regex.Pattern;

public class Utils {
    public static String checkNumber(String number){
        if(!Pattern.matches("4[045][0-9]{3}810[0-9][0-9]{11}", number)) throw new InvalidAccountNumberException();
        if(Pattern.matches("\\d{11}0000\\d{7}", number)) throw new InvalidAccountNumberException();
        if(Pattern.matches("\\d{15}0000000", number)) throw new InvalidAccountNumberException();
        return number;
    }
}
