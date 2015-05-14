/**
 * Autores:
 * Alexandre Braga Saldanha - R.A.: 408484
 * Daniel Miola - R.A.: 438340
 **/

package MulticastOrdenado;

import java.util.Comparator;

public class MessageComparator implements Comparator<Message> {
	@Override
	public int compare(Message m1, Message m2) {
		return m1.compareTo(m2);
	}
}
