/**
 * Autores:
 * Alexandre Braga Saldanha - R.A.: 408484
 * Daniel Miola - R.A.: 438340
 **/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.TreeSet;
import MulticastOrdenado.*;

public class P1 {

    private static Clock clock;
	private static int pid;
	private static int porta;
	private static TreeSet<Message> fila;
	private static int acks[];

    public static void main(String[] args) throws IOException {

        pid = 1;
		porta = 60011;
		fila = new TreeSet<Message>(new MessageComparator());
		acks = new int[999];
		for(int i = 0; i < 999; i++) acks[i] = 0;
		clock = new Clock(1, 1);
		
        server();
        try{ 
            Thread.sleep(10000); 
        }catch(Exception e){}
		
		for(int i = 0; i < 2; i++) {
			// Evento (envia uma mensagem em multicast), incrementa o clock
			clock.add();
			sendMessageMulticast(i, 0, clock.getTime());				
		}
    }

    /**
	 * sendMessageMulticast()
	 * Envia uma mensagem em multicast para o grupo
	 * @param msg O número da mensagem
	 * @param tipo O tipo da mensagem (0 = mensagem normal, 1 = mensagem ACK)
	 * @param time O tempo do relógio do processo no momento de envio do multicast
	 */
	public static void sendMessageMulticast(int msg, int tipo, int time) {
		client(60011, msg, tipo, time);
		client(60012, msg, tipo, time);
		client(60013, msg, tipo, time);
	}
	
	/**
	 * client()
	 * Thread para envio de mensagens
	 * @param destino A porta de destino da mensagem
	 * @param msg O número da mensagem
	 * @param tipo O tipo da mensagem (0 = mensagem normal, 1 = mensagem ACK)
	 * @param time O tempo do relógio do processo no momento de envio do multicast
	 */
	public synchronized static void client(final int destino, final int msg, final int tipo, final int time) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    String envio;
					
					/* Monta a mensagem a ser enviada, contendo o número da mensagem e o tempo de relógio
					 * do processo no momento do envio */
					
					// Mensagem normal
					if(tipo == 0)
						envio = "Message#" + msg + "#" + time;
					// ACK
					else
						envio = "ACK#" + msg + "#" + time;

                    // Envia a mensagem (ou ack) para todos os outros processos, 
					// inclusive o proprio
					// A marca de tempo da mensagem é a marca de tempo atual
					Socket s;
                    s = new Socket("localhost", destino);
                    BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream()));

                    out.write(envio);    
                    out.newLine();
                   	out.flush();
					
					/* Tirar os comentários aqui para visualizar quando o processo
					 * enviou uma mensagem 
					
					if(tipo == 0)
						System.out.println("Processo " + pid + " enviou a mensagem " + msg + " para o destino " + destino);
					else
					 	System.out.println("Processo " + pid + " enviou ACK da mensagem " + msg + " para o destino " + destino);
					*/

                	} catch (UnknownHostException e) {
                    	e.printStackTrace();
                	} catch (IOException e) {
                    	e.printStackTrace();
                 }
            }
        }).start();
    }
	
	/**
	 * server()
	 * Thread servidor para receber mensagens
	 */
	public synchronized static void server() {
        (new Thread() {
            @Override
            public void run() {
                ServerSocket ss;
                try {                
                    ss = new ServerSocket(60011);

					// Servidor fica ouvindo
                    while(true){ 
                        Socket s = ss.accept();

						// Coloca a thread para dormir para evitar concorrencia
                        try{ 
							int t = (int)(Math.random()*2000);
			            	Thread.sleep(t); 
			        	}catch(Exception e){}	

						// Trata a requisição em outra thread enquanto o servidor volta a ouvir
                        tratar(s);
						
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

	/**
	 * tratar()
	 * Trata uma requisição (recebimento e tratamento da mensagem)
	 * @param s O socket que recebeu a requisição
	 */
    public synchronized static void tratar(final Socket s) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    // Lê a mensagem
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));
								
                    String line = null;
					
					line = in.readLine();
						
					// Quebra a mensagem
					String lineQuebrada[] = line.split("#");
						
					// Evento (recebeu uma mensagem), ajusta o relogio interno
					int numeroMensagem = Integer.parseInt(lineQuebrada[1]);
					int timeMensagem = Integer.parseInt(lineQuebrada[2]);
					clock.adjust(timeMensagem);
						
					// Caso seja mensagem normal, ela é adicionada na fila
					if(lineQuebrada[0].equals("Message")){							
						Message msg = new Message(numeroMensagem, timeMensagem);
							
						// Evento interno(adicionar mensagem na fila), adiciona um ao clock
						clock.add();
							
						/* Retirar os comentarios para ver a fila depois de adicionar uma nova mensagem
						if(!fila.isEmpty()) {
							Iterator it = fila.iterator();
							while(it.hasNext()) {
								Message m = (Message) it.next();
								System.out.print(m.getNumeroMensagem() + " ");
							}
							System.out.println();
						}
						*/
						
						if(fila.add(msg)) {
							/* Tirar os comentários para visualizar quando uma mensagem é adicionada na fila
							System.out.println("Processo " + pid + " recebeu a mensagem " + numeroMensagem);
							*/
							
							// Manda um ACK em multicast da mensagem
							// Evento (envia uma mensagem em multicast), adiciona um ao clock
							clock.add();
							sendMessageMulticast(numeroMensagem, 1, clock.getTime());
						}
						else
							System.out.println("Erro ao inserir a mensagem " + numeroMensagem + " na fila");
					}
						
					// Caso seja um ACK
					else if (lineQuebrada[0].equals("ACK")) {							
						// Adiciona um aos ACKs recebidos
						// Evento interno (recebeu ACK e alterou o contador), adiciona um ao clock
						clock.add();
						
						acks[numeroMensagem]++;

						// Caso o primeiro da fila tenha recebido 3 ACKs, o remove da fila
						if(!fila.isEmpty() && acks[fila.first().getNumeroMensagem()] == 3) {
							// Evento interno (entregou a mensagem à aplicação), adiciona um ao clock
							clock.add();
								
							System.out.println("Processo " + pid + " recebeu 3 ACKs da mensagem " +
												fila.first().getNumeroMensagem() +  "(time: " + fila.first().getTimeStamp() + ") e liberou ela para o APP");
							fila.remove(fila.first());
							

							/* Retirar os comentários para visualizar a fila depois da remoção
							Iterator it = fila.iterator();
							while(it.hasNext()) {
								Message m = (Message) it.next();
								System.out.print(m.getNumeroMensagem() + " ");
							}
							System.out.println();
							*/
						}
					}

                } catch (UnknownHostException e) {
                    	e.printStackTrace();
                } catch (IOException e) {
                   	e.printStackTrace();
                }
            }
        }).start();
    }
}
