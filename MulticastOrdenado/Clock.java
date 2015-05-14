/**
 * Autores:
 * Alexandre Braga Saldanha - R.A.: 408484
 * Daniel Miola - R.A.: 438340
 **/

package MulticastOrdenado;

public class Clock {
	private int time;
	private int incremento;
	
	/**
	 * Construtor
	 * @param init_clock O tempo inicial do clock
	 * @param incremento O incremento que será usado no clock
	 */
	public Clock(int init_clock, int incremento) {
		this.time = init_clock;
		this.incremento = incremento;
	}
	
	/**
	 * add()
	 * Adiciona 1 ao tempo do clock
	 */
	public void add() {
		time += incremento;
	}
	
	/**
	 * getTime()
	 * Retorna o tempo atual do clock
	 */
	public int getTime() {
		return time;
	}
	
	/**
	 * adjust()
	 * Ajusta o tempo do clock entre o maior entre o tempo atual e o tempo fornecido
	 * como parametro
	 * @param tempo O tempo para se comparar para ajustar o relogio
	 */
	public void adjust(int tempo) {
		if(tempo > time)
			time = tempo;
		time++;
	}
}	
