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

				// System.out.println(digimon1_p + " - " + digimon2_p + " - " + digimon3_p);
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
		int inizio = scanner.nextInt();
		scanner.nextLine();
		if (inizio == 1) {
			creazionePartita(scanner, connessione);
			popolaListaPartita(scanner, connessione);
			Partita partita = creazionePartita(scanner, connessione);
			creaSvolgimentoPartita(partita, connessione);
		}
		calcoloRisultatoC(scanner, connessione);
		System.out.println("Fatto riga 107");
		// inizio partita
		// faccio una select nella tabella partita e scelgo l'ultimo record.
		// inserisco nella tabella svolgimento_partita i dati del creatore perchè gioca
		// per primo e attacca
		// inserisco nella tabella svolgimento_partita i dati del partecipante e
		// difende.
		// faccio l'iserimento per tre round. quindi un ciclo
		// una volta che ho i dati calcolo il punteggio in base alle regole delle
		// specifiche e vedo chi ha vinto

	}

	private static void creaSvolgimentoPartita(Partita partita, Connection connessione) throws SQLException {
		System.out.println("creazione mosse partita");
		Statement st1 = connessione.createStatement();
		ResultSet nomeDigimonPartecipante2 = st1
				.executeQuery("select * from partita order by id_partita desc limit 1;");
		while (nomeDigimonPartecipante2.next()) {
			idpartita = nomeDigimonPartecipante2.getInt("id_partita");
		}

		String queryInserimentoDigimon = "INSERT INTO svolgimento_partita (id_partita,Id_mossa, giocatore_C, attacco_C, difesa_C, giocatore_P, attacco_P, difesa_P) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement prepareStatement = connessione.prepareStatement(queryInserimentoDigimon);
		prepareStatement.setInt(1, idpartita);
		for (int i = 1; i < 11; i++) {
			prepareStatement.setInt(2, i);
			int chiGiocaC = randInt(1, 3);
			int chiGiocaP = randInt(1, 3);
			// gioca per primo il creatore e attacca e si procede in modo alternato
			if (i % 2 == 1) {
				if (chiGiocaC == 1) {
					prepareStatement.setString(3, partita.getDigimon1C());
					prepareStatement.setString(4, "T");
					prepareStatement.setString(5, "F");
					if (chiGiocaP == 1) {
						prepareStatement.setString(6, partita.getDigimon1P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else if (chiGiocaP == 2) {
						prepareStatement.setString(6, partita.getDigimon2P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else {
						prepareStatement.setString(6, partita.getDigimon3P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");

					}
				} else if (chiGiocaC == 2) {
					prepareStatement.setString(3, partita.getDigimon2C());
					prepareStatement.setString(4, "T");
					prepareStatement.setString(5, "F");
					if (chiGiocaP == 1) {
						prepareStatement.setString(6, partita.getDigimon1P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else if (chiGiocaP == 2) {
						prepareStatement.setString(6, partita.getDigimon2P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else {
						prepareStatement.setString(6, partita.getDigimon3P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");

					}
				} else {
					prepareStatement.setString(3, partita.getDigimon3C());
					prepareStatement.setString(4, "T");
					prepareStatement.setString(5, "F");
					if (chiGiocaP == 1) {
						prepareStatement.setString(6, partita.getDigimon1P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else if (chiGiocaP == 2) {
						prepareStatement.setString(6, partita.getDigimon2P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");
					} else {
						prepareStatement.setString(6, partita.getDigimon3P());
						prepareStatement.setString(7, "F");
						prepareStatement.setString(8, "T");

					}
				}
			} else if (chiGiocaP == 1) {
				prepareStatement.setString(6, partita.getDigimon1P());
				prepareStatement.setString(7, "T");
				prepareStatement.setString(8, "F");
				if (chiGiocaC == 1) {
					prepareStatement.setString(3, partita.getDigimon1C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				} else if (chiGiocaC == 2) {
					prepareStatement.setString(3, partita.getDigimon2C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");

				} else {
					prepareStatement.setString(3, partita.getDigimon3C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				}

			} else if (chiGiocaP == 2) {
				prepareStatement.setString(6, partita.getDigimon2P());
				prepareStatement.setString(7, "T");
				prepareStatement.setString(8, "F");
				if (chiGiocaC == 1) {
					prepareStatement.setString(3, partita.getDigimon1C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				} else if (chiGiocaC == 2) {
					prepareStatement.setString(3, partita.getDigimon2C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");

				} else {
					prepareStatement.setString(3, partita.getDigimon3C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				}
			} else {
				prepareStatement.setString(6, partita.getDigimon3P());
				prepareStatement.setString(7, "T");
				prepareStatement.setString(8, "F");
				if (chiGiocaC == 1) {
					prepareStatement.setString(3, partita.getDigimon1C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				} else if (chiGiocaC == 2) {
					prepareStatement.setString(3, partita.getDigimon2C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");

				} else {
					prepareStatement.setString(3, partita.getDigimon3C());
					prepareStatement.setString(4, "F");
					prepareStatement.setString(5, "T");
				}
			}

			prepareStatement.execute();
		}

		// System.out.println("idpartita "+idpartita);

	}

	// adesso devo calcolare il punteggio e vedere chi ha vinto
	// questo lo devo fare per ogni mossa.
	public static void calcoloRisultatoC(Scanner scanner, Connection connessione) throws SQLException {
		int conta = 0;
		Statement st1 = connessione.createStatement();
		Statement st2 = connessione.createStatement();
		System.out.println("id partita " + idpartita);
		ResultSet conteggioMosse = st1.executeQuery(
				"select count(*) as conteggio from svolgimento_partita where id_partita='" + idpartita + "';");
		while (conteggioMosse.next()) {
			conta = conteggioMosse.getInt("conteggio");
			System.out.println("conta mosse " + conta);
		}
		for (int i = 1; i < conta + 1; i++) {
			if (i % 2 == 1) {
				ResultSet datiPartitaCreatore = st1.executeQuery(
						"select d.* from digimon d, partita p where d.owner=p.creatore_partita and p.id_partita='"
								+ idpartita
								+ "' and d.nome in (select giocatore_C from svolgimento_partita where p.id_partita='"
								+ idpartita + "' and id_mossa='" + i + "');");
				while (datiPartitaCreatore.next()) {
					String ownerC = datiPartitaCreatore.getString("owner");
					String nomeC = datiPartitaCreatore.getString("nome");
					int hpC = datiPartitaCreatore.getInt("hp");
					int atkC = datiPartitaCreatore.getInt("atk");
					int defC = datiPartitaCreatore.getInt("def");
					int resC = datiPartitaCreatore.getInt("res");
					String evoC = datiPartitaCreatore.getString("evo");
					String tipoC = datiPartitaCreatore.getString("tipo");

					// inizio calcolo dei valori
					System.out.println("Creatore " + ownerC + " - " + nomeC + " - " + hpC + " - " + atkC);
				}
			} else {
				ResultSet datiPartitaPartecipante = st2.executeQuery(
						"select d.* from digimon d, partita p where d.owner=p.partecipante and p.id_partita='"
								+ idpartita
								+ "' and d.nome in (select giocatore_P from svolgimento_partita where p.id_partita='"
								+ idpartita + "' and id_mossa='" + i + "');");
				while (datiPartitaPartecipante.next()) {
					String ownerP = datiPartitaPartecipante.getString("owner");
					String nomeP = datiPartitaPartecipante.getString("nome");
					int hpP = datiPartitaPartecipante.getInt("hp");
					int atkP = datiPartitaPartecipante.getInt("atk");
					int defP = datiPartitaPartecipante.getInt("def");
					int resP = datiPartitaPartecipante.getInt("res");
					String evoP = datiPartitaPartecipante.getString("evo");
					String tipoP = datiPartitaPartecipante.getString("tipo");

					// inizio calcolo dei valori
					System.out.println("Partecipante " + ownerP + " - " + nomeP + " - " + hpP + " - " + atkP);
				}

			}
		}

	}

	// creare i seguenti metodi
	// confronta tipo
	// calcolo nuovo hp
	// update hp nella tabella svolgimento_partita
	// calcola vincitore
	// evoluzione digimon

	public static void confrontaTipoNuovoHp(int idpartita, String tipoCreatore, String tipoPartecipante,
			Connection connessione, Scanner scanner, int hpC, int hpP, int atkC, int atkP,
			int defC, int defP, int resC, int resP) throws SQLException {
		Statement st3 = connessione.createStatement();
		double hpappoggio;
		System.out.println("id partita " + idpartita);
		ResultSet querySvolPartita = st3
				.executeQuery("select * from svolgimento_partita where id_partita='" + idpartita + "';");
		while (querySvolPartita.next()) {
			String attaccoC = querySvolPartita.getString("attacco_c");
			String difesaC = querySvolPartita.getString("difesa_c");
			String attaccoP = querySvolPartita.getString("attacco_p");
			String difesaP = querySvolPartita.getString("difesa_p");
			int idmossa = querySvolPartita.getInt("id_mossa");
			if (attaccoC.contentEquals("T") && tipoCreatore.contentEquals("terra") && attaccoP.contentEquals("F")) {
				if (tipoPartecipante.contentEquals("terra") || tipoPartecipante.contentEquals("acqua")) {
					hpappoggio = (atkC - defP);
					hpappoggio = hpappoggio - (hpappoggio * resP) / 100;
					hpP = (int) (hpP - Math.round(hpappoggio));
				} else if (tipoPartecipante.contentEquals("aria")) { // danno favorevole
					hpappoggio = (atkC * (resP / 100) - defP);
					hpP = (int) (hpP - Math.round(hpappoggio));
				} else if (tipoPartecipante.contentEquals("fuoco")) {// danno sfavorevole
					hpappoggio = (atkC * 2 - defP);
					hpP = (int) (hpP - Math.round(hpappoggio));
				}
				aggiornaHp(idpartita, idmossa, hpP, connessione);// fare update su tabella svolgiPartita
			} else if (attaccoC.contentEquals("T") && tipoCreatore.contentEquals("fuoco") && attaccoP.contentEquals("F")) {
				if (tipoPartecipante.contentEquals("aria") || tipoPartecipante.contentEquals("fuoco")) {
					hpappoggio = (atkC - defP);
					hpappoggio = hpappoggio - (hpappoggio * resP) / 100;
					hpP = (int) (hpP - Math.round(hpappoggio));
				} else if (tipoPartecipante.contentEquals("terra")) {
					hpappoggio = (atkC * (resP / 100) - defP);
					hpP = (int) (hpP - Math.round(hpappoggio));
				} else if (tipoPartecipante.contentEquals("acqua")) {
					hpappoggio = (atkC * 2 - defP);
					hpP = (int) (hpP - Math.round(hpappoggio));
				}
				aggiornaHp(idpartita, idmossa, hpP, connessione);
			}else if (attaccoC.contentEquals("T") && tipoCreatore.contentEquals("aria")&& attaccoP.contentEquals("F")) {
					if (tipoPartecipante.contentEquals("aria") || tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkC - defP);
						hpappoggio = hpappoggio - (hpappoggio * resP) / 100;
						hpP = (int) (hpP - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("acqua")) {
						hpappoggio = (atkC * (resP / 100) - defP);
						hpP = (int) (hpP - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("terra")) {
						hpappoggio = (atkC * 2 - defP);
						hpP = (int) (hpP - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpP, connessione);
			} else if (attaccoC.contentEquals("T") && tipoCreatore.contentEquals("acqua") && attaccoP.contentEquals("F")) {
					if (tipoPartecipante.contentEquals("acqua") || tipoPartecipante.contentEquals("terra")) {
						hpappoggio = (atkC - defP);
						hpappoggio = hpappoggio - (hpappoggio * resP) / 100;
						hpP = (int) (hpP - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkC * (resP / 100) - defP);
						hpP = (int) (hpP - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("aria")) {
						hpappoggio = (atkC * 2 - defP);
						hpP = (int) (hpP - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpP, connessione);
			} else if (attaccoC.contentEquals("F") && tipoPartecipante.contentEquals("acqua") && attaccoP.contentEquals("T")) {
					if (tipoPartecipante.contentEquals("acqua") || tipoPartecipante.contentEquals("terra")) {
						hpappoggio = (atkP - defC);
						hpappoggio = hpappoggio - (hpappoggio * resC) / 100;
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkP * (resC / 100) - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("aria")) {
						hpappoggio = (atkP * 2 - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpC, connessione);
				} else if (attaccoC.contentEquals("F") && tipoPartecipante.contentEquals("fuoco") && attaccoP.contentEquals("T")) {
					if (tipoPartecipante.contentEquals("aria") || tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkP - defC);
						hpappoggio = hpappoggio - (hpappoggio * resC) / 100;
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("terra")) {
						hpappoggio = (atkP * (resC / 100) - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("acqua")) {
						hpappoggio = (atkP * 2 - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpC, connessione);
				} else if (attaccoC.contentEquals("F") && tipoPartecipante.contentEquals("aria") && attaccoP.contentEquals("T")) {
					if (tipoPartecipante.contentEquals("aria") || tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkP - defC);
						hpappoggio = hpappoggio - (hpappoggio * resC) / 100;
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("acqua")) {
						hpappoggio = (atkP * (resC / 100) - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("terrra")) {
						hpappoggio = (atkP * 2 - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpC, connessione);
				} else if (attaccoC.contentEquals("F") && tipoPartecipante.contentEquals("terra") && attaccoP.contentEquals("T")) {
					if (tipoPartecipante.contentEquals("acqua") || tipoPartecipante.contentEquals("terra")) {
						hpappoggio = (atkP - defC);
						hpappoggio = hpappoggio - (hpappoggio * resC) / 100;
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("aria")) {
						hpappoggio = (atkP * (resC / 100) - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					} else if (tipoPartecipante.contentEquals("fuoco")) {
						hpappoggio = (atkP * 2 - defC);
						hpC = (int) (hpC - Math.round(hpappoggio));
					}
					aggiornaHp(idpartita, idmossa, hpC, connessione);
				}

			}

		}

	
	
	public static void aggiornaHp(int idpartita, int idmossa, int hp, Connection connessione) throws SQLException {
		String queryAggiornamentoTabSvolPartita = "update svolgimento_partita set punteggio_hp_dif= ? where id_partita=? and id_mossa=?";
		PreparedStatement prepareStatement1 = connessione.prepareStatement(queryAggiornamentoTabSvolPartita);
		prepareStatement1.setInt(1, hp);
		prepareStatement1.setInt(2, idpartita);
		prepareStatement1.setInt(3, idmossa);
		prepareStatement1.executeUpdate();
	}

	private static void popolaListaPartita(Scanner scanner, Connection connessione) throws SQLException {
		lPartita = new ArrayList<Partita>();
		lPartita.add(creazionePartita(scanner, connessione));
		System.out.println("ListaPa" + lPartita);

	}

	private static Partita creazionePartita(Scanner scanner, Connection connessione) throws SQLException {
		Statement st = connessione.createStatement();
		ResultSet listaP = st.executeQuery("select * from partita order by id_partita desc limit 1;");
		while (listaP.next()) {
			int idPartita = listaP.getInt("id_partita");
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
			 * String queryInserimentoDigimon =
			 * "INSERT INTO svolgimento_partita (id_partita, Id_mossa, giocatore, attacco, difesa, giocatore) VALUES (?, ?, ?, ?, ?, ?);"
			 * ; PreparedStatement prepareStatement =
			 * connessione.prepareStatement(queryInserimentoDigimon);
			 * prepareStatement.setInt(1, idPartita); for()
			 */
			return new Partita(idPartita, creatorePartita, password, digimon1c, digimon2c, digimon3c, partecipante,
					digimon1p, digimon2p, digimon3p);
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
		System.out.println("calcolo in modo random hp, atk,def,res");
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

/*
 
  CREATE TABLE DIGIMON(
owner VARCHAR(45) NOT NULL,
nome VARCHAR(45) NOT NULL, 
hp   INT,
atk  INT,
def  INT,
res  INT,
evo  VARCHAR(45) NOT NULL DEFAULT 'BASE',
tipo VARCHAR(45),
PRIMARY KEY (`owner`,`nome`)
);

------------------------------------------------

CREATE TABLE PARTITA (
id_partita INT NOT NULL AUTO_INCREMENT,
creatore_partita VARCHAR(45),
password VARCHAR(8),
digimon1_c VARCHAR(45),
digimon2_c VARCHAR(45),
digimon3_c VARCHAR(45),
partecipante VARCHAR(45),
digimon1_p VARCHAR(45),
digimon2_p VARCHAR(45),
digimon3_p VARCHAR(45),
PRIMARY KEY (`id_partita`),
FOREIGN KEY (`creatore_partita`) REFERENCES DIGIMON (`owner`),
//FOREIGN KEY (`partecipante`) REFERENCES DIGIMON (`owner`)
);

-----------------------------------

create table svolgimento_partita(
id_partita int ,
id_mossa   int ,
giocatore_C varchar(45),
attacco_C   varchar(1),
difesa_C    varchar(1),
giocatore_P varchar(45),
attacco_P   varchar(1),
difesa_P    varchar(1),
vincitore varchar(45),
punteggio_hp_dif int,
FOREIGN KEY (`id_partita`) REFERENCES partita (`id_partita`)
);

-------------------------------------


INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo1','Bommon',532,123,25,10,'livello_primario','terra');
INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo1','Commandramon',581,126,18,5,'intermedio_campione','fuoco');
INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo1','Missimon',559,113,11,5,'primo_stato','acqua');
INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo2','Botamon',553,120,18,8,'livello_primario','terra');
INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo2','Commandramon',501,127,12,5,'intermedio_campione','fuoco');
INSERT INTO `DIGIMON` (`owner`,`nome`,`hp`,`atk`,`def`,`res`,`evo`,`tipo`) VALUES ('gruppo2','Koromon',569,136,15,5,'primo_stato','acqua');

  
 */
