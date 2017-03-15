package ai.sensy.jadia;

/**
 * Created by akihiroaida on 2017/03/15.
 */

public class Item {

    public String message;
    public String messageId;
    public long sendDate;

    public boolean moreTwoLines = false;
    public boolean isExpanded = false;

    @Override
    public String toString() {
        return message;
    }
}
