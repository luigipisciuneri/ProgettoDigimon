import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Execute;

public class MainDigimon {
	private static List<Partita> lPartita;
	private static int idpartita;

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Scanner scanner = new Scanner(System.in);
		Class.forName("com.mysql.cj.jdbc.Driver"); // in questo punto carichia nella JVM in esecuzione la nostra
													// libreria
		String password = "gigi"; // la vostra password
		String username = "root"; // la vostra username
		String url = "jdbc:mysql://localhost:3306/digimon?useUnicode=true&useLegacyDatetimeCode=false&serverTimezone=UTC&createDatabaseIfNotExist=true&allowPublicKeyRetrieval=true&useSSL=false";
		Connection connessione = DriverManager.getConnection(url, username, password);
		Statement statement = connessione.createStatement();
		String digimon1_p = null;
		String digimon2_p = null;
		String digimon3_p = null;
		System.out.println("Se vuoi inserire nella tabella digimon scrivi 1");
		int i = scanner.nextInt();
		scanner.nextLine();
		if (i == 1) {
			NewDigimon digimon = creazioneDigimon(scanner);
			insertDigimon(digimon, connessione);
			System.out.println(digimon);
		}

		System.out.println("Se vuoi creare una partita digita 1");
		int j = scanner.nextInt();
		scanner.nextLine();
		if (j == 1) {
			System.out.println("digita il nome del creatore della partita");
			String creatore = scanner.nextLine();		
			System.out.println("digita la password di accesso alla partita");
			String passwordPartitaCreatore = scanner.nextLine();
			String queryInsDatiCratore = "INSERT INTO partita (creatore_partita, password, digimon1_c, digimon2_c, digimon3_c) VALUES ('"
					+ creatore + "', '" + passwordPartitaCreatore + "', (select nome from digimon where owner ='"
					+ creatore + "' order by rand() limit 1) , (select nome from digimon where owner ='" + creatore
					+ "' order by rand() limit 1) , (select nome from digimon where owner ='" + creatore
					+ "' order by rand() limit 1));";
			PreparedStatement prepareStatement = connessione.prepareStatement(queryInsDatiCratore);
			prepareStatement.execute();
			System.out.println("digita il nome del partecitante alla partita");
			String partecipante = scanner.nextLine();
			System.out.println("Inserisci la password della partita");
			String passwordPartitaPartecipante = scanner.nextLine();
			if (passwordPartitaCreatore.equals(passwordPartitaPartecipante)) {
				System.out.println("Inseriti i dati dei digimon partecipanti");
				ResultSet nomeDigimonPartecipante1 = statement.executeQuery(
						"select nome  from digimon where owner ='" + partecipante + "' order by rand() limit 1;");
				while (nomeDigimonPartecipante1.next()) {
					digimon1_p = nomeDigimonPartecipante1.getString("nome");

				}

				ResultSet nomeDigimonPartecipante2 = statement.executeQuery(
						"select nome from digimon where owner ='" + partecipante + "' order by rand() limit 1;");
				while (nomeDigimonPartecipante2.next()) {
					digimon2_p = nomeDigimonPartecipante2.getString("nome");
				}

				ResultSet nomeDigimonPartecipante3 = statement.executeQuery(
						"select nome from digimon where owner ='" + partecipante + "' order by rand() limit 1;");
				while (nomeDigimonPartecipante3.next()) {
					digimon3_p = nomeDigimonPartecipante3.getString("nome");
				}

			//	System.out.println(digimon1_p + " - " + digimon2_p + " - " + digimon3_p);
				String queryInsDatiPartecipante = "update partita set partecipante='" + partecipante
						+ "', digimon1_p=?, digimon2_p=?, digimon3_p=? where creatore_partita=? and password=?";
				PreparedStatement prepareStatement1 = connessione.prepareStatement(queryInsDatiPartecipante);

				prepareStatement1.setString(1, digimon1_p);
				prepareStatement1.setString(2, digimon2_p);
				prepareStatement1.setString(3, digimon3_p);
				prepareStatement1.setString(4, creatore);
				prepareStatement1.setString(5, passwordPartitaPartecipante);
				prepareStatement1.executeUpdate();

			} else {
				
				
				System.out.println("Password non corretta");
			}

		}
		System.out.println("Vuoi iniziare una partita? digita 1");
		int inizio=scanner.nextInt();
		scanner.nextLine();
		if(inizio==1) {
			creazionePartita(scanner, connessione);
			popolaListaPartita(scanner, connessione);
			Partita partita = creazionePartita(scanner, connessione);
			creaSvolgimentoPartita(partita, connessione);
		}
		// inizio partita
		// faccio una select nella tabella partita e scelgo l'ultimo record.
		// inserisco nella tabella partita i dati del creatore perchè gioca per primo e attacca
		// inserisco nella tabella i dati del partecipante e difende.
		// faccio l'iserimento per tre round. quindi un ciclo
		// una volta che ho i dati calcolo il punteggio in base alle regole delle specifiche e vedo chi ha vinto
		
	}
	
	private static void creaSvolgimentoPartita(Partita partita, Connection connessione) throws SQLException {
		System.out.println("creazione mosse partita");
		Statement st1 = connessione.createStatement();
		ResultSet nomeDigimonPartecipante2 = st1.executeQuery(
				"select * from partita order by id_partita desc limit 1;");
		while (nomeDigimonPartecipante2.next()) {
			 idpartita = nomeDigimonPartecipante2.getInt("id_partita");
		}
		
		String queryInserimentoDigimon = "INSERT INTO svolgimento_partita (id_partita,Id_mossa, giocatore, attacco, difesa) VALUES (?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryInserimentoDigimon);
		prepareStatement.setInt(1, idpartita);
		for(int i=1; i<4; i++) {
			prepareStatement.setInt(2, i);
			prepareStatement.setString(3, partita.getDigimon1C());
			if(i%2==1) {
			prepareStatement.setInt(4, 'T');
			prepareStatement.setInt(5, 'F');
			}else {
				prepareStatement.setInt(4, 'F');
				prepareStatement.setInt(5, 'T');
			}
			prepareStatement.execute();
		}
	}
	
	private static void popolaListaPartita(Scanner scanner, Connection connessione) throws SQLException {
		lPartita=new ArrayList<Partita>();
		lPartita.add(creazionePartita(scanner,connessione));
		System.out.println("ListaPa"+lPartita);
		
	}
	
	private static Partita creazionePartita(Scanner scanner, Connection connessione) throws SQLException {
		Statement st = connessione.createStatement();
		ResultSet listaP = st.executeQuery(
				"select * from partita order by id_partita desc limit 1;");
		while (listaP.next()) {
			int idPartita=listaP.getInt("id_partita");
			String creatorePartita = listaP.getString("creatore_partita");
			String password = listaP.getString("password");
			String digimon1c = listaP.getString("digimon1_c");
			String digimon2c = listaP.getString("digimon2_c");
			String digimon3c = listaP.getString("digimon3_c");
			String partecipante = listaP.getString("partecipante");
			String digimon1p = listaP.getString("digimon1_p");
			String digimon2p = listaP.getString("digimon2_p");
			String digimon3p = listaP.getString("digimon3_p");
		/*	
			String queryInserimentoDigimon = "INSERT INTO svolgimento_partita (id_partita, Id_mossa, giocatore, attacco, difesa, giocatore) VALUES (?, ?, ?, ?, ?, ?);";
			PreparedStatement prepareStatement = connessione.prepareStatement(queryInserimentoDigimon);
			prepareStatement.setInt(1, idPartita);
			for()
			*/
			return new Partita(idPartita,creatorePartita, password, digimon1c, digimon2c, digimon3c, partecipante, digimon1p, digimon2p, digimon3p);
		}
		
		System.out.println(" tabella partita vuota");
		return null;
	}

	private static void insertDigimon(NewDigimon digimon, Connection connessione) throws SQLException {
		String queryInserimentoDigimon = "INSERT INTO digimon (owner, nome, hp, atk, def, res, evo, tipo) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryInserimentoDigimon);
		prepareStatement.setString(1, digimon.getOwner());
		prepareStatement.setString(2, digimon.getNome());
		prepareStatement.setInt(3, digimon.getHp());
		prepareStatement.setInt(4, digimon.getAtk());
		prepareStatement.setInt(5, digimon.getDef());
		prepareStatement.setInt(6, digimon.getRes());
		prepareStatement.setString(7, digimon.getEvo());
		prepareStatement.setString(8, digimon.getTipo());
		prepareStatement.execute();

	}

	private static NewDigimon creazioneDigimon(Scanner scanner) {

		System.out.println("Inserisci il prorietario");
		String owner = scanner.nextLine();
		System.out.println("Inserisci il nome del digimon");
		String nome = scanner.nextLine();
		System.out.println("Inserisci l'evoluzione");
		String evo = scanner.nextLine();
		System.out.println("Inserisci il tipo");
		String tipo = scanner.nextLine();
		System.out.println("calcolo in modo randow hp, atk,def,res");
		int hp = randInt(500, 600);
		int atk = randInt(100, 150);
		int def = randInt(10, 30);
		int res = randInt(5, 10);
		return new NewDigimon(owner, nome, hp, atk, def, res, evo, tipo);

	}

	public static int randInt(int min, int max) {
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

}
