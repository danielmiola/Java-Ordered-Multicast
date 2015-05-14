/**
 * Autores:
 * Alexandre Braga Saldanha - R.A.: 408484
 * Daniel Miola - R.A.: 438340
 **/

package MulticastOrdenado;

import java.lang.Comparable;

public class Message implements Comparable<Message>{
	private int numero;
	private int timeStamp;
	
	/**
	 * Construtor
	 * @param numero O numero da mensagem
	 * @param timeStamp O tempo em que a mensagem foi enviada
	 */
	public Message(int numero, int timeStamp) {
		this.numero = numero;
		this.timeStamp = timeStamp;
	}
	
	/**
	 * getNumeroMensagem()
	 * Retorna o número da mensagem
	 */
	public int getNumeroMensagem() {
		return numero;
	}
	
	/**
	 * getTimeStamp()
	 * Retorna o tempo em que a mensagem foi enviada
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	public int compareTo(Message m2) {
		if(this.timeStamp < m2.timeStamp)
			return -1;
		
		if(this.timeStamp > m2.timeStamp)
			return 1;
		
		return 0;
	}
}
