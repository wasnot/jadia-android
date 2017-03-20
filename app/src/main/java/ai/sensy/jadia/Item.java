package ai.sensy.jadia;

import android.graphics.Color;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

/**
 * Created by akihiroaida on 2017/03/15.
 */

public class Item {

    int color;
    public String message;
    public String messageId;
    public long sendDate;

    public boolean moreTwoLines = false;
    public boolean isExpanded = false;

    @Override
    public String toString() {
        return message;
    }


    public Item() {
        this.color = Color.parseColor("#FF4081");
        this.messageId = RandomString.nextSessionId();
        this.message = RandomString.nextId();
        this.sendDate = new Date().getTime();
    }
    public Item(int color) {
        this.color =color;
        this.messageId = RandomString.nextSessionId();
        this.message = RandomString.nextId();
        this.sendDate = new Date().getTime();
    }

    static class RandomString {
        private static final char[] symbols;

        static {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch) {
                tmp.append(ch);
            }
            for (char ch = 'a'; ch <= 'z'; ++ch) {
                tmp.append(ch);
            }
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();
        private final char[] buf;

        RandomString() {
            Random r = new Random();
            int length = r.nextInt(99) + 1;
            if (length < 1) {
                throw new IllegalArgumentException("length < 1: " + length);
            }
            buf = new char[length];
        }

        String nextString() {
            for (int idx = 0; idx < buf.length; ++idx) {
                buf[idx] = symbols[random.nextInt(symbols.length)];
            }
            return new String(buf);
        }

        static String nextSessionId() {
            return new BigInteger(130, new SecureRandom()).toString(32);
        }

        static String nextId() {
            return new RandomString().nextString();
        }
    }
}
class HeaderItem extends Item{

}
class FooterItem extends Item{
    int space = 0;
}
