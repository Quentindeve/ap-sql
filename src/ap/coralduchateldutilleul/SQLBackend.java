package ap.coralduchateldutilleul;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Optional;
import java.util.OptionalInt;

public class SQLBackend {
	private Connection dbConnection;
	
	public SQLBackend(String serverIp, String username, String password) throws SQLException {
		this.dbConnection = DriverManager.getConnection(serverIp, username, password);
	}
	
	public void insertTuple(int responsibleId, String descriptionTache, int priority) throws SQLException {
		String request = String.format(
				"INSERT INTO taches(id_responsable, description_tache, id_priorite, id_statut) VALUES\n"
				+ "(%d, \'%s\', %d, \'2\');"
				, responsibleId, descriptionTache, priority);
		this.dbConnection.beginRequest();
		Statement stmt = this.dbConnection.createStatement();
		stmt.executeUpdate(request);
		this.dbConnection.endRequest();
	}
	
	public ResultSet requestTaches(Optional<TaskState> taskState, OptionalInt responsibleId, OptionalInt priority) throws SQLException {
		String request = "SELECT * FROM taches";
		boolean alreadyWhere = false;
		
		if (taskState.isPresent()) {
			request += "\n\tWHERE id_statut = " + taskState.get().id;
			alreadyWhere = true;
		}
		
		if (responsibleId.isPresent()) {
			if (alreadyWhere) {
				request += "\n\tAND id_responsable = " + responsibleId.getAsInt();
			}
			else {
				request += "\n\tWHERE id_responsable = " + responsibleId.getAsInt();
				alreadyWhere = true;
			}
		}
		
		if (priority.isPresent()) {
			if (alreadyWhere) {
				request += "\n\tAND id_priorite = " + priority.getAsInt();
			}
			else {
				request += "\n\tWHERE id_priorite = " + priority.getAsInt();
				alreadyWhere = true;
			}
		}
		request += ";";
		System.out.println(request);
		this.dbConnection.beginRequest();
		Statement stmt = this.dbConnection.createStatement();
		ResultSet result = stmt.executeQuery(request);
		this.dbConnection.endRequest();
		return result;
	}
	
	public ResultSet getAll(String table) throws SQLException {
		String request = String.format("SELECT * FROM `%s`", table);
		this.dbConnection.beginRequest();
		Statement stmt = this.dbConnection.createStatement();
		ResultSet result = stmt.executeQuery(request);
		this.dbConnection.endRequest();
		return result;
	}
	
	public HashMap<Integer, String> getResponsables() {
		try {
			ResultSet set = this.getAll("responsables");
			HashMap<Integer, String> map = new HashMap<>();
			while (set.next()) {
				map.put(set.getInt("id"), set.getString("nom_prenom"));
			}
			return map;
		} catch (Exception e) {
			System.out.println("Ta gueule");
			System.exit(69);
			return null;
		}
	}

	public HashMap<String, Integer> getResponsablesId() {
		try {
			ResultSet set = this.getAll("responsables");
			HashMap<String, Integer> map = new HashMap<>();
			while (set.next()) {
				map.put(set.getString("nom_prenom"), set.getInt("id"));
			}
			return map;
		} catch (Exception e) {
			System.out.println("Ta gueule");
			System.exit(69);
			return null;
		}
	}
	
	public HashMap<String, Integer> getStatutId() {
		try {
			ResultSet set = this.getAll("statut_tache");
			HashMap<String, Integer> map = new HashMap<>();
			while (set.next()) {
				map.put(set.getString("description"), set.getInt("id"));
			}
			return map;
		} catch (Exception e) {
			System.out.println("Ta gueule");
			System.exit(69);
			return null;
		}
	}
	
	/*
	 * Renvoie sous forme {
	 * 	id,
	 *  description_tache
	 *  responsable,
	 *  statut
	 *  priorite
	 * }
	 */
	public ResultSet getTaches(OptionalInt priority, OptionalInt responsibleId, OptionalInt status) throws SQLException {
		boolean where = false;
		String request = "SELECT taches.id, taches.description_tache, responsables.nom_prenom AS responsable, statut_tache.description  AS statut, priorite.description AS priorite FROM taches\n";
		
		request +=   "\tINNER JOIN responsables\n"
				   + "\tON taches.id_responsable = responsables.id\n"
				   + "\tINNER JOIN priorite\n"
				   + "\tON taches.id_priorite = priorite.id\n"
				   + "\tINNER JOIN statut_tache\n"
				   + "\tON taches.id_statut = statut_tache.id\n";
		
		if (responsibleId.isPresent()) {
			where = true;
			request += "\tWHERE taches.id_responsable = " + responsibleId.getAsInt() + "\n";
		}
		
		if (priority.isPresent()) {
			if (where) {
				request += "\tAND taches.id_priorite = " + priority.getAsInt() + "\n";
			}
			else {
				request += "\tWHERE taches.id_priorite = " + priority.getAsInt() + "\n";
				where = true;
			}
		}
		
		if (status.isPresent()) {
			if (where) {
				request += "\tAND taches.id_statut = " + status.getAsInt() + "\n";
			}
			else {
				request += "\tWHERE taches.id_priorite = " + status.getAsInt() + "\n";
				where = true;
			}
		}
		
		request += ";";
		
		System.out.println(request);
		this.dbConnection.beginRequest();
		Statement stmt = this.dbConnection.createStatement();
		ResultSet result = stmt.executeQuery(request);
		this.dbConnection.endRequest();
		return result;
	}
	
	public int modifyTache(int id, int newPriority, int newResponsible, int newStatus) {
		String request = 
				"UPDATE taches\n" + 
				"SET id_priorite = %s, id_responsable = %s, id_statut = %s\n" +
				"WHERE id = %s";
		try {
			this.dbConnection.beginRequest();
			Statement stmt = this.dbConnection.createStatement();
			int result = stmt.executeUpdate(String.format(request, newPriority, newResponsible, newStatus, id));
			this.dbConnection.endRequest();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public int deleteTache(int id) {
		String request =
				"DELETE FROM taches\n" +
				"WHERE id = %s";
		try {
			 this.dbConnection.beginRequest();
			 Statement stmt = this.dbConnection.createStatement();
			 int result = stmt.executeUpdate(String.format(request, id));
			 this.dbConnection.endRequest();
			 return result;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}