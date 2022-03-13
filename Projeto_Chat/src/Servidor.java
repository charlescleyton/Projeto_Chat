//Trabalho de Sistemas distribuídos - BSI - Noturno 
//Alunos: Charles CLeyton Gomes Pereira e Filipe Lages dos Reis

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

public class Servidor extends Thread {

	private Socket conexao;

	public Servidor(Socket s) {
		conexao = s;
	}
//Declaração dos Vetores
	private static Vector<DataOutputStream> vet_Saida_Economia = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vet_Saida_Entretenimento = new Vector<DataOutputStream>();
	private static Vector<DataOutputStream> vet_Saida_Tecnologia = new Vector<DataOutputStream>();

	public static void main(String[] args) throws IOException {

		// cria socket de comunicação com os clientes na porta 8657;
		ServerSocket servidor = new ServerSocket(8657);

		// Classe de Execução
		while (true) {
			// espera conexão de algum cliente
			System.out.println("Esperando cliente se conectar...");
			Socket cx = servidor.accept();
			Thread t = new Servidor(cx);
			t.start();

			System.out.println("Cliente conectado!");
		}
	}

	
	public void run() {
		String msg_recebida; // lida do cliente
		String msg_enviada; // enviada ao cliente
		String nome_cliente; // Nome do Cliente
		String assunto; //Assunto escolhido

		BufferedReader entrada_cliente;

		try {
			entrada_cliente = new BufferedReader(new InputStreamReader(conexao.getInputStream()));

			DataOutputStream saida_cliente = new DataOutputStream(conexao.getOutputStream());
			// lê o nome do Cliente
			nome_cliente = entrada_cliente.readLine();

			// Envia retorno para cliente
			saida_cliente.writeBytes("<Servidor> : Olá " + nome_cliente + "! Data e Hora: "+dataAtual()+">\n");

			assunto = entrada_cliente.readLine();

			Integer i;
			Vector<DataOutputStream> v;

			switch (assunto) {
			case "1":
				vet_Saida_Economia.add(saida_cliente);
				v = vet_Saida_Economia;
				assunto = "Economia";
				break;

			case "2":
				vet_Saida_Entretenimento.add(saida_cliente);
				v = vet_Saida_Entretenimento;
				assunto = "Entretenimento";
				break;

			case "3":
				vet_Saida_Tecnologia.add(saida_cliente);
				v = vet_Saida_Tecnologia;
				assunto = "Tecnologia";
				break;

			default:
				vet_Saida_Tecnologia.add(saida_cliente);
				v = vet_Saida_Tecnologia;
				assunto = "Tecnologia";
			}
			
			i = 0;
			//Envia a mensagem recebida para todos do mesmo assunto
			while (i < v.size()) {
				v.get(i).writeBytes(" " + nome_cliente + " entrou no assunto: " + assunto + "! Data e Hora: "+dataAtual()+"\n");
				i = i + 1;
			}

			// Ler a mensagem recebida pelo Cliente enquanto nada é enviado
			msg_recebida = entrada_cliente.readLine();

			//Enquanto a mensagem for recebida não for nula ou finalizada
			while (msg_recebida != null && !(msg_recebida.trim().equals("")) && !(msg_recebida.startsWith("fim"))) {

				// Mostra Mensagem recebida pelo Console
				System.out.println(nome_cliente + ": " + msg_recebida+"\n");

			
				msg_enviada = nome_cliente + " :  " + msg_recebida + "! Data e Hora: "+dataAtual()+"\n";
				i = 0;

				//Envia a mensagem recebida para todos, exceto para o que enviou
				while (i < v.size()) {
					if (v.get(i) != saida_cliente) {
						// Envia retorno para Cliente
						v.get(i).writeBytes(msg_enviada);
					}
					i = i + 1;
				}
				msg_recebida = entrada_cliente.readLine();
			}
			
			i = 0;

			while (i < v.size()) {
				v.get(i).writeBytes(nome_cliente + " saiu " + assunto + "! Data e Hora: "+dataAtual()+">\n");
				i = i + 1;
			}
			
			i = 0;
			
			while (i < v.size()) {
				if (v.get(i) == saida_cliente) {
					v.remove(v.get(i));
					System.out.println("Cliente Desconectado");
				}
				i = i + 1;
			}
			conexao.close();
		} catch (IOException e) {
			e.printStackTrace();
		}}
	private String dataAtual() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
}
