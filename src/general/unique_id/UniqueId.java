package general.unique_id;
import java.util.Calendar;

public class UniqueId {
	public static String getUniqueId() {
        Calendar calendar = Calendar.getInstance();
        Long date = calendar.get(Calendar.YEAR) * 10000000000000l;
        date += (calendar.get(Calendar.MONTH)+1) * 100000000000l;
        date += calendar.get(Calendar.DAY_OF_MONTH) * 1000000000l;
        date += calendar.get(Calendar.HOUR_OF_DAY)  * 10000000l;
        date += calendar.get(Calendar.MINUTE) * 100000;
        date += calendar.get(Calendar.SECOND) * 1000;
        String uniqueId = "ID" + date.toString();
        return uniqueId;
    }
}
