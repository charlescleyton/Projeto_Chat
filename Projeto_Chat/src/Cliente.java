//Trabalho de Sistemas distribuídos - BSI - Noturno 
//Alunos: Charles CLeyton Gomes Pereira e Filipe Lages dos Reis

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.security.auth.Subject;

public class Cliente extends Thread {

	private BufferedReader entrada;

	private static String dig_mensagem;

	public Cliente(BufferedReader i) {

		entrada = i;
		dig_mensagem = "Not Null";
	}

	public static void main(String[] args) throws UnknownHostException, IOException {

		System.out.println("Cliente Ativo!");

		String msg_digitada; // mensagem digitada
		String msg_recebida; // mensagem recebida
		String nome_cliente; // nome do cliente

		// cria o stream do teclado
		BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

		// solicita um nome para o cliente
		System.out.println("Informe o nome do cliente:");
		nome_cliente = teclado.readLine();

		// cria o socket de acesso ao server hostname na porta 8657
		Socket cliente = new Socket("localhost", 8657);
		System.out.println(nome_cliente + " entrou no chat!");

		// Envia a Mensagem
		DataOutputStream saida_servidor = new DataOutputStream(cliente.getOutputStream());
		// Recebe a Mensagem
		BufferedReader entrada_servidor = new BufferedReader(new InputStreamReader(cliente.getInputStream()));

		saida_servidor.writeBytes(nome_cliente + "\n");
		msg_recebida = entrada_servidor.readLine();
		System.out.println(msg_recebida);

		System.out.println("Escolha o Assunto:\n1 - Economia\n2 - Entretenimento\n3 - Tecnologia");

		msg_digitada = teclado.readLine();

		// envia a linha para o servidor
		saida_servidor.writeBytes(msg_digitada + '\n');

		// lê a linha para o servidor
		msg_recebida = entrada_servidor.readLine();

		// apresenta a linha do servidor na console
		System.out.println("Servidor: " + msg_recebida);

		// Inicializa a Thread que recebe a mensagem 
		Thread t = new Cliente(entrada_servidor);

		t.start();

		while (true) {
			// le uma linha do teclado
			msg_digitada = teclado.readLine();

			// testa se o chat deve ser finalizado
			if (msg_digitada.startsWith("fim") == true)
				break;

			// envia a linha para o servidor
			saida_servidor.writeBytes(msg_digitada + '\n');
		}

		// lê uma linha do servidor
		msg_recebida = entrada_servidor.readLine();

		if (msg_recebida != null) {
			System.out.println(msg_recebida);
		}
		// fecha o cliente
		cliente.close();
		System.out.println(nome_cliente + " saiu do chat!");
	}

	public void run() {
		try {
			// Verifica se o Chat foi encerrado
			while (dig_mensagem != null && !(dig_mensagem.trim().equals("")) && !(dig_mensagem.startsWith("fim"))) {
				System.out.println(entrada.readLine());
			}
			System.exit(0);
		} catch (IOException e) {
			System.exit(0);

		}

	}

}
