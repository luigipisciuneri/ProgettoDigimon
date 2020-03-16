
public class NewDigimon {

	private String owner;
	private String nome;
	private int hp;
	private int atk;
	private int def;
	private int res;
	private String evo;
	private String tipo;
	
	
	public NewDigimon(String owner, String nome, int hp, int atk, int def, int res, String evo, String tipo) {
		this.owner = owner;
		this.nome = nome;
		this.hp = hp;
		this.atk = atk;
		this.def = def;
		this.res = res;
		this.evo = evo;
		this.tipo = tipo;
	}


	@Override
	public String toString() {
		return "NewDigimon [owner=" + owner + ", nome=" + nome + ", hp=" + hp + ", atk=" 
	                        + atk + ", def=" + def + ", res=" + res + ", evo=" + evo + ", tipo=" + tipo + "]";
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public int getHp() {
		return hp;
	}


	public void setHp(int hp) {
		this.hp = hp;
	}


	public int getAtk() {
		return atk;
	}


	public void setAtk(int atk) {
		this.atk = atk;
	}


	public int getDef() {
		return def;
	}


	public void setDef(int def) {
		this.def = def;
	}


	public int getRes() {
		return res;
	}


	public void setRes(int res) {
		this.res = res;
	}


	public String getEvo() {
		return evo;
	}


	public void setEvo(String evo) {
		this.evo = evo;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
	
	
	
}
