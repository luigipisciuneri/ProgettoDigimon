
public class Partita {

	private int idPartita;
	private String creatorePartita;
	private String password;
	private String digimon1C;
	private String digimon2C;
	private String digimon3C;
	private String partecipante;
	private String digimon1P;
	private String digimon2P;
	private String digimon3P;
	
	

	
	public Partita(int idPartita, String creatorePartita, String password, String digimon1c, String digimon2c,
			String digimon3c, String partecipante, String digimon1p, String digimon2p, String digimon3p) {
		super();
		this.idPartita = idPartita;
		this.creatorePartita = creatorePartita;
		this.password = password;
		digimon1C = digimon1c;
		digimon2C = digimon2c;
		digimon3C = digimon3c;
		this.partecipante = partecipante;
		digimon1P = digimon1p;
		digimon2P = digimon2p;
		digimon3P = digimon3p;
	}
	public String getCreatorePartita() {
		return creatorePartita;
	}
	public void setCreatorePartita(String creatorePartita) {
		this.creatorePartita = creatorePartita;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getDigimon1C() {
		return digimon1C;
	}
	public void setDigimon1C(String digimon1c) {
		digimon1C = digimon1c;
	}
	public String getDigimon2C() {
		return digimon2C;
	}
	public void setDigimon2C(String digimon2c) {
		digimon2C = digimon2c;
	}
	public String getDigimon3C() {
		return digimon3C;
	}
	public void setDigimon3C(String digimon3c) {
		digimon3C = digimon3c;
	}
	public String getPartecipante() {
		return partecipante;
	}
	public void setPartecipante(String partecipante) {
		this.partecipante = partecipante;
	}
	public String getDigimon1P() {
		return digimon1P;
	}
	public void setDigimon1P(String digimon1p) {
		digimon1P = digimon1p;
	}
	public String getDigimon2P() {
		return digimon2P;
	}
	public void setDigimon2P(String digimon2p) {
		digimon2P = digimon2p;
	}
	public String getDigimon3P() {
		return digimon3P;
	}
	public void setDigimon3P(String digimon3p) {
		digimon3P = digimon3p;
	}

	@Override
	public String toString() {
		return "Partita [creatorePartita=" + creatorePartita + ", password=" + password + ", digimon1C=" + digimon1C
				+ ", digimon2C=" + digimon2C + ", digimon3C=" + digimon3C + ", partecipante=" + partecipante
				+ ", digimon1P=" + digimon1P + ", digimon2P=" + digimon2P + ", digimon3P=" + digimon3P + "]";
	}
	
	
	
}
